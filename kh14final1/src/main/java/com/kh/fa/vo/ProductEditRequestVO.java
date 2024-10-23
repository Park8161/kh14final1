package com.kh.fa.vo;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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
	private List<Integer> originList; // 기존 이미지의 첨부파일번호
	private List<MultipartFile> attachList; // 첨부된 파일의 정보가 담긴 리스트
}
