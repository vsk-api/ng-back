package ru.pt.domain.productVersion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NumberGeneratorCfg {

	@JsonProperty("mask")
	private String mask;

	@JsonProperty("xorMask")
	private String xorMask;

	@JsonProperty("maxValue")
	private Integer maxValue;

	@JsonProperty("resetPolicy")
	private String resetPolicy;

	public String getMask() { return mask; }
	public void setMask(String mask) { this.mask = mask; }

	public String getXorMask() { return xorMask; }
	public void setXorMask(String xorMask) { this.xorMask = xorMask; }

	public Integer getMaxValue() { return maxValue; }
	public void setMaxValue(Integer maxValue) { this.maxValue = maxValue; }

	public String getResetPolicy() { return resetPolicy; }
	public void setResetPolicy(String resetPolicy) { this.resetPolicy = resetPolicy; }
}


