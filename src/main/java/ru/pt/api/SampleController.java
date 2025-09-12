package ru.pt.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/items")
public class SampleController {

    private final Map<Long, String> inMemory = Collections.synchronizedMap(new LinkedHashMap<>());
    private long sequence = 0L;

    @GetMapping
    public List<Map.Entry<Long, String>> list() {
        return new ArrayList<>(inMemory.entrySet());
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> get(@PathVariable long id) {
        String value = inMemory.get(id);
        return value != null ? ResponseEntity.ok(value) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Map.Entry<Long, String>> create(@RequestBody @Valid String value) {
        long id = ++sequence;
        inMemory.put(id, value);
        return ResponseEntity.ok(Map.entry(id, value));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map.Entry<Long, String>> update(@PathVariable long id, @RequestBody @Valid String value) {
        if (!inMemory.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        inMemory.put(id, value);
        return ResponseEntity.ok(Map.entry(id, value));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        return inMemory.remove(id) != null ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}


