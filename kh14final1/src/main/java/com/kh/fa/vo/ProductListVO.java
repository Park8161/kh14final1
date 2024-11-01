package com.kh.fa.vo;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class ProductListVO {
	private int productNo;
	private String productMember;
	private String productName;
	private int productCategory;
	private int productPrice;
	private String productDetail;
	private String productState;
	private int productQty;
	private int productLikes;
	private Timestamp productDate;
	private Integer attachment;
}
