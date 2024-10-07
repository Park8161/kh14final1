package com.kh.fa.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kh.fa.error.TargetNotFoundException;

import io.jsonwebtoken.ExpiredJwtException;

// 스프링 부트에서 발생하는 각종 예외들을 처리하는 간섭 객체
//@RestControllerAdvice(basePackages = {"com.kh.spring12.restcontroller"})
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice {
	
	// TargetNotFoundException은 404로 처리
	// 나머지는 500번으로 처리하되 메세지 제거(서버에만 출력)
	
	@ExceptionHandler(TargetNotFoundException.class)
	public ResponseEntity<String> error404() {
		// return ResponseEntity.ok(); // 200
		// return ResponseEntity.status(404).build(); // 404
		// return ResponseEntity.notFound().build(); // 404 >> notFound()는 .body()를 쓸 수 없어 메세지 못 남김
		return ResponseEntity.status(404).body("target not found");
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> error500(Exception e){
		e.printStackTrace(); // 콘솔 창에 오류 알림
		// return ResponseEntity.status(500).build(); // 500
		// return ResponseEntity.internalServerError().build(); // 500
		return ResponseEntity.internalServerError().body("server error");
	}
	
	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<String> errorTokenExpire(Exception e) {
		e.printStackTrace();
		return ResponseEntity.status(404).body("token expired");
	}
	
}
