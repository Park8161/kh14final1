package com.kh.fa.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductBuyRequestVO {
	private int productNo;
	private int totalPrice;
	private String approvalUrl;
	private String cancelUrl;
	private String failUrl;
}
