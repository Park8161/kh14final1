package com.kh.fa.service;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kh.fa.dao.CertDao;
import com.kh.fa.dao.MemberDao;
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
	@Autowired
	private MemberDao memberDao;
	
	
	// 인증메일 발송 서비스(+MIME MESSAGE)
	public void sendCert(String email) throws MessagingException, IOException {
		// [3] 
		// 담아야 할 정보 설정 : 수신자(to), 제목(subject), 내용(text), 참조(cc), 숨은참조(bcc)	
		
		// 인증 번호 생성
		String value = randomService.generateNumber(6);
		
		// 메세지 생성
//		SimpleMailMessage message = new SimpleMailMessage();
		MimeMessage message = sender.createMimeMessage(); // sender를 이용하여 전송 가능한 마임메세지 객체를 생성한다
		MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8"); // 메세지에 정보를 설정해주는 도우미 설정
		// 도우미를 통해 메세지에 정보 설정
		helper.setTo(email); // 메일을 보낼 이메일
		helper.setSubject("[KH정보교육원] 인증번호 안내"); // 제목
//		message.setText("인증번호는 [ "+value+" ] 입니다"); // 내용
//		message.setCc("nk5959@naver.com"); // 참조
//		message.setBcc("nk5959@naver.com"); // 숨은참조
		
		// 외부에 만들어놓은 HTML 문서를 불러와서 첨부
		// - src에 만든 구성요소들을 쉽게 찾을 수 있도록 ClassPathResource라는 클래스를 제공
		ClassPathResource template = new ClassPathResource("templates/email-cert.html");
		File target = template.getFile();
		
		StringBuffer buffer = new StringBuffer(); // 문자열 저장소 생성
		Scanner sc = new Scanner(target); // 파일을 읽을 수 있는 Scanner 생성
		while(sc.hasNextLine()) { // 읽을 수 있는 줄이 있다면
			String line = sc.nextLine(); // 한 줄을 읽으세요
			buffer.append(line); // 저장소에 추가
		}
		sc.close(); // 스캐너 종료
		
		// 읽어들인 데이터(buffer)에서 원하는 부분을 찾아서 교체하도록 코드 작성
		// 1. 문자열 탐색 및 치환(replace)
		// 2. HTML 해석기 사용(Jsoup)
		Document document = Jsoup.parse(buffer.toString()); // 문자열을 HTML로 해석
		Element certNumber = document.getElementById("cert-number"); // #receiver 탐색
//		Elements certNumber = document.select("#cert-number");
		certNumber.text(value);
		
//		Element moveLink = document.getElementById("move-link"); // #move-link 탐색 
//		moveLink.attr("href", url); // 속성 교체
		
		helper.setText(document.toString(), true);
		
		// 메세지 전송
		sender.send(message);
		
		// DB 기록 남기기
		certDao.delete(email); // 이전 기록 제거
		CertDto certDto = new CertDto();
		certDto.setCertEmail(email);
		certDto.setCertNumber(value);
		certDao.insert(certDto);
		
	}

	// 임시 비밀번호 발급 및 메일 전송
	public void sendTempPw(String memberId, String memberEmail) throws IOException, MessagingException {
		// 임시 비밀번호 발급
//		String tempPassword = "Aa1!1234567Aa1!1";
		String tempPassword = randomService.generateString(12);
		memberDao.updateMemberPw(memberId, tempPassword);
		
		// 이메일 템플릿 불러와 정보 설정 후 발송
		ClassPathResource resource = new ClassPathResource("templates/temp-pw.html");
		File target = resource.getFile();
		Document document = Jsoup.parse(target);
		Element element = document.getElementById("temp-pw");
		element.text(tempPassword);
		
		// 메세지 생성
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
		helper.setTo(memberEmail);
		helper.setSubject("[KH정보교육원] 임시 비밀번호 안내");
		helper.setText(document.toString(), true);
		
		// 메세지 발송
		sender.send(message); 
	}

	// 비밀번호 재설정 링크 메일 전송
	public void sendResetPw(String memberId, String memberEmail) throws IOException, MessagingException {
		
		// 이메일 템플릿 불러와 정보 설정 후 발송
		ClassPathResource resource = new ClassPathResource("templates/reset-pw.html");
		File target = resource.getFile();
		Document document = Jsoup.parse(target);
		
		Element memberIdWrapper = document.getElementById("member-id");
		memberIdWrapper.text(memberId);
		
		// 돌아올 링크 주소를 생성하는 코드
		
		// - 인증번호 생성
		String certNumber = randomService.generateNumber(6);
		certDao.delete(memberEmail);
		CertDto certDto = new CertDto();
		certDto.setCertEmail(memberEmail);
		certDto.setCertNumber(certNumber);
		certDao.insert(certDto);
		
		// - 접속 주소 생성 : 도구를 이용하여 현재 실행중인 주소를 자동으로 읽어오도록 처리
//		String url = "http://localhost:8080/member/resetPw"
//				+ "?certNumber="+certNumber+"&certEmail="+memberEmail+"&memberId="+memberId;
		
		String url = ServletUriComponentsBuilder
//						.fromCurrentContextPath()//http://localhost:8080
						.fromHttpUrl("http://localhost:3000")
						.path("/#/member/resetpw")//나머지경로
						.queryParam("certNumber", certNumber)//파라미터
						.queryParam("certEmail", memberEmail)//파라미터
						.queryParam("memberId", memberId)//파라미터
						.build().toUriString();//문자열변환
		
		Element resetUrlWrapper = document.getElementById("reset-url");
		resetUrlWrapper.attr("href", url);
		
		// 메세지 생성
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
		helper.setTo(memberEmail);
		helper.setSubject("[KH중고나라] 비밀번호 재설정 안내");
		helper.setText(document.toString(), true);
		
		// 메세지 발송
		sender.send(message); 
	}
	
}
