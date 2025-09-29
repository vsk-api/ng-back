package ru.pt.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import ru.pt.domain.lob.LobVar;
import ru.pt.domain.lob.VarDataType;

public class ValidatorImpl {


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

    public static boolean validate(List<LobVar> dataMap, String leftKey, String rightKey, String rightValue, String ruleType) {
        LobVar leftVarDef = dataMap.stream().filter(v -> v.getVarCode().equals(leftKey)).findFirst().orElse(null);
        LobVar rightVarDef = dataMap.stream().filter(v -> v.getVarCode().equals(rightKey)).findFirst().orElse(null);
        if (rightVarDef != null) {
            if (! leftVarDef.getVarType().equalsIgnoreCase(rightVarDef.getVarType())) {
                return false;
            }
        }
        if ( rightVarDef == null) {
            rightVarDef = new LobVar(rightKey, "","",leftVarDef.getVarType(), rightValue, leftVarDef.getVarDataType());
        }

        if (leftVarDef == null || rightVarDef == null) return false;
        if (leftVarDef.getVarDataType() == VarDataType.NUMBER) {
            return checkNumber(ruleType, leftVarDef.getVarValue(), rightVarDef.getVarValue());
        }
        if (leftVarDef.getVarDataType() == VarDataType.STRING) {
            return checkString(ruleType, leftVarDef.getVarValue(), rightVarDef.getVarValue());
        }
        
        return false;
    }
}
