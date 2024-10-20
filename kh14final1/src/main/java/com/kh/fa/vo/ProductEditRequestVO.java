package com.kh.fa.vo;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kh.fa.advice.JsonEmptyStringToNullDeserializer;

import lombok.Data;

@Data
public class ProductEditRequestVO {
	private int productNo;
	private String productMember;
	private String productName;
	private int productPrice;
	private String productDetail;
//	private String productState;
	private int productQty;
	private int productCategory;

	private List<MultipartFile> attachList;
}
