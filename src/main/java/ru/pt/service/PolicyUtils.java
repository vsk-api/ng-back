package ru.pt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.pt.domain.Lob;
import ru.pt.domain.Product;
import ru.pt.domain.ProductVersion;
import ru.pt.domain.pv.*;
import ru.pt.domain.pv.Error;
import ru.pt.hz.PeriodUtils;
import ru.pt.repository.LobRepository;
import ru.pt.repository.ProductRepository;
import ru.pt.repository.ProductVersionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class PolicyUtils {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LobRepository lobRepository;

    @Autowired
    private ProductVersionRepository productVersionRepository;

    public InnerPolicy getPolicy(String requestBody) {
        try {
            com.jayway.jsonpath.Configuration jaywayConfig = com.jayway.jsonpath.Configuration.builder()
                    .options(com.jayway.jsonpath.Option.DEFAULT_PATH_LEAF_TO_NULL)
                    .build();
            // Extract product code from request body using JSONPath
            String productCode = JsonPath.using(jaywayConfig).parse(requestBody).read("$.product.code");
            
            // Get product from repository
            Product product = productRepository.findByCodeAndIsDeletedFalse(productCode)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productCode));
            
            // Get LOB from repository
            Lob lob = lobRepository.findByCodeAndIsDeletedFalse(product.getLob())
                    .orElseThrow(() -> new RuntimeException("LOB not found: " + product.getLob()));
            
            // Get product version from repository
            ProductVersion productVersion = productVersionRepository
                    .findByProductIdAndVersionNo(product.getId(), product.getDevVersionNo())
                    .orElseThrow(() -> new RuntimeException("Product version not found for product: " + productCode + ", version: " + product.getDevVersionNo()));
            
            // Create new InnerPolicy
            InnerPolicy innerPolicy = new InnerPolicy();
            
            // Set product with data from productVersion
            ProductInfo productInfo = new ProductInfo();
            productInfo.setId(product.getId());
            productInfo.setLob(product.getLob());
            productInfo.setCode(product.getCode());
            productInfo.setName(product.getName());
            productInfo.setVersionNo(product.getDevVersionNo());
            productInfo.setVersionStatus("DEV"); // Default statusTODO: get version status from productVersion
            innerPolicy.setProduct(productInfo);

            
                        // Set issueDate, issueTimeZone, startDate, endDate with data from requestBody
            innerPolicy.setIssueDate(JsonPath.using(jaywayConfig).parse(requestBody).read("$.issueDate"));
           // innerPolicy.setIssueTimeZone(JsonPath.read(requestBody, "$.issueTimeZone"));
            innerPolicy.setStartDate(JsonPath.using(jaywayConfig).parse(requestBody).read("$.startDate"));
            innerPolicy.setEndDate(JsonPath.using(jaywayConfig).parse(requestBody).read("$.endDate"));
            innerPolicy.setPremium(JsonPath.using(jaywayConfig).parse(requestBody).read("$.premium"));
            
            
            // Initialize empty arrays
            innerPolicy.setErrors(new ArrayList<>());
            innerPolicy.setAttributes(new ArrayList<>());

            // Set waitingPeriod and policyTerm with data from productVersion
            //
            //
            //
            JsonNode productJson = productVersion.getProduct();
            if (productJson.has("waitingPeriod")) {
                JsonNode waitingPeriod = productJson.get("waitingPeriod");
                String waitingPeriodType = waitingPeriod.get("validatorType").asText();
                String waitingPeriodValue = waitingPeriod.get("validatorValue").asText();
                
                switch (waitingPeriodType) {
                    case "RANGE":
                        try {
                            if (!PeriodUtils.isDateInRange(innerPolicy.getIssueDate(), innerPolicy.getStartDate(), waitingPeriodValue)) {
                                innerPolicy.getErrors().add(new Error("$.startDate", "Waiting period is not in range"));
                            }                            
                            } catch (Exception e) {
                                innerPolicy.getErrors().add(new Error("waitingPeriodValue", e.getMessage()));
                            }
                        break;
                    case "LIST":
                        try {
                            String date=PeriodUtils.isDatesInList(innerPolicy.getIssueDate(), innerPolicy.getStartDate(), waitingPeriodValue);
                            innerPolicy.setStartDate(date);
                        } catch (Exception e) {
                            innerPolicy.getErrors().add(new Error("waitingPeriodValue", e.getMessage()));
                        }
                        break;
                    case "NEXT_MONTH":
                        try {
                            String date=PeriodUtils.getNextMonth(innerPolicy.getIssueDate());
                            innerPolicy.setStartDate(date);
                        } catch (Exception e) {
                            innerPolicy.getErrors().add(new Error("waitingPeriodValue", e.getMessage()));
                        }
                        break;
                }
                innerPolicy.setWaitingPeriod(waitingPeriodType + ":" + waitingPeriodValue);
            }
            if (productJson.has("policyTerm")) {
                JsonNode policyTerm = productJson.get("policyTerm");   
                String policyTermType = policyTerm.get("validatorType").asText();
                String policyTermValue = policyTerm.get("validatorValue").asText();
                switch (policyTermType) {
                    
                    case "RANGE":
                        try {
                            if (!PeriodUtils.isDateInRange(innerPolicy.getStartDate(), innerPolicy.getEndDate(), policyTermValue)) {
                                innerPolicy.getErrors().add(new Error("$.endDate", "Waiting period is not in range"));
                            }                            
                            } catch (Exception e) {
                                innerPolicy.getErrors().add(new Error("waitingPeriodValue", e.getMessage()));
                            }
                        break;
                    case "LIST":
                    try {
                        String date = PeriodUtils.isDatesInList(innerPolicy.getStartDate(), innerPolicy.getEndDate(), policyTermValue);
                        innerPolicy.setEndDate(date);
                    } catch (Exception e) {
                        innerPolicy.getErrors().add(new Error("waitingPeriodValue", e.getMessage()));
                    }
                    break;
            }
                innerPolicy.setPolicyTerm(policyTermType + ":" + policyTermValue);
            }
            
            
            // Process mpVars from LOB
            JsonNode lobJson = lob.getLob();
            if (lobJson.has("mpVars")) {
                JsonNode mpVars = lobJson.get("mpVars");
                List<Attribute> attributes = new ArrayList<>();
                
                for (JsonNode mpVar : mpVars) {
                    String varCode = mpVar.get("varCode").asText();
                    String varType = mpVar.get("varType").asText();
                    String varPath = mpVar.get("varPath").asText();
                    
                    Attribute attribute = new Attribute();
                    attribute.setKey(varCode);
                    attribute.setPath(varPath);
                    
                    if ("IN".equals(varType)) {
                        try {
                            String value = JsonPath.using(jaywayConfig).parse(requestBody).read("$."+ varPath);
                            attribute.setValue(value);
                        } catch (Exception e) {
                            attribute.setValue("");
                        }
                    } else {
                        attribute.setValue("");
                    }
                    
                    attributes.add(attribute);
                }
                
                innerPolicy.setAttributes(attributes);
            }
            
            // Set bundles, covers, deductibles with data from productVersion
            if (productJson.has("packages")) {
                JsonNode bundlesJson = productJson.get("packages");
                List<Bundle> bundles = new ArrayList<>();
                
                for (JsonNode bundleJson : bundlesJson) {
                    Bundle bundle = new Bundle();
                    bundle.setCode(bundleJson.get("code").asText());
                    bundle.setName(bundleJson.get("name").asText());
                    if (bundleJson.has("premium")) {
                        bundle.setPremium(bundleJson.get("premium").asText("0"));
                    }
                    Double bundlePremium = 0.0;

                    if (bundleJson.has("covers")) {
                        JsonNode coversJson = bundleJson.get("covers");
                        List<Cover> covers = new ArrayList<>();
                        
                        for (JsonNode coverJson : coversJson) {
                            Cover cover = new Cover();
                            cover.setCode(coverJson.get("code").asText());
                            cover.setExistsInRequest(coverJson.has("existsInRequest") ? coverJson.get("existsInRequest").asBoolean() : false);
                            cover.setMandatory(coverJson.has("isMandatory") ? coverJson.get("isMandatory").asBoolean() : false);

                            
                            cover.setWaitingPeriod(coverJson.has("delayPeriod") ? coverJson.get("delayPeriod").asText() : "P0D");
                            cover.setCoverageTerm(coverJson.has("coverageTerm") ? coverJson.get("coverageTerm").asText() : "P1Y");


//                            cover.setStartDate(coverJson.has("startDate") ? coverJson.get("startDate").asText() : "");
//                            cover.setEndDate(coverJson.has("endDate") ? coverJson.get("endDate").asText()     : "");

                            cover.setStartDate(PeriodUtils.addPeriod(innerPolicy.getStartDate(),cover.getWaitingPeriod())); 
                            cover.setEndDate(PeriodUtils.addPeriod(cover.getStartDate(),cover.getCoverageTerm()));

                            cover.setInsAmount(coverJson.has("insAmount") ? coverJson.get("insAmount").asText() : "");
                            cover.setPremium(coverJson.has("premium") ? coverJson.get("premium").asText() : "");
                            cover.setDeductibleMandatory(coverJson.has("isDeductibleMandatory") ? coverJson.get("isDeductibleMandatory").asBoolean() : false);
                            
                            if (coverJson.has("deductibles")) {
                                JsonNode deductiblesJson = coverJson.get("deductibles");
                                List<Deductible> deductibles = new ArrayList<>();
                                
                                for (JsonNode deductibleJson : deductiblesJson) {
                                    Deductible deductible = new Deductible();
                                    deductible.setNr(deductibleJson.has("nr") ? deductibleJson.get("nr").asInt() : 0);
                                    deductible.setDeductibleType(deductibleJson.has("deductibleType") ? deductibleJson.get("deductibleType").asText() : "");
                                    deductible.setDeductible(deductibleJson.has("deductible") ? deductibleJson.get("deductible").asDouble() : 0);
                                    deductible.setDeductibleUnit(deductibleJson.has("deductibleUnit") ? deductibleJson.get("deductibleUnit").asText() : "");
                                    deductible.setDeductibleSpecific(deductibleJson.has("deductibleSpecific") ? deductibleJson.get("deductibleSpecific").asText() : "");
                                    
                                    deductibles.add(deductible);
                                }
                                
                                cover.setDeductibles(deductibles);
                            }
                            
                            covers.add(cover);
                        }
                        
                        bundle.setCovers(covers);
                        bundlePremium = covers.stream().mapToDouble(cover -> Double.parseDouble(cover.getPremium())).sum();
                        bundle.setPremium(Double.toString(bundlePremium));
                    }
                    
                    bundles.add(bundle);
                }
                
                innerPolicy.setBundles(bundles);
            }
            
            return innerPolicy;
            
        } catch (Exception e) {
            throw new RuntimeException("Error processing policy: " + e.getMessage(), e);
        }
    }
}
