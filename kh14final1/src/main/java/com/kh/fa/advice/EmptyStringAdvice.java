package com.kh.fa.advice;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

//@ControllerAdvice(대상 지정과 관련된 옵션)
//@ControllerAdvice(basePackages = {"com.kh.spring06.controller"})
@ControllerAdvice(annotations = {Controller.class})
public class EmptyStringAdvice { // 제목 > 기능 설명, 비어있는 문자열에 대한 간섭
	
	// 이 컨트롤러에서 파라미터를 처리하는 규칙을 변경 
	//  >> 이러한 것을 스프링에서 "간섭"이라고함 (나중에 Spring AOP가 됨)
	// - 비어있는 문자열("")을 null로 변환하도록 처리
	// - 장점 : 직접 메소드를 고치지 않고 처리 방식을 변경할 수 있다
	// - 단점 : 다른 컨트롤러에 또 써야 한다 >> 사용 방식 변환으로 해결 가능
	// - 해결책 : 모든 컨트롤러에 적용할 수 있는 신규 도구 사용(ControllerAdvice)	
	
	@InitBinder // 컨트롤러가 처리하기 전(전처리) 작업을 지정
	public void initBinder(WebDataBinder binder) {
		// Binder에 어떤 도구를 설정하느냐에 따라 처리 규칙이 바뀜
		// binder.registerCustomEditor(자료형,도구);
		// 문자열 : String.class , Trim : 공백제거의미
		// true >> EmptyAsNull , false >> EmptyAsnotNull
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
}
