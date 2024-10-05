package com.kh.fa.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kh.fa.interceptor.AdminInterceptor;
import com.kh.fa.interceptor.MemberInterceptor;
import com.kh.fa.interceptor.TestInterceptor;

/*
커스텀 설정 파일

@COnfiguration으로 등록 후 필요하다면 상속을 받아서 자격을 획득해야함
*/

//@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {
	
	@Autowired
	private TestInterceptor testInterceptor;
	@Autowired
	private MemberInterceptor memberInterceptor;
	@Autowired
	private AdminInterceptor adminInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 목표 : 모든 페이지가 실행 되기 전에 testInterceptor가 간섭하게 하고 싶다!
		//registry.addInterceptor(testInterceptor)
		//				.addPathPatterns("/**");
		// 윗줄 주석처리하면 해제 끝
		// "/**" : / 으로 시작하는 모두 다
		// "/poketmon/**" : /poketmon/ 으로 시작하는 모두 다
		
		// 회원 검사 인터셉터 등록
		// - 와일드카드(*)는 어떤 글자가 와도 관계 없는 자리라는 뜻
		// - 와일드카드가 1개면 현재 엔드포인트(/)에서 글자만 무관이란 뜻
		//   (ex) /admin/* : /admin/home은 되지만 /admin/member/list는 안됨
		// - 와일드카드가 2개면 하위 엔드포인트(/)를 모두 포함하는 범위에서 적용
		
		// 회원 검사 인터셉터 설정
		registry.addInterceptor(memberInterceptor)
				.addPathPatterns( // 접속불가
				//"/admin/**", // 모든 관리자 페이지
				//"/emp/**", // 모든 사원 페이지
				//"/book/**", // 모든 도서 페이지
				"/member/**", // 모든 회원 페이지
				"/board/**", // 게시판 페이지
				"/rest/board/**", // 게시글 REST 서비스
				"/rest/member/profile", // 프로필 변경 서비스
				"/rest/reply/**" // 댓글 페이지
				) // 해당 설정은 화이트 리스트 방식
				.excludePathPatterns( // 접속가능
				"/member/join*", // 가입 관련 페이지				
				"/member/login", // 로그인 페이지
				"/member/goodbye", // 탈퇴 완료 페이지	
				"/member/block", // 차단 페이지
				"/member/findPw*",
				"/member/resetPw*",
				"/board/list", // 게시글 목록
				"/board/detail", // 게시글 상세
				"/member/image", // 회원 프로필 이미지는 로그인 안해도 볼 수 있도록 조정
				"/rest/board/check", // 좋아요 확인 페이지
				"/rest/reply/list", // 댓글 확인 페이지
				"/rest/reply/list/paging" // 댓글 확인 페이지
				);
		
		// 다 막아놓고 일부 허용 : 화이트리스트 방식
		// 다 허용해놓고 일부 막음 : 블랙리스트 방식

		// 관리자 검사 인터셉터 설정
		registry.addInterceptor(adminInterceptor)
				.addPathPatterns(
						"/admin/**",
						"/rest/poketmon/status*", // 통게 관련 페이지
						"/rest/book/status*", // 통게 관련 페이지
						"/rest/emp/status*", // 통게 관련 페이지
						"/rest/member/status*" // 통게 관련 페이지
						)
				.excludePathPatterns(
						
				);
		
		
		
		
	}
	
}
