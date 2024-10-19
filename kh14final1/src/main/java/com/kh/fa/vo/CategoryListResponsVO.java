package com.kh.fa.vo;

import java.util.List;

import com.kh.fa.dto.CategoryDto;

import lombok.Data;

@Data
public class CategoryListResponsVO {
	private List<CategoryDto> categoryList;
	private boolean isLast; // 다음 항목이 존재하는가
	private int count; // 개수는 몇개인가
}
