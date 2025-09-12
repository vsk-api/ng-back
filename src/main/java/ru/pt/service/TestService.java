package ru.pt.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import ru.pt.domain.FileEntity;
import ru.pt.domain.Lob;
import ru.pt.domain.Product;
import ru.pt.domain.ProductVersion;
import ru.pt.repository.FileRepository;
import ru.pt.repository.LobRepository;
import ru.pt.repository.ProductRepository;
import ru.pt.repository.ProductVersionRepository;
import ru.pt.service.FileService;

@Service
public class TestService {

    private final ProductRepository productRepository;
    private final ProductVersionRepository productVersionRepository;
    private final LobRepository lobRepository;
    private final FileRepository fileRepository;
    private final ProductService productService;
    private final ObjectMapper objectMapper;
    private final FileService fileService;

    private final CalculatorImpl calculatorImpl;

    public TestService(ProductRepository productRepository,
                       LobRepository lobRepository,
                       ProductService productService,
                       ObjectMapper objectMapper,
                       ProductVersionRepository productVersionRepository,
                       FileRepository fileRepository,
                       FileService fileService,
                       CalculatorImpl calculatorImpl) {
        this.productRepository = productRepository;
        this.productVersionRepository = productVersionRepository;
        this.lobRepository = lobRepository;
        this.fileRepository = fileRepository;
        this.productService = productService;
        this.objectMapper = objectMapper;
        this.fileService = fileService;
        this.calculatorImpl = calculatorImpl;
    }

    public ObjectNode quoteValidator(String requestBody) {
        return Validator(requestBody,"quoteValidator");
    }

    public ObjectNode Validator(String requestBody, String validatorType) {
        try {
            Map<String, String> dataMap = new HashMap<>();

            DocumentContext ctx = JsonPath.parse(requestBody);
            String productCode = ctx.read("$.product.code", String.class);

            Product product = productRepository.findByCodeAndIsDeletedFalse(productCode)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productCode));

            if (product.getDevVersionNo() == null) {
                throw new IllegalArgumentException("Product has no dev version");
            }

            Lob lob = lobRepository.findByCodeAndIsDeletedFalse(product.getLob())
                    .orElseThrow(() -> new IllegalArgumentException("LOB not found: " + product.getLob()));

            ObjectNode response = objectMapper.createObjectNode();
            response.put("product", productCode);

            // Build context from LOB variables
            ArrayNode context = response.putArray("context");
            JsonNode lobVars = lob.getLob().path("mpVars");
            if (lobVars.isArray()) {
                for (JsonNode var : lobVars) {
                    String varCode = var.path("varCode").asText();
                    String varPath = var.path("varPath").asText();
                    String varType = var.path("varType").asText();

                    if (varType.equals("IN")) {
                        ObjectNode contextItem = context.addObject();
                        contextItem.put("varCode", varCode);

                        try {
                            String value = ctx.read(varPath);
                            contextItem.put("varValue", value == null ? "" : value);
                            dataMap.put(varCode, value == null ? "" : value);

                        } catch (Exception ignore) {
                            contextItem.put("varValue", ignore.getMessage());
//                            contextItem.put("varValue", "");
                        }
                    }
                }
            }

            if (lobVars.isArray()) {
                for (JsonNode var : lobVars) {
                    String varCode = var.path("varCode").asText();
                    String varPath = var.path("varPath").asText();
                    String varType = var.path("varType").asText();

                    if (!varType.equals("IN")) {
                        ObjectNode contextItem = context.addObject();
                        contextItem.put("varCode", varCode);

                        try {
                            String value = "";
                            if (varType.equals("MAGIC")) {
                                 value = getMagicValue(dataMap, varCode);
                            } else {
                                 value = ctx.read(varPath);
                            }
                            contextItem.put("varValue", value == null ? "" : value);
                        } catch (Exception ignore) {
                            contextItem.put("varValue", ignore.getMessage());
                        }
                    }
                }
            }

            // Build errorText array
            ArrayNode errorText = response.putArray("errorText");
            ObjectNode error = errorText.addObject();
            error.put("errorText", "Validation completed");
            
            
            ProductVersion productVersion = productVersionRepository.findByProductIdAndVersionNo(product.getId(), product.getDevVersionNo())
                    .orElseThrow(() -> new IllegalArgumentException("Product version not found"));
            // Validate quoteValidator
            for (JsonNode validator : productVersion.getProduct().path(validatorType)) {
                String keyLeft = validator.get("keyLeft") != null ? validator.get("keyLeft").asText() : "";
                String keyRight = validator.get("keyRight") != null ? validator.get("keyRight").asText() : "";
                String valueRight = validator.get("valueRight") != null ? validator.get("valueRight").asText() : "";
                String ruleType = validator.get("ruleType") != null ? validator.get("ruleType").asText() : "";
                String dataType = validator.get("dataType") != null ? validator.get("dataType").asText() : "";

                if (!ValidatorImpl.validate(dataMap, keyLeft, keyRight, valueRight, ruleType, dataType ))
                {
                    error.put("validator",keyLeft + " " + keyRight + " " + valueRight + " " + ruleType + " " + dataType);

                    error.put("errorText", validator.get("errorText").asText());
                }
            }


            return response;
        } catch (Exception e) {
            ObjectNode errorResponse = objectMapper.createObjectNode();
            errorResponse.put("product", "ERROR");
            ArrayNode errorText = errorResponse.putArray("errorText");
            ObjectNode error = errorText.addObject();
            error.put("errorText", e.getMessage());
            return errorResponse;
        }
    }

    public ObjectNode quoteCalculator(String requestBody) {
        return calculatorImpl.preCalculate(requestBody);
        
    }

    public ObjectNode policyValidator(String requestBody) {
        return Validator(requestBody,"saveValidator");
    }

    public String policyCalculator(String requestBody) {
        return requestBody;
    }

    public byte[] printpf(String pf_type, String requestBody) {
        // TODO Auto-generated method stub
        ObjectNode dataMap = quoteValidator(requestBody);
        // Take context[] from dataMap and loop through its objects
        java.util.Map<String, String> kv = new java.util.HashMap<>();
        if (dataMap.has("context") && dataMap.get("context").isArray()) {
            for (JsonNode obj : dataMap.get("context")) {
                String varCode = obj.has("varCode") ? obj.get("varCode").asText() : null;
                String varValue = obj.has("varValue") ? obj.get("varValue").asText() : null;
                if (varCode != null && varValue != null) {
                    kv.put(varCode, varValue);
                }
            }
        }
        String productCode = dataMap.get("product").asText();

        FileEntity fileEntity = fileRepository.findActiveByProductandFileType(productCode, pf_type);

              
        //
        //throw new UnsupportedOperationException("Unimplemented method 'printpf'");
//        java.util.Map<String, String> kv = new java.util.HashMap<>();
//        for (Map<String, String> p : pairs) {
//            kv.put(p.get("key"), p.get("value"));
//        }
        return fileService.process(fileEntity.getId(), kv);
        
    }
    
    // Временно тут
    public static String getMagicValue(Map<String, String> dataMap, String key) {
        switch (key) {
            case "ph_isMale":
                return "M".equals(dataMap.get("ph_gender")) ? "X" : "";
            case "ph_isFemale":
                return "F".equals(dataMap.get("ph_gender")) ? "X" : "";
            default:
            return key +" Not Found";
        }
        
    }
}
