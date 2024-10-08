package com.kh.fa.vo;

import com.kh.fa.dto.CertDto;

import lombok.Data;

@Data
public class MemberFindPwVO {
	private CertDto certDto; // 아이디의 주인인지 확인을 위한 인증번호 요구
	private String memberId; // 아이디
	private String changePw; // 바꿀 비밀번호
}
