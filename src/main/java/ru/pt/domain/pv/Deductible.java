package ru.pt.domain.pv;

public class Deductible {
    
    private int nr;
    private String deductibleType;
    private double deductible;
    private String deductibleUnit;
    private String deductibleSpecific;
    
    public int getNr() {
        return nr;
    }
    public void setNr(int nr) {
        this.nr = nr;
    }
    public String getDeductibleType() {
        return deductibleType;
    }
    public void setDeductibleType(String deductibleType) {
        this.deductibleType = deductibleType;
    }

    public double getDeductible() {
        return deductible;
    }
    public void setDeductible(double deductible) {
        this.deductible = deductible;
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

