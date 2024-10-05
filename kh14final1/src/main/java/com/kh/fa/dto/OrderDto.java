package com.kh.fa.dto;

import lombok.Data;

@Data
public class OrderDto {
	private int orderNo;
	private int orderProduct;
	private String orderSeller;
	private String orderBuyer;
	private String orderState;
}
