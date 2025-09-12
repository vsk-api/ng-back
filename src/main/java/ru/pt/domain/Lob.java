package ru.pt.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "pt_lobs")
public class Lob {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 128)
    private String code;

    @Column(name = "name", nullable = false, length = 512)
    private String name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "lob", columnDefinition = "jsonb", nullable = false)
    private JsonNode lob;

    @Column(name = "isDeleted", nullable = false)
    private boolean isDeleted = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JsonNode getLob() {
        return lob;
    }

    public void setLob(JsonNode lob) {
        this.lob = lob;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}


