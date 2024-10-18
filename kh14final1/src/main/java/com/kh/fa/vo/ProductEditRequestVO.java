package com.kh.fa.vo;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kh.fa.dto.ProductDto;

import lombok.Data;

@Data
public class ProductEditRequestVO {
//	private int productNo;
//	private String productMember;
//	private String productName;
//	private int productPrice;
//	private String productDetail;
//	private String productState;
//	private int productQty;
//	private int productCategory;
	private ProductDto productDto;
	private List<MultipartFile> attachList;
}
