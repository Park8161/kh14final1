package com.kh.fa.vo;

import lombok.Data;

// 로그인의 결과 정보
@Data
public class MemberLoginResponseVO {
	private String memberId; // 로그인 성공한 사용자의 아이디
	private String memberLevel; // 로그인 성공한 사용자의 등급
	private String accessToken; // 토큰을 이용해 로그인 정보를 확인 후 통과/거절
}
