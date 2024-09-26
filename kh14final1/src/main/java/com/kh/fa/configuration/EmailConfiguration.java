package com.kh.fa.configuration;

import java.text.DecimalFormat;
import java.util.Properties;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfiguration {
	
	@Autowired
	private CustomEmailProperties customEmailProperties;
	
	// 수동으로 객체를 등록하기 위해서는 @Bean을 사용한다
	@Bean
//	public JavaMailSenderImpl sender() { // 참치김밥
	public JavaMailSender sender() { // 김밥(업캐스팅)
		// [1] 도구 생성
		// [2] 이용할 서드파티 서비스(Gmail) 정보 입력 
		// [3] 메세지 생성
		// [4] 전송
		
		// [1]
		JavaMailSenderImpl sender = new JavaMailSenderImpl();

		// [2]
		// - 필수 설정
		sender.setHost(customEmailProperties.getHost()); // 이용할 업체의 호스트 정보
		sender.setPort(customEmailProperties.getPort()); // 이용할 업체의 포트번호
		sender.setUsername(customEmailProperties.getUsername()); // 계정 
		sender.setPassword(customEmailProperties.getPassword()); // 계정 비밀번호(공백 입력 불가)
		
		//- 필수 설정에 장착할 설정
		Properties props = new Properties(); // 자바에서 제공하는 문자열 Key=Value 저장소
		// 문자열만 저장하능한 맵이라고 봐도 됨
		props.setProperty("mail.smtp.auth", "true"); // 인증 후 사용하도록 설정
		props.setProperty("mail.smtp.debug", "true"); // 디버깅 허용 설정
		props.setProperty("mail.smtp.starttls.enable", "true"); // TLS 사용 설정
		props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2"); // TLS 버전 설정(1.1은 Mac에서 사용불가)
		props.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com"); // 신뢰할 수 있는 주소로 등록
		sender.setJavaMailProperties(props);
		
		return sender;
	}
}
