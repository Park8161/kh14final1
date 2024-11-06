package com.kh.fa.restcontroller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.fa.configuration.CustomCertProperties;
import com.kh.fa.dao.CertDao;
import com.kh.fa.dto.CertDto;
import com.kh.fa.service.EmailService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/cert")
@CrossOrigin
public class CertRestController {
	
	@Autowired
	private EmailService emailService;
	@Autowired
	private CertDao certDao;
	@Autowired
	private CustomCertProperties customCertProperties;
	
	// 사용자가 요구하는 이메일로 인증메일을 발송하는 기능
	@PostMapping("/send/{certEmail}")
	public void send(@PathVariable String certEmail) throws MessagingException, IOException {
		emailService.sendCert(certEmail);
	}
	
	// 사용자가 입력한 인증번호가 유효한지를 판정하는 기능
	@PostMapping("/check")
	public boolean check(@RequestBody CertDto certDto) {
		boolean result = certDao.check(certDto, customCertProperties.getExpire()); // 해당 숫자는 검사시간(분) 무조건 관리자가 관리해야함
		if(result) {
			certDao.delete(certDto.getCertEmail()); // 인증번호 삭제
		}
		return result; 
	}
	
}
