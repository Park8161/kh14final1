package com.kh.fa.vo;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kh.fa.advice.JsonEmptyIntegerToNullDeserializer;
import com.kh.fa.advice.JsonEmptyStringToNullDeserializer;

import lombok.Data;

// 복합 검색 요청을 위한 VO
@Data
public class MemberComplexRequestVO {
	// 클래스에 붙이면 읽는 범위가 달라져서 항목을 제대로 타게팅하지 않기에 항목마다 해주어야 한다
	// JsonDeserializer<String> 이라서 String만 해당 사항 있음
	// Integer 타입을 위해 JsonDeserializer<Integer> 생성
	@JsonDeserialize(using = JsonEmptyStringToNullDeserializer.class)
	private String memberId;
	@JsonDeserialize(using = JsonEmptyStringToNullDeserializer.class)
	private String memberNickname;
	@JsonDeserialize(using = JsonEmptyStringToNullDeserializer.class)
	private String memberBirth;
	@JsonDeserialize(using = JsonEmptyStringToNullDeserializer.class)
	private String memberContact;
	@JsonDeserialize(using = JsonEmptyStringToNullDeserializer.class)
	private String memberEmail;
	@JsonDeserialize(using = JsonEmptyIntegerToNullDeserializer.class)
	private Integer minMemberPoint, maxMemberPoint;
	@JsonDeserialize(using = JsonEmptyStringToNullDeserializer.class)
	private String memberAddress;
	@JsonDeserialize(using = JsonEmptyStringToNullDeserializer.class)
	private String beginMemberJoin, endMemberJoin;
	@JsonDeserialize(using = JsonEmptyStringToNullDeserializer.class)
	private String beginMemberLogin, endMemberLogin;
	@JsonDeserialize(using = JsonEmptyIntegerToNullDeserializer.class)
	private Integer beginRow, endRow;
	
	private List<String> memberLevelList;
	private List<String> orderList;
}
