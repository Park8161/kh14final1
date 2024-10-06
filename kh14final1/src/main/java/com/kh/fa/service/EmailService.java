
package com.kh.fa.service;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kh.fa.dao.CertDao;
import com.kh.fa.dto.CertDto;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
		
	@Autowired
	private JavaMailSender sender;
	
	@Autowired
	private RandomService randomService;
	
	@Autowired
	private CertDao certDao;
	
	//인증메일 발송 서비스
	public void sendCert(String email){
		//인증번호 생성
		String value = randomService.generateNumber(6);
		
		//메세지 생성
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("행운의 인증번호를 드립니다!");
		message.setText("행운의 인증번호는" +value+"입니다!");
		
		//메세지 전송
		sender.send(message);
		
		//DB 기록 남기기
		certDao.delete(email);
		CertDto certDto = new CertDto();
		certDto.setCertEmail(email);
		certDto.setCertNumber(value);
		certDao.insert(certDto);
	}
	
	//비밀번호 재설정 메일 발송 기능
	public void sendResetPw(String memberId, String memberEmail) throws IOException, MessagingException {
	ClassPathResource resource = new ClassPathResource("templates/reset-pw.html");
	File target = resource.getFile();
	Document document = Jsoup.parse(target);
	
	Element memberIdWrapper = document.getElementById("member-id");
	memberIdWrapper.text(memberId);
	
	//인증번호 생성 코드
	String certNumber = randomService.generateNumber(6);
	certDao.delete(memberEmail);
	CertDto certDto = new CertDto();
	certDto.setCertEmail(memberEmail);
	certDto.setCertNumber(certNumber);
	certDao.insert(certDto);
	
	
	//-접속주소생성: 도구를 이용해 실행중인 주소를 자동으로 읽어오도록한다
	String url = ServletUriComponentsBuilder
			.fromCurrentContextPath() //이주소가 로칼호스트8080
			.path("/member/resetPw")
			.queryParam("certNumber", certNumber)
			.queryParam("certEmail", memberEmail)
			.queryParam("memberId", memberId)
			.build().toUriString();//문자열변환
	
	Element resetUrlWrapper = document.getElementById("reset-url");
	resetUrlWrapper.attr("href", url);
	
	//메세지
	MimeMessage message = sender.createMimeMessage();
	MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
	helper.setTo(memberEmail);
	helper.setSubject("[KH 대학교] 비밀번호 재설정 안내");
	helper.setText(document.toString(), true);

	//전송
	sender.send(message);
		
	}
}
