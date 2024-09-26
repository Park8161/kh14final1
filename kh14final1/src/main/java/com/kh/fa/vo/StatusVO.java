package com.kh.fa.vo;

import lombok.Data;

// VO(Value Object)
// - DTO로 표현하기 힘든 변형된 데이터를 담기 위한 객체

@Data // Dto와 용도가 같아 등록 없이 사용 (매 때 만들어 사용)
public class StatusVO {
	private String title;
	private int cnt;
}
