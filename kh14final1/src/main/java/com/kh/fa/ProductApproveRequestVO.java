package com.kh.fa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductApproveRequestVO {
	private String partnerOrderId;
	private String tid;
	private String pgToken;
	private int productNo;
	private int totalPrice;
}
