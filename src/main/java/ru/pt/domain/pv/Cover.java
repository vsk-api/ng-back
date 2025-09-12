package ru.pt.domain.pv;

public class Cover {
    private String code;
    private boolean existsInRequest;
    private boolean isMandatory;
    private String waitingPeriod;
    private String coverageTerm;
    private String startDate;
    private String endDate;
    private String insAmount;
    private String premium;
    private boolean isDeductibleMandatory;
    private java.util.List<Deductible> deductibles;

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public boolean isExistsInRequest() {
        return existsInRequest;
    }
    public void setExistsInRequest(boolean existsInRequest) {
        this.existsInRequest = existsInRequest;
    }
    public boolean isMandatory() {
        return isMandatory;
    }
    public void setMandatory(boolean isMandatory) {
        this.isMandatory = isMandatory;
    }
    public String getWaitingPeriod() {
        return waitingPeriod;
    }
    public void setWaitingPeriod(String waitingPeriod) {
        this.waitingPeriod = waitingPeriod;
    }
    public String getCoverageTerm() {
        return coverageTerm;
    }
    public void setCoverageTerm(String coverageTerm) {
        this.coverageTerm = coverageTerm;
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
    public String getInsAmount() {
        return insAmount;
    }
    public void setInsAmount(String insAmount) {
        this.insAmount = insAmount;
    }
    public String getPremium() {
        return premium;
    }
    public void setPremium(String premium) {
        this.premium = premium;
    }
    public boolean isDeductibleMandatory() {
        return isDeductibleMandatory;
    }
    public void setDeductibleMandatory(boolean isDeductibleMandatory) {
        this.isDeductibleMandatory = isDeductibleMandatory;
    }
    public java.util.List<Deductible> getDeductibles() {
        return deductibles;
    }
    public void setDeductibles(java.util.List<Deductible> deductibles) {
        this.deductibles = deductibles;
    }
}
