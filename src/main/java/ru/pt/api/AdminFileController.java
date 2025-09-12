package ru.pt.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.pt.domain.FileEntity;
import ru.pt.service.FileService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/files")
public class AdminFileController {

    private final FileService fileService;

    public AdminFileController(FileService fileService) {
        this.fileService = fileService;
    }

    // POST /admin/files body json
    @PostMapping
    public ResponseEntity<Map<String, Object>> createMeta(@RequestBody Map<String, String> body) {
        String fileType = body.get("fileType").toLowerCase();
        String fileDesc = body.get("fileDescription");
        String productCode = body.get("productCode");
        FileEntity e = fileService.createMeta(fileType, fileDesc, productCode);
        Map<String, Object> resp = Map.of(
                "id", e.getId(),
                "fileType", e.getFileType(),
                "fileDescription", e.getFileDesc(),
                "productCode", e.getProductCode()
        );
        return ResponseEntity.ok(resp);
    }

    // POST /admin/files/{id} multipart file upload
    @PostMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> upload(@PathVariable("id") Long id, @RequestPart("file") MultipartFile file) {
        fileService.uploadBody(id, file);
        return ResponseEntity.noContent().build();
    }

    // DELETE /admin/files/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        fileService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    // GET /admin/files?product_code=***
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list(@RequestParam(value = "product_code", required = false) String productCode) {
        return ResponseEntity.ok(fileService.list(productCode));
    }

    // GET /admin/files/{id} -> file body
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@PathVariable("id") Long id) {
        byte[] bytes = fileService.download(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=output.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }

    // POST /admin/files/{ID}/cmd/process
    @PostMapping("/{id}/cmd/process")
    public ResponseEntity<byte[]> process(@PathVariable("id") Long id, @RequestBody List<Map<String, String>> pairs) {
        java.util.Map<String, String> kv = new java.util.HashMap<>();
        for (Map<String, String> p : pairs) {
            kv.put(p.get("key"), p.get("value"));
        }
        byte[] bytes = fileService.process(id, kv);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=processed.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }
}


