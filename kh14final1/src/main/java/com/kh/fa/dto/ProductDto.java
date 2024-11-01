package com.kh.fa.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class ProductDto {
	private int productNo;
	private String productMember;
	private String productName;
	private int productCategory;
	private int productPrice;
	private String productDetail;
	private String productState;
	private int productQty;
	private int productLikes;
	private Date productDate;
}
