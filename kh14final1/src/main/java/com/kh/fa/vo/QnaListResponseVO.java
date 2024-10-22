package com.kh.fa.vo;

import java.util.List;

import com.kh.fa.dto.QnaDto;

import lombok.Data;

@Data
public class QnaListResponseVO {
	private List<QnaDto> qnaList; // 검색 결과
	private boolean isLast;
	private int count;
}
