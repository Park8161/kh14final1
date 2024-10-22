package com.kh.fa.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

// 등록안함
@Data // 세터 게터 생성자 만들기 (Lombok)
public class MemberDto {
	private String memberId;
	private String memberPw;
	private String memberName;
	private String memberEmail;
	private String memberPost;
	private String memberAddress1, memberAddress2;
	private String memberContact;
	private String memberBirth;
	private String memberLevel;
	private Integer memberPoint;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date memberJoin;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date memberLogin;
	private Integer memberReliability;
}
