package ru.pt.domain.data;


public class cover {
    private String code;
    private String option;
    private String description;
    private java.util.List<String> risk;
    private String startDate;
    private String endDate;
    private Double sumInsured;
    private Double premium;
    private String deductibleType;
    private Double deductible;
    private Double sumInsuredCur;
    private Double premiumCur;
    private Double deductibleCur;
    private Double deductiblePercent;
    private Double deductibleMin;
    private String deductibleUnit;
    private String deductibleSpecific;

    public cover() {}


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public java.util.List<String> getRisk() {
        return risk;
    }

    public void setRisk(java.util.List<String> risk) {
        this.risk = risk;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Double getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(Double sumInsured) {
        this.sumInsured = sumInsured;
    }

    public Double getPremium() {
        return premium;
    }

    public void setPremium(Double premium) {
        this.premium = premium;
    }

    public String getDeductibleType() {
        return deductibleType;
    }

    public void setDeductibleType(String deductibleType) {
        this.deductibleType = deductibleType;
    }

    public Double getDeductible() {
        return deductible;
    }

    public void setDeductible(Double deductible) {
        this.deductible = deductible;
    }

    public Double getSumInsuredCur() {
        return sumInsuredCur;
    }

    public void setSumInsuredCur(Double sumInsuredCur) {
        this.sumInsuredCur = sumInsuredCur;
    }

    public Double getPremiumCur() {
        return premiumCur;
    }

    public void setPremiumCur(Double premiumCur) {
        this.premiumCur = premiumCur;
    }

    public Double getDeductibleCur() {
        return deductibleCur;
    }

    public void setDeductibleCur(Double deductibleCur) {
        this.deductibleCur = deductibleCur;
    }

    public Double getDeductiblePercent() {
        return deductiblePercent;
    }

    public void setDeductiblePercent(Double deductiblePercent) {
        this.deductiblePercent = deductiblePercent;
    }

    public Double getDeductibleMin() {
        return deductibleMin;
    }

    public void setDeductibleMin(Double deductibleMin) {
        this.deductibleMin = deductibleMin;
    }

    public String getDeductibleUnit() {
        return deductibleUnit;
    }

    public void setDeductibleUnit(String deductibleUnit) {
        this.deductibleUnit = deductibleUnit;
    }

    public String getDeductibleSpecific() {
        return deductibleSpecific;
    }

    public void setDeductibleSpecific(String deductibleSpecific) {
        this.deductibleSpecific = deductibleSpecific;
    }
}
