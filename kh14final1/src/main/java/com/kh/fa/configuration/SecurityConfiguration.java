package com.kh.fa.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration {
	
	@Bean
	public PasswordEncoder encoder() {
//	public BCryptPasswordEncoder encoder() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		//원하는 설정 추가
		return encoder;
	}
}
