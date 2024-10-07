package com.kh.fa.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kh.fa.error.TargetNotFoundException;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberClaimVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

// 회원만 통과시키는 인터셉터
// - 기존과 달라진 점은 HttpSession으로 사용자를 조회하는 것이 아니라
// - Authorization이라는 이름의 헤더를 조사하여 검증을 해야한다
@Slf4j
@Service
public class MemberInterceptor implements HandlerInterceptor {
	
	@Autowired
	private TokenService tokenService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		// [1] OPTIONS 요청이 들어오면 통과시킨다
		// - options는 통신이 가능한지 확인하는 선발대 형식의 요청 : BE와 FE가 분리되어 통신이 불확실할 때 사용
		// - CORS 상황이거나 GET, HEAD, POST와 같은 일반적인 요청이 아니면 발생
		String method = request.getMethod();
		if(method.toLowerCase().equals("options")) return true;
		
		// [2] Authorization 헤더를 검사
		// (1) Authorization 헤더가 반드시 존재해야 함
		// (2) 헤더의 값은 Bearer로 시작해야 함
		// (3) 해석했을 때 memberId와 memberLevel이 나와야 함 (+유효한 토큰)
		// (4) 이 중 하나라도 일치하지 않는다면 정상적인 로그인이 아니라고 간주
		try {
			String token = request.getHeader("Authorization"); // 헤더의 Authorization을 읽어
			if(token == null) throw new TargetNotFoundException("헤더 없음"); // (1)
			if(tokenService.isBearerToken(token) == false) throw new Exception("Bearer 토큰이 아님"); // (2)
			String realToken = tokenService.removeBearer(token); // Bearer 제거
			MemberClaimVO claimVO = tokenService.check(realToken); // (3)
//			log.info("아이디 = {}, 등급 = {}", claimVO.getMemberId(), claimVO.getMemberLevel());
			return true;
		}
		catch(Exception e) { // (4)
			response.sendError(401); // Unauthorized
			return false;
			
		}
	}
}
