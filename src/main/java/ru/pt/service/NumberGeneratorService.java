package ru.pt.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pt.domain.NumberGenerator;
import ru.pt.repository.NumberGeneratorRepository;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class NumberGeneratorService {

    private final NumberGeneratorRepository repository;

    public NumberGeneratorService(NumberGeneratorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public NumberGenerator getNext(Long id) {
        NumberGenerator ng = repository.findForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("Generator not found: " + id));

        LocalDate today = LocalDate.now();
        switch (ng.getResetPolicy()) {
            case "YEARLY":
                if (ng.getLastReset().getYear() != today.getYear()) {
                    ng.setLastReset(today);
                    ng.setCurrentValue(0);
                }
                break;
            case "MONTHLY":
                if (ng.getLastReset().getYear() != today.getYear() || ng.getLastReset().getMonth() != today.getMonth()) {
                    ng.setLastReset(today);
                    ng.setCurrentValue(0);
                }
                break;
            case "NEVER":
            default:
                // no-op
        }

        int next = ng.getCurrentValue() + 1;
        if (ng.getMaxValue() != null && next > ng.getMaxValue()) {
            next = 1;
        }
        ng.setCurrentValue(next);

        return repository.save(ng);
    }

    public String getNumber(Map<String, String> values, Long id) {
        NumberGenerator ng = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Generator not found: " + id));

        String mask = ng.getMask();
        StringBuilder resultMask = new StringBuilder(mask);
        LocalDate today = LocalDate.now();
        
        // Replace {KEY} patterns
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{([^}]+)\\}");
        java.util.regex.Matcher matcher = pattern.matcher(mask);
        
        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement;
            
            // Built-in date keys
            if ("YYYY".equals(key)) {
                replacement = Integer.toString(today.getYear());
            } else if ("YY".equals(key)) {
                replacement = String.format("%02d", today.getYear() % 100);
            } else if ("MM".equals(key)) {
                replacement = String.format("%02d", today.getMonthValue());
            } else if (key.matches("X+")) {
                // Handle any number of X's as sequence number (XXXX, XXX, XX, etc.)
                int xCount = key.length();
                replacement = String.format("%0" + xCount + "d", ng.getCurrentValue());
            } else {
                // Get value from provided map
                replacement = values.getOrDefault(key, "");
            }
            
            replaceAll(resultMask, matcher.group(0), replacement);
        }

        return resultMask.toString();
    }


    private static void replaceAll(StringBuilder sb, String target, String replacement) {
        int idx;
        while ((idx = sb.indexOf(target)) != -1) {
            sb.replace(idx, idx + target.length(), replacement);
        }
    }

    private static String repeat(char ch, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(ch);
        return sb.toString();
    }
}


