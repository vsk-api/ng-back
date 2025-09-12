package ru.pt.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pt.domain.Product;
import ru.pt.domain.ProductVersion;
import ru.pt.service.ProductService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return productService.listSummaries();
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody JsonNode payload) {
        Product created = productService.create(payload);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}/versions/{versionNo}")
    public ResponseEntity<JsonNode> getVersion(@PathVariable("id") Long id, @PathVariable("versionNo") Integer versionNo) {
        return productService.getVersion(id, versionNo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/versions/{versionNo}/cmd/create")
    public ResponseEntity<JsonNode> createVersion(@PathVariable("id") Long id, @PathVariable("versionNo") Integer versionNo) {
        JsonNode json = productService.createVersionFrom(id, versionNo).getProduct();
        return ResponseEntity.ok(json);
    }

    @PutMapping("/{id}/versions/{versionNo}")
    public ResponseEntity<JsonNode> updateVersion(@PathVariable("id") Long id,
                                                  @PathVariable("versionNo") Integer versionNo,
                                                  @RequestBody JsonNode payload) {
        JsonNode updatedJson = productService.updateVersion(id, versionNo, payload);
        return ResponseEntity.ok(updatedJson);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        productService.softDeleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/versions/{versionNo}")
    public ResponseEntity<Void> deleteVersion(@PathVariable("id") Long id, @PathVariable("versionNo") Integer versionNo) {
        productService.deleteVersion(id, versionNo);
        return ResponseEntity.noContent().build();
    }
}


