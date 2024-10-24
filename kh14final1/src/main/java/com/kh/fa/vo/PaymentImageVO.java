package com.kh.fa.vo;

import java.sql.Date;

import lombok.Data;

@Data
public class PaymentImageVO {
//	payment
	private int paymentNo;
	private String paymentTid;
	private String paymentName;
	private int paymentTotal;
	private int paymentRemain;
	private String paymentSeller;
	private String paymentBuyer;
	private Date paymentTime;
	private int productNo;
	private String paymentStatus;
//	product Image
	private int product;
	private int attachment;
}
