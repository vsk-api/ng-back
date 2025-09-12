package ru.pt.service;

import java.util.Map;
import java.util.Objects;

public class ValidatorImpl {

    public static boolean validate(Map<String, String> dataMap, String leftKey, String rightKey, String rightValue, String ruleType, String dataType) {

        String left = dataMap.get(leftKey);
        if (left == null) return false;

        String right = dataMap.get(rightKey);
        if (right == null) right = rightValue;

        if (dataType.equals("NUMBER")) {
            return checkNumber(ruleType, left, right);
        }
        if (dataType.equals("STRING")) {
            return checkString(ruleType, left, right);
        }
        return false;
    }

    private static boolean checkString(String type, String v1, String v2) {

        switch (type) {
            case "NOT_NULL": return v1 != null && !v1.isEmpty();
            case "=": return Objects.equals(v1, v2);
            case "!=": return !Objects.equals(v1, v2);
            case "MATCHES_REGEX": return v1 != null && v2 != null && v1.matches(v2);
            default: return false;
        }
    }

    private static boolean checkNumber(String type, String s1, String s2) {
        
        switch (type) {
            case "=": return Double.parseDouble(s1) == Double.parseDouble(s2);
            case "!=": return Double.parseDouble(s1) != Double.parseDouble(s2);
            case ">": return Double.parseDouble(s1) > Double.parseDouble(s2);
            case "<": return Double.parseDouble(s1) < Double.parseDouble(s2);
            case ">=": return Double.parseDouble(s1) >= Double.parseDouble(s2);
            case "<=": return Double.parseDouble(s1) <= Double.parseDouble(s2);
            case "RANGE": 
                String[] rightParts = s2.split("-");
                
                if (Double.parseDouble(s1) >= Double.parseDouble(rightParts[0].trim()) && Double.parseDouble(s1) <= Double.parseDouble(rightParts[1].trim())) {
                        return true;
                }
                
                return false;
            default: return false;
        }
    }

}
