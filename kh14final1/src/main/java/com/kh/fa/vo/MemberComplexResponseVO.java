package com.kh.fa.vo;

import java.util.List;

import com.kh.fa.dto.MemberDto;

import lombok.Data;

// 회원 복합 검색의 결과가 저장될 클래스
@Data
public class MemberComplexResponseVO {
	private List<MemberDto> memberList; // 검색 결과
	private boolean isLast; // 다음 항목이 존재하는가
	private int count; // 개수는 몇개인가
//	private Integer beginRow, endRow; // 조회한 행은 어디부터 어디까지인가 >> 페이징을 안하면 문제가 될 수 있음
	
}
