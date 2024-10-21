package com.kh.fa.vo;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ProductInsertRequestVO {
	private String productName;
	private int productCategory;
	private int productPrice;
	private String productDetail;
	private int productQty;

	private List<MultipartFile> attachList;
}
