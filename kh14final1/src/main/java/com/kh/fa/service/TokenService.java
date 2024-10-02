package com.kh.fa.service;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.fa.configuration.TokenProperties;
import com.kh.fa.vo.MemberClaimVO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {
	
	@Autowired
	private TokenProperties tokenProperties;
	
	// 토큰 생성 메소드
	public String create(MemberClaimVO vo) {
		// 키 생성
		SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
		
		// 만료시간 계산
		Calendar c = Calendar.getInstance(); // 현재 시간 추출
		Date now = c.getTime(); // 현재 시간을 now로 지칭
		c.add(Calendar.MINUTE, tokenProperties.getExpire()); // 설정한 만료시간 후 만료
		Date limit = c.getTime(); // 만료 설정을 limit로 지칭
		
		// 토큰
		return Jwts.builder()
						// 정보 설정
						.signWith(key) // 서명에 사용할 키 정보
						.expiration(limit) // 만료 시간 설정(java.util.Date)
						.issuer(tokenProperties.getIssuer()) // 발급자 정보(issuer) issuer는 보통 발급사를 지칭
						.issuedAt(now)
						.claim("memberId", vo.getMemberId())
						.claim("memberLevel", vo.getMemberLevel())
					.compact();
	}
	
	// 토큰 검증 메소드
	public MemberClaimVO check(String token) {
		// 키 생성
		SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
		
		// 토큰 해석 - claim 정보를 읽는 것이 목적
		Claims claims = (Claims) Jwts.parser() // 해석 도구를 준비해서
										.verifyWith(key) // 증명을 위한 열쇠를 설정하고 (verify(검증) : valid(검정,검사)와 다름, 검사해서 이게 맞다고 증명하는 것)
										.requireIssuer(tokenProperties.getIssuer()) // 발행자 정보까지 일치하는지 확인
									.build() // 위의 정보를 바탕으로 도구를 생성
										.parse(token) // 토큰을 해석하고
										.getPayload(); // 핵심 정보를 가져오세요
		
		// 결과 생성 및 반환
		MemberClaimVO vo = new MemberClaimVO();
		vo.setMemberId((String) claims.get("memberId")); 
		vo.setMemberLevel((String) claims.get("memberLevel"));
		// claims는 자료형이 Object라서 다운캐스팅으로 String 변환 필요
		
		return vo;
	}
	
}
