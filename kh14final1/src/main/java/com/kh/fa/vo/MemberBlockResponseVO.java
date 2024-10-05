package com.kh.fa.vo;

import java.util.List;

import lombok.Data;

@Data
public class MemberBlockResponseVO {
	private List<MemberBlockVO> memberBlockList; // 검색 결과
	private boolean isLast;
	private int count;
}
