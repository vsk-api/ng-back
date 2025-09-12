package ru.pt.api;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pt.domain.pv.InnerPolicy;
import ru.pt.service.PolicyUtils;
import ru.pt.service.NumberGeneratorService;
import ru.pt.service.TestService;

@RestController
@RequestMapping("/test")
public class TestController {

    private final TestService testService;
    private final PolicyUtils policyUtils;
    private final NumberGeneratorService numberGeneratorService;

    public TestController(TestService testService, PolicyUtils policyUtils, NumberGeneratorService numberGeneratorService) {
        this.testService = testService;
        this.policyUtils = policyUtils;
        this.numberGeneratorService = numberGeneratorService;
    }

    @PostMapping("/quote/validator")
    public ResponseEntity<ObjectNode> quoteValidator(@RequestBody String requestBody) {
        ObjectNode response = testService.quoteValidator(requestBody);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quote/calculator")
    public ResponseEntity<ObjectNode> quoteCalculator(@RequestBody String requestBody) {
        ObjectNode response = testService.quoteCalculator(requestBody);
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

    @PostMapping("/policy/inner")
    public ResponseEntity<InnerPolicy> policyInner(@RequestBody String requestBody) {
        InnerPolicy response = policyUtils.getPolicy(requestBody);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/policy/nextnumber/{id}")
    public ResponseEntity<String> nextNumber(@PathVariable("id") Long id, @RequestBody(required = false) java.util.Map<String, String> values) {
        if (values == null) values = java.util.Collections.emptyMap();
        // Step 1: advance number atomically
        numberGeneratorService.getNext(id);
        // Step 2: render formatted number using provided map
        String number = numberGeneratorService.getNumber(values, id);
        return ResponseEntity.ok(number);
    }


}
