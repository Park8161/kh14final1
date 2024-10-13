package com.kh.fa.vo;

import java.util.List;

import com.kh.fa.dto.BlockDto;
import com.kh.fa.dto.MemberDto;
import com.kh.fa.dto.ProductDto;

import lombok.Data;

@Data
public class MypageVO {
	private MemberDto memberDto;
	private List<BlockDto> blockList;
	private List<ProductDto>  productList;
}
