package com.kh.fa.error;

// 내가 만드는 예외 클래스
// - RuntimeException을 자바에서는 unchecked exception이라고 부른다
// - RuntimExcetption을 상속받으면 별도의 예외 처리 절차를 작성하지 않아도 됨
// - 다 아는 사람들끼리 개발을 편하게 할 수 있다

// 생길 때 마다 만들어지므로 등록(ex. @Service)을 안한다

// public class TargetNotFoundException extends Exception
// 아래 코드가 위에 비해서 작업량이 더 적고 플랜비를 안적어도 됨
public class TargetNotFoundException extends RuntimeException {
	
	// 기본생성자
	public TargetNotFoundException() {}
	
	// 예외메세지를 전달받는 생성자
	public TargetNotFoundException(String msg) {
		super(msg);
	}
}
