package com.kh.fa.vo;

import java.util.List;

import lombok.Data;

@Data
public class MypageVO {
	// 내가 좋아요 누른 상품 목록 조회
	private List<ProductListVO> likeList;
	// 내 상품 목록 조회
	private List<ProductListVO> myList;
	// 내가 판매중인 상품 목록 조회 >> 리액트에서 내 상품 목록에 filter 쓰면 됨
	// 내가 예약중인 상품 목록 조회
	// 판매 완료된 내 상품 목록 조회
}
