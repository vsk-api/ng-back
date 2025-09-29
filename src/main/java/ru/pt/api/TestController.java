package ru.pt.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pt.domain.calculator.CoefficientColumn;
//import ru.pt.domain.pv.InnerPolicy;
//import ru.pt.service.PolicyUtils;
import ru.pt.service.NumberGeneratorService;
import ru.pt.service.PolicyService;
import ru.pt.service.CoefficientService;
import ru.pt.service.CalculatorService;

@RestController
@RequestMapping("/test")
public class TestController {

    private final PolicyService testService;
 //   private final PolicyUtils policyUtils;
    private final NumberGeneratorService numberGeneratorService;
    private final CoefficientService coefficientService;
    private final ObjectMapper objectMapper;
    private final CalculatorService calculateService;

    public TestController(PolicyService testService,
                          //PolicyUtils policyUtils,
                          NumberGeneratorService numberGeneratorService,
                          CoefficientService coefficientService,
                          ObjectMapper objectMapper,
                          CalculatorService calculateService) {
        this.testService = testService;
        //this.policyUtils = policyUtils;
        this.numberGeneratorService = numberGeneratorService;
        this.coefficientService = coefficientService;
        this.objectMapper = objectMapper;
        this.calculateService = calculateService;
    }

    @PostMapping("/quote/validator")
    public ResponseEntity<ObjectNode> quoteValidator(@RequestBody String requestBody) {
        ObjectNode response = testService.quoteValidator(requestBody);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quote/calculator")
    public ResponseEntity<ObjectNode> quoteCalculator(@RequestBody String requestBody) {
        ObjectNode response = testService.policyValidator(requestBody);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/policy/validator")
    public ResponseEntity<ObjectNode> policyValidator(@RequestBody String requestBody) {
        ObjectNode response = testService.policyValidator(requestBody);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/policy/calculator")
    public ResponseEntity<String> policyCalculator(@RequestBody String requestBody) {
        String response = testService.policyCalculator(requestBody);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/policy/printpf/{pf_type}")
    public ResponseEntity<byte[]> printpf(@PathVariable("pf_type") String pf_type, @RequestBody String requestBody) {
        byte[] bytes = testService.printpf(pf_type.toLowerCase(), requestBody);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=processed.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
        
    }

    @PostMapping("/main")
    public ResponseEntity<String> main(@RequestBody String requestBody) {
        try {
            JsonNode node = objectMapper.readTree(requestBody);
            Integer calculatorId = node.path("calculatorId").isMissingNode() ? null : node.path("calculatorId").asInt();
            String coefficientCode = node.path("coefficientCode").isMissingNode() ? null : node.path("coefficientCode").asText(null);
            java.util.Map<String,String> values = objectMapper.convertValue(
                    node.path("values"), new TypeReference<java.util.Map<String,String>>(){});
            java.util.List<CoefficientColumn> columns = objectMapper.convertValue(
                    node.path("columns"), new TypeReference<java.util.List<CoefficientColumn>>(){});
            String result = coefficientService.getCoefficientValue(calculatorId, coefficientCode, values, columns);
            return ResponseEntity.ok(result == null ? "" : result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("");
        }
    }




}
