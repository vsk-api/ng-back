package ru.pt.domain.pv;

public class Bundle {
    
    private String code;
    private String name;
    private String premium;
    private java.util.List<Cover> covers;

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPremium() {
        return premium;
    }
    public void setPremium(String premium) {
        this.premium = premium;
    }
    public java.util.List<Cover> getCovers() {
        return covers;
    }
    public void setCovers(java.util.List<Cover> covers) {
        this.covers = covers;
    }
}
