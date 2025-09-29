package ru.pt.domain.productVersion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductVersionModel {

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("lob")
	private String lob;

	@JsonProperty("code")
	private String code;

	@JsonProperty("name")
	private String name;

	@JsonProperty("versionNo")
	private Integer versionNo;

	@JsonProperty("versionStatus")
	private String versionStatus;

	@JsonProperty("waitingPeriod")
	private PeriodRule waitingPeriod;

	@JsonProperty("policyTerm")
	private PeriodRule policyTerm;

	@JsonProperty("quoteValidator")
	private List<ValidatorRule> quoteValidator;

	@JsonProperty("saveValidator")
	private List<ValidatorRule> saveValidator;

	@JsonProperty("packages")
	private List<PvPackage> packages;

	@JsonProperty("numberGenerator")
	private NumberGeneratorCfg numberGenerator;

	public static ProductVersionModel fromJson(JsonNode node, ObjectMapper mapper) {
		if (node == null) return null;
		return mapper.convertValue(node, ProductVersionModel.class);
	}

	public ObjectNode toJson(ObjectMapper mapper) {
		return mapper.valueToTree(this);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLob() {
		return lob;
	}

	public void setLob(String lob) {
		this.lob = lob;
	}

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

	public List<PvPackage> getPackages() {
		return packages;
	}

	public void setPackages(List<PvPackage> packages) {
		this.packages = packages;
	}

	public Integer getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	public PeriodRule getPolicyTerm() {
		return policyTerm;
	}

	public void setPolicyTerm(PeriodRule policyTerm) {
		this.policyTerm = policyTerm;
	}

	public List<ValidatorRule> getSaveValidator() {
		return saveValidator;
	}

	public void setSaveValidator(List<ValidatorRule> saveValidator) {
		this.saveValidator = saveValidator;
	}

	public String getVersionStatus() {
		return versionStatus;
	}

	public void setVersionStatus(String versionStatus) {
		this.versionStatus = versionStatus;
	}

	public List<ValidatorRule> getQuoteValidator() {
		return quoteValidator;
	}

	public void setQuoteValidator(List<ValidatorRule> quoteValidator) {
		this.quoteValidator = quoteValidator;
	}

	public PeriodRule getWaitingPeriod() {
		return waitingPeriod;
	}

	public void setWaitingPeriod(PeriodRule waitingPeriod) {
		this.waitingPeriod = waitingPeriod;
	}

	public NumberGeneratorCfg getNumberGenerator() {
		return numberGenerator;
	}

	public void setNumberGenerator(NumberGeneratorCfg numberGenerator) {
		this.numberGenerator = numberGenerator;
	}	
}


