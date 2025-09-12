package ru.pt.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pt.domain.Lob;
import ru.pt.service.LobService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/lobs")
public class AdminLobController {

    private final LobService lobService;

    public AdminLobController(LobService lobService) {
        this.lobService = lobService;
    }

    // get /admin/lobs return id, Code, Name from repository
    @GetMapping
    public List<Map<String, Object>> list() {
        return lobService.listActiveSummaries().stream()
                .map(row -> Map.of(
                        "id", row[0],
                        "mpCode", row[1],
                        "mpName", row[2]
                ))
                .collect(Collectors.toList());
    }

    // get /admin/lobs/{lob_code} returns json
    @GetMapping("/{lobCode}")
    public ResponseEntity<JsonNode> getByCode(@PathVariable("lobCode") String lobCode) {
        return lobService.getByCode(lobCode)
                .map(l -> ResponseEntity.ok(l.getLob()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // post /admin/lobs insert new record
    @PostMapping
    public ResponseEntity<Lob> create(@RequestBody JsonNode payload) {
        Lob created = lobService.create(payload);
        return ResponseEntity.ok(created);
    }

    // put /admin/lobs/{lob_code} replace json, fix name and mpCode/id rules
    @PutMapping("/{lobCode}")
    public ResponseEntity<Lob> update(@PathVariable("lobCode") String lobCode, @RequestBody JsonNode payload) {
        return lobService.updateByCode(lobCode, payload)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // delete /admin/lobs/{lob_code} soft delete
    @DeleteMapping("/{lobCode}")
    public ResponseEntity<Void> delete(@PathVariable("lobCode") String lobCode) {
        boolean deleted = lobService.softDeleteByCode(lobCode);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}


