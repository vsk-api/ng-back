package ru.pt.domain.lob;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class LobModel {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("mpCode")
    private String mpCode;
    
    @JsonProperty("mpName")
    private String mpName;
    
    @JsonProperty("mpVars")
    private List<LobVar> mpVars;
    
    @JsonProperty("mpCovers")
    private List<LobCover> mpCovers;
    
    // Constructors
    public LobModel() {}
    
    public LobModel(Long id, String mpCode, String mpName, List<LobVar> mpVars, List<LobCover> mpCovers) {
        this.id = id;
        this.mpCode = mpCode;
        this.mpName = mpName;
        this.mpVars = mpVars;
        this.mpCovers = mpCovers;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getMpCode() {
        return mpCode;
    }
    
    public void setMpCode(String mpCode) {
        this.mpCode = mpCode;
    }
    
    public String getMpName() {
        return mpName;
    }
    
    public void setMpName(String mpName) {
        this.mpName = mpName;
    }
    
    public List<LobVar> getMpVars() {
        return mpVars;
    }
    
    public void setMpVars(List<LobVar> mpVars) {
        this.mpVars = mpVars;
    }
    
    public List<LobCover> getMpCovers() {
        return mpCovers;
    }
    
    public void setMpCovers(List<LobCover> mpCovers) {
        this.mpCovers = mpCovers;
    }
}
