package ru.pt.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "pt_product_versions")
public class ProductVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "version_no", nullable = false)
    private Integer versionNo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "product", columnDefinition = "jsonb", nullable = false)
    private JsonNode product;

    public Long getPk() { return pk; }
    public void setPk(Long pk) { this.pk = pk; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getVersionNo() { return versionNo; }
    public void setVersionNo(Integer versionNo) { this.versionNo = versionNo; }
    public JsonNode getProduct() { return product; }
    public void setProduct(JsonNode product) { this.product = product; }
}


