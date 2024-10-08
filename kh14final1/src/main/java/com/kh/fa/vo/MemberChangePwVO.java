package com.kh.fa.vo;

import lombok.Data;

@Data
public class MemberChangePwVO {
	private String currentPw; // 기존 비밀번호
	private String changePw; // 바꿀 비밀번호
}
