package com.kh.fa.interceptor;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// 비관리자의 관리자 기능 접근을 차단하는 인터셉터

@Service
public class AdminInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {	
		// 관리자 = 현재 사용자의 세션에 들어있는 createdLevel이 관리자인 경우
		
		HttpSession session = request.getSession();
		String createdLevel = (String) session.getAttribute("createdLevel");
		
		// 관리자 레벨 확인
		//boolean isAdmin = createdLevel.equals("관리자"); 
		// createdLevel이 null이면 에러 발생 >> null.equals("관리자");
		boolean isAdmin = createdLevel != null && createdLevel.equals("관리자");
		//boolean isAdmin = "관리자".equals(createdLevel);
		// 이건 createdLevel이 null이어도 에러가 생기지 않는다
		//boolean isAdmin = createdLevel == "관리자"; // 문자열 비교는 ==이 안좋다.....
		
		if(isAdmin) {
			return true;
		}
		else {
			//response.sendRedirect("/에러페이지주소"); // 관리자 아니면 저리가
			response.sendError(403, "You must check correct status or not."); 
			// 403 Forbidden : 관리자 권한없음
			return false;
		}
				
	}
		
}
