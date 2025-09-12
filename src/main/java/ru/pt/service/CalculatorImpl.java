package ru.pt.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.pt.domain.Lob;
import ru.pt.domain.Product;
import ru.pt.domain.ProductVersion;
import ru.pt.repository.LobRepository;
import ru.pt.repository.ProductRepository;
import ru.pt.repository.ProductVersionRepository;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import ru.pt.hz.PeriodUtils;
import ru.pt.domain.pv.Bundle;
import ru.pt.domain.pv.Cover;

@Service
public class CalculatorImpl {
    private final ProductRepository productRepository;
    private final LobRepository lobRepository;
    private final ProductVersionRepository productVersionRepository;
    
    private static ObjectMapper objectMapper = new ObjectMapper();

    public CalculatorImpl(ObjectMapper objectMapper, ProductRepository productRepository, LobRepository lobRepository, ProductVersionRepository productVersionRepository) {
        this.productRepository = productRepository;
        this.lobRepository = lobRepository;
        this.productVersionRepository = productVersionRepository;
        CalculatorImpl.objectMapper = objectMapper;
    }


    public  ObjectNode preCalculate(String requestBody) {

        try {
            OffsetDateTime startDate1;
            OffsetDateTime issueDate;
    
            ObjectNode req = (ObjectNode) objectMapper.readTree(requestBody);

            issueDate = parseDate(req.path("issueDate").asText(null));
            startDate1 = parseDate(req.path("startDate").asText(null));
            OffsetDateTime endDate = parseDate(req.path("endDate").asText(null));
            String productCode = req.path("product").path("code").asText(null);

            Product product = productRepository.findByCodeAndIsDeletedFalse(productCode)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productCode));

            ProductVersion productVersion = productVersionRepository.findByProductIdAndVersionNo(product.getId(), product.getDevVersionNo())
                .orElseThrow(() -> new IllegalArgumentException("Product version not found: " + product.getDevVersionNo()));
            
            Lob lob = lobRepository.findByCodeAndIsDeletedFalse(product.getLob())
                .orElseThrow(() -> new IllegalArgumentException("LOB not found: " + product.getLob()));

            if (issueDate == null) {
                issueDate = OffsetDateTime.now();
                req.put("issueDate", format(issueDate));
            }

            // 1) Activation period handling
            JsonNode activationDelay = productVersion.getProduct().path("activationDelay");
            String activationDelay_listOfPeriods = activationDelay.path("listOfPeriods").asText(null);
            String[] activationDelay_listOfPeriods_array = activationDelay_listOfPeriods.split(",");
            String activationDelay_minPeriod = activationDelay.path("minPeriod").asText(null);
            String activationDelay_maxPeriod = activationDelay.path("maxPeriod").asText(null);

            JsonNode policyTerm = productVersion.getProduct().path("policyTerm");
            String policyTerm_listOfPeriods = policyTerm.path("listOfPeriods").asText(null);
            String[] policyTerm_listOfPeriods_array = policyTerm_listOfPeriods.split(",");
            String policyTerm_minPeriod = policyTerm.path("minPeriod").asText(null);
            String policyTerm_maxPeriod = policyTerm.path("maxPeriod").asText(null);
            //JsonNode covers1 = productVersion.getProduct().path("covers");

            // 1) Activation period handling
            //JsonNode activationList = activationDelay.path("listOfPeriods");

            if (activationDelay_listOfPeriods_array.length == 1) {
                String def = activationDelay_listOfPeriods_array[0];
                startDate1 = addPeriod(issueDate, def);
                req.put("startDate", format(startDate1));
                req.put("activationDelay", def);
            } else if (activationDelay_listOfPeriods_array.length > 0) {
                String period = calculatePeriodBetweenDates(issueDate, startDate1);
                req.put("activationDelay", period);
                if (!Arrays.asList(activationDelay_listOfPeriods_array).contains(period)) {
                    throw new IllegalArgumentException("Period " + period + " is not in the list of periods");
                }
            } else {
                String period = calculatePeriodBetweenDates(issueDate, startDate1);
                req.put("activationDelay", period);

                if (activationDelay_minPeriod != null && !activationDelay_minPeriod.isEmpty()) 
                {
                    if ( PeriodUtils.comparePeriods(parsePeriod(period),  parsePeriod(activationDelay_minPeriod)) < 0) {
                        throw new IllegalArgumentException("Period " + period + " is less than the minimum period");
                    }
                } 
                if (activationDelay_maxPeriod != null && !activationDelay_maxPeriod.isEmpty()) 
                {
                    if ( PeriodUtils.comparePeriods(parsePeriod(period),  parsePeriod(activationDelay_maxPeriod)) < 0) {
                        throw new IllegalArgumentException("Period " + period + " is greater than the maximum period");
                    }
                }
            }
            
            // 2) Policy term handling
            if (policyTerm_listOfPeriods_array.length == 1) {
                String def = policyTerm_listOfPeriods_array[0];
                endDate = addPeriod(startDate1, def);
                req.put("endDate", format(endDate));
                req.put("policyTerm", def);
            } else if (policyTerm_listOfPeriods_array.length > 0) {
                String period = calculatePeriodBetweenDates(startDate1, endDate);
                req.put("policyTerm", period);
                if (!Arrays.asList(policyTerm_listOfPeriods_array).contains(period)) {
                    throw new IllegalArgumentException("Period " + period + " is not in the list of policy term periods");
                }
            } else {
                String period = calculatePeriodBetweenDates(startDate1, endDate);
                req.put("policyTerm", period);

                if (policyTerm_minPeriod != null && !policyTerm_minPeriod.isEmpty()) 
                {
                    if (PeriodUtils.comparePeriods(parsePeriod(period), parsePeriod(policyTerm_minPeriod)) < 0) {
                        throw new IllegalArgumentException("Policy term period " + period + " is less than the minimum period");
                    }
                } 
                if (policyTerm_maxPeriod != null && !policyTerm_maxPeriod.isEmpty()) 
                {
                    if (PeriodUtils.comparePeriods(parsePeriod(period), parsePeriod(policyTerm_maxPeriod)) > 0) {
                        throw new IllegalArgumentException("Policy term period " + period + " is greater than the maximum period");
                    }
                }
            }

           
            JsonNode pv_packages = productVersion.getProduct().path("packages");
            List<Bundle> bundles = objectMapper.readValue(pv_packages.toString(), new TypeReference<List<Bundle>>() {});

            // Check if "insuredObject" exists in req, add if not found
            if (!req.has("insuredObject") || req.get("insuredObject").isNull()) {
                ObjectNode insuredObjectNode = objectMapper.createObjectNode();
                req.set("insuredObject", insuredObjectNode);
            }
            JsonNode insuredObject = req.get("insuredObject");
            JsonNode policyBundle = insuredObject.path("package");
            String policyBundleCode = policyBundle.asText("Basic");
            if (policyBundle.isNull()) {
                ((ObjectNode) insuredObject).put("package", "Basic");
            }

            ArrayNode coversNode;
            if (!insuredObject.has("covers") || insuredObject.get("covers").isNull()) {
                coversNode = objectMapper.createArrayNode();
                ((ObjectNode) insuredObject).set("covers", coversNode);
            } else {
                coversNode = (ArrayNode) insuredObject.get("covers");
            }

            final OffsetDateTime finalStartDate = startDate1;
            bundles.forEach(bundle -> {
                if (bundle.getCode().equals(policyBundleCode)) {
                    // Loop through covers in the bundle

                    for (Cover cover : bundle.getCovers()) {
                        boolean exists = false;
                        for (JsonNode node : coversNode) {
                            if (node.has("code") && cover.getCode().equals(node.get("code").asText())) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            ObjectNode newCoverNode = objectMapper.createObjectNode();
                            
                            newCoverNode.put("code", cover.getCode());
                            
                            OffsetDateTime coverStartDate = addPeriod(finalStartDate, "P0D");//cover.getDelayPeriod());
                            newCoverNode.put("startDate", format(coverStartDate));
                            OffsetDateTime coverEndDate = addPeriod(coverStartDate, cover.getCoverageTerm());
                            newCoverNode.put("endDate", format(coverEndDate));

                            newCoverNode.put("isMandatory", cover.isMandatory());
                            //newCoverNode.put("delayPeriod", cover.getDelayPeriod());
                            newCoverNode.put("coverageTerm", cover.getCoverageTerm());
                            newCoverNode.put("isDeductibleMandatory", cover.isDeductibleMandatory());
                            newCoverNode.set("deductibles", objectMapper.createArrayNode());
                            
                            coversNode.add(newCoverNode);
                        }
                    }
                }
            });


            return req;
        } catch (Exception e) {
            ObjectNode err = objectMapper.createObjectNode();
            err.put("error", e.getMessage());
            return err;
        }
    }

    private String calculatePeriodBetweenDates(OffsetDateTime issueDate, OffsetDateTime startDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calculatePeriodBetweenDates'");
    }


    private static boolean isSingleValueList(JsonNode node) {
        return node != null && node.isArray() && node.size() == 1 && node.get(0).isTextual();
    }

    private static Period parsePeriod(String text) {
        if (text == null || text.isBlank()) return null;
        return Period.parse(text);
    }

    

    private static OffsetDateTime parseDate(String text) {
        if (text == null || text.isBlank()) return null;
        return OffsetDateTime.parse(text);
    }

    private static String format(OffsetDateTime dt) {
        return dt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private static OffsetDateTime addPeriod(OffsetDateTime base, String isoPeriod) {
        Period p = Period.parse(isoPeriod);
        return base.plus(p);
    }
}

