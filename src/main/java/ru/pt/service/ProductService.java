package ru.pt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.pt.domain.Product;
import ru.pt.domain.ProductVersion;
import ru.pt.repository.ProductRepository;
import ru.pt.repository.ProductVersionRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVersionRepository productVersionRepository;
    private final DataSource dataSource;

    public ProductService(ProductRepository productRepository,
                          ProductVersionRepository productVersionRepository,
                          DataSource dataSource) {
        this.productRepository = productRepository;
        this.productVersionRepository = productVersionRepository;
        this.dataSource = dataSource;
    }

    public List<Map<String, Object>> listSummaries() {
        return productRepository.listActiveSummaries().stream()
                .map(r -> {
                    java.util.LinkedHashMap<String, Object> m = new java.util.LinkedHashMap<>();
                    m.put("id", r[0]);
                    m.put("lob", r[1]);
                    m.put("code", r[2]);
                    m.put("name", r[3]);
                    m.put("prodVersionNo", r[4]);
                    m.put("devVersionNo", r[5]);
                    return m;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Product create(JsonNode productJson) {
        requireText(productJson, "lob");
        requireText(productJson, "code");
        requireText(productJson, "name");

        long id = nextId();

        if (productJson instanceof ObjectNode obj) {
            obj.put("id", id);
            obj.put("versionNo", 1);
            obj.put("versionStatus", "DEV");
        }

        Product p = new Product();
        p.setId(id);
        p.setLob(productJson.get("lob").asText());
        p.setCode(productJson.get("code").asText());
        p.setName(productJson.get("name").asText());
        p.setProdVersionNo(null);
        p.setDevVersionNo(1);
        p.setDeleted(false);
        productRepository.save(p);

        ProductVersion pv = new ProductVersion();
        pv.setProductId(id);
        pv.setVersionNo(1);
        pv.setProduct(productJson);
        productVersionRepository.save(pv);

        return p;
    }

    public Optional<JsonNode> getVersion(Long id, Integer versionNo) {
        return productVersionRepository.findByProductIdAndVersionNo(id, versionNo)
                .map(ProductVersion::getProduct);
    }

    @Transactional
    public ProductVersion createVersionFrom(Long id, Integer versionNo) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (product.getDevVersionNo() != null) {
            throw new IllegalArgumentException("only one version can be in dev status");
        }
        int newVersion = product.getProdVersionNo() == null ? 1 : product.getProdVersionNo() + 1;

        JsonNode baseJson = productVersionRepository.findByProductIdAndVersionNo(id, versionNo)
                .orElseThrow(() -> new IllegalArgumentException("Base version not found"))
                .getProduct();

        if (baseJson instanceof ObjectNode obj) {
            obj.put("versionNo", newVersion);
            obj.put("versionStatus", "DEV");
        }

        ProductVersion pv = new ProductVersion();
        pv.setProductId(id);
        pv.setVersionNo(newVersion);
        pv.setProduct(baseJson);
        productVersionRepository.save(pv);

        product.setDevVersionNo(newVersion);
        productRepository.save(product);
        return pv;
    }

    @Transactional
    public JsonNode updateVersion(Long id, Integer versionNo, JsonNode newJson) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (product.getDevVersionNo() == null || !product.getDevVersionNo().equals(versionNo)) {
            throw new IllegalArgumentException("only dev version can be updated");
        }
        if (newJson instanceof ObjectNode obj) {
            obj.put("id", id);
            obj.put("versionNo", versionNo);
            obj.put("versionStatus", "DEV");
        }
        ProductVersion pv = productVersionRepository.findByProductIdAndVersionNo(id, versionNo)
                .orElseThrow(() -> new IllegalArgumentException("Version not found"));
        pv.setProduct(newJson);
        productVersionRepository.save(pv);
        return newJson;
    }

    @Transactional
    public void softDeleteProduct(Long id) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Transactional
    public void deleteVersion(Long id, Integer versionNo) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (product.getDevVersionNo() == null || !product.getDevVersionNo().equals(versionNo)) {
            throw new IllegalArgumentException("only dev version can be deleted");
        }
        int deleted = productVersionRepository.deleteByProductIdAndVersionNo(id, versionNo);
        if (deleted == 0) {
            throw new IllegalArgumentException("Version not found");
        }
        product.setDevVersionNo(null);
        Integer pv = product.getProdVersionNo();
        product.setProdVersionNo(pv == null ? null : Math.max(0, pv));
        productRepository.save(product);
    }

    private void requireText(JsonNode node, String field) {
        JsonNode v = node.get(field);
        if (v == null || v.isNull() || !v.isTextual() || v.asText().isBlank()) {
            throw new IllegalArgumentException("Field '" + field + "' is required");
        }
    }

    private long nextId() {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("select nextval('pt_products_seq')");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
            throw new IllegalStateException("No value from sequence pt_products_seq");
        } catch (SQLException e) {
            throw new IllegalStateException("Sequence pt_products_seq is missing or inaccessible", e);
        }
    }
}


