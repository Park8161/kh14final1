package com.kh.fa.vo;

import java.util.List;

import com.kh.fa.dto.ProductDto;

import lombok.Data;

@Data
public class ProductDetailResponseVO {
	private ProductDto productDto;
	private List<Integer> images;
}
