package ru.pt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.pt.domain.Lob;
import ru.pt.repository.LobRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class LobService {

    private final LobRepository lobRepository;
    private final DataSource dataSource;

    public LobService(LobRepository lobRepository, DataSource dataSource) {
        this.lobRepository = lobRepository;
        this.dataSource = dataSource;
    }

    public List<Object[]> listActiveSummaries() {
        return lobRepository.listActiveSummaries();
    }

    public Optional<Lob> getByCode(String code) {
        return lobRepository.findByCodeAndIsDeletedFalse(code);
    }

    @Transactional
    public Lob create(JsonNode payload) {
        validatePayload(payload, true);

        long nextId = fetchNextId();

        String mpCode = getText(payload, "mpCode");
        String mpName = getText(payload, "mpName");

        if (payload instanceof ObjectNode objectNode) {
            objectNode.put("id", nextId);
        }

        Lob entity = new Lob();
        entity.setId(nextId);
        entity.setCode(mpCode);
        entity.setName(mpName);
        entity.setLob(payload);

        try {
            return lobRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("LOB with code already exists: " + mpCode);
        }
    }

    @Transactional
    public Optional<Lob> updateByCode(String code, JsonNode payload) {
        return lobRepository.findByCodeAndIsDeletedFalse(code).map(existing -> {
            validatePayload(payload, false);

            if (payload instanceof ObjectNode objectNode) {
                objectNode.put("id", existing.getId());
                objectNode.put("mpCode", existing.getCode());
            }

            existing.setName(getText(payload, "mpName"));
            existing.setLob(payload);
            return lobRepository.save(existing);
        });
    }

    @Transactional
    public boolean softDeleteByCode(String code) {
        return lobRepository.findByCodeAndIsDeletedFalse(code).map(existing -> {
            existing.setDeleted(true);
            lobRepository.save(existing);
            return true;
        }).orElse(false);
    }

    private long fetchNextId() {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("select nextval('pt_lobs_seq')");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new IllegalStateException("No value from sequence pt_lobs_seq");
        } catch (SQLException e) {
            throw new IllegalStateException("Sequence pt_lobs_seq is missing or inaccessible", e);
        }
    }

    private static String getText(JsonNode node, String field) {
        JsonNode v = node.get(field);
        if (v == null || v.isNull() || !v.isTextual() || v.asText().isBlank()) {
            throw new IllegalArgumentException("Field '" + field + "' is required and must be non-empty string");
        }
        return v.asText();
    }

    private static void validatePayload(JsonNode payload, boolean isCreate) {
        // mpCode and mpName
        getText(payload, "mpCode");
        getText(payload, "mpName");

        // mpVars unique varCode, varType in ['IN','VAR'], varPath present
        Set<String> varCodes = new HashSet<>();
        if (payload.has("mpVars") && payload.get("mpVars").isArray()) {
            for (JsonNode var : payload.get("mpVars")) {
                String varCode = getText(var, "varCode");
                String varType = getText(var, "varType");
//                if (!("IN".equals(varType) || "VAR".equals(varType))) {
//                    throw new IllegalArgumentException("varType must be one of [IN, VAR]");
//                }
                if (!varCodes.add(varCode)) {
                    throw new IllegalArgumentException("Duplicate varCode: " + varCode);
                }
                getText(var, "varPath");
            }
        }

        // mpCovers unique coverCode, coverName not empty
        Set<String> coverCodes = new HashSet<>();
        if (payload.has("mpCovers") && payload.get("mpCovers").isArray()) {
            for (JsonNode cover : payload.get("mpCovers")) {
                String coverCode = getText(cover, "coverCode");
                if (!coverCodes.add(coverCode)) {
                    throw new IllegalArgumentException("Duplicate coverCode: " + coverCode);
                }
                getText(cover, "coverName");
            }
        }
    }
}


