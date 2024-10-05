package com.kh.fa.vo;

import com.kh.fa.dto.BlockDto;
import com.kh.fa.dto.MemberDto;
import com.kh.fa.dto.ProductDto;

import lombok.Data;

@Data
public class MypageVO {
	private MemberDto memberDto;
	private BlockDto blockDto;
	private ProductDto productDto;
}
