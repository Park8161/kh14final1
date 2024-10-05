package com.kh.fa.vo;

import lombok.Data;

// 로그인에 필요한 정보
@Data
public class MemberLoginRequestVO {
	private String memberId;
	private String memberPw;
	private String accessToken; // 나중에 들고올 토큰 정보
}
