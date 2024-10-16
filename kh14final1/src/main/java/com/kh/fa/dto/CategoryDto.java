package com.kh.fa.dto;

import lombok.Data;

@Data
public class CategoryDto {
	private int categorytNo;
	private String categoryName;
	
	private int categoryGroup; // 분류그룹번호
	private Integer categoryUpper; // 상위분류, null 가능
	private int categoryDepth; // 분류차수
	
	// 메소드 추가 - 대분류 확인 목적
	public boolean isNew() { // 상위분류가 없다면 - 대분류
		return this.categoryUpper == null;
	}
	public boolean isNotNew() { // 상위분류가 있다면 - 대분류 X
		return this.categoryUpper !=null;
	}
	
}
