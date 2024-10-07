package com.kh.fa.service;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.fa.configuration.TokenProperties;
import com.kh.fa.dao.MemberTokenDao;
import com.kh.fa.dto.MemberTokenDto;
import com.kh.fa.vo.MemberClaimVO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {
	
	public static final String BEARER_PREFIX = "Bearer ";
	
	@Autowired
	private TokenProperties tokenProperties;
	@Autowired
	private MemberTokenDao memberTokenDao;
	
	// 토큰 생성 메소드
	public String createAccessToken(MemberClaimVO vo) {
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
	
	// Bearer 토큰인지 검사하는 메소드
	public boolean isBearerToken(String token) {
		return token != null && token.startsWith(BEARER_PREFIX);
	}	
	
	// Bearer를 제거하는 메소드
	public String removeBearer(String token) {
		// return token.substring(0,7);
		// return token.substring(0, "Bearer ".length()); // 원하는 문자열의 길이만큼
		return token.substring(0, BEARER_PREFIX.length()); // 문자열의 오타 방지를 위한 상수값 변환 후 대입
	}	
	
	// 리프레시 토큰 생성 메소드
	// - 긴 시간 동안 사용할 수 있도록 처리
	// - DB에 이 토큰의 정보를 저장해서 나중에 비교가 가능하도록 처리
	public String createRefreshToken(MemberClaimVO vo) {
		// 키 생성
		SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
		
		// 만료시간 계산
		Calendar c = Calendar.getInstance(); // 현재 시간 추출
		Date now = c.getTime(); // 현재 시간을 now로 지칭
		c.add(Calendar.MONTH, 1); // 설정한 만료시간 후 만료, 이 숫자는 거의 바꿀 일이 없어 그냥 넣어주어도 괜찮다
		Date limit = c.getTime(); // 만료 설정을 limit로 지칭
		
		// 토큰
		String token = Jwts.builder()
							// 정보 설정
							.signWith(key) // 서명에 사용할 키 정보
							.expiration(limit) // 만료 시간 설정(java.util.Date)
							.issuer(tokenProperties.getIssuer()) // 발급자 정보(issuer) issuer는 보통 발급사를 지칭
							.issuedAt(now)
							.claim("memberId", vo.getMemberId())
							.claim("memberLevel", vo.getMemberLevel())
						.compact();
		
		// DB 저장
		MemberTokenDto memberTokenDto = new MemberTokenDto();
		memberTokenDto.setTokenTarget(vo.getMemberId());
		memberTokenDto.setTokenValue(token);
		memberTokenDao.insert(memberTokenDto);
		
		return token;
	}
	
}
