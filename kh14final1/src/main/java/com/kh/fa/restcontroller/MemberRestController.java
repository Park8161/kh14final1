package com.kh.fa.restcontroller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.fa.configuration.CustomCertProperties;
import com.kh.fa.dao.CertDao;
import com.kh.fa.dao.MemberDao;
import com.kh.fa.dto.CertDto;
import com.kh.fa.dto.MemberDto;
import com.kh.fa.error.TargetNotFoundException;
import com.kh.fa.service.EmailService;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberBlockRequestVO;
import com.kh.fa.vo.MemberBlockResponseVO;
import com.kh.fa.vo.MemberCertVO;
import com.kh.fa.vo.MemberClaimVO;
import com.kh.fa.vo.MemberComplexRequestVO;
import com.kh.fa.vo.MemberComplexResponseVO;
import com.kh.fa.vo.MemberLoginRequestVO;
import com.kh.fa.vo.MemberLoginResponseVO;
import com.kh.fa.vo.MypageVO;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/member")
@CrossOrigin(origins = {"http://localhost:3000"}) // CORS 해제 설정
public class MemberRestController {

	@Autowired
	private MemberDao memberDao;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private CertDao certDao;
	@Autowired
	private CustomCertProperties customCertProperties;
	
	// 여태까지 배운대로라면 복합검색도 GET으로 구현해야 한다
	// 하지만 보내야 하는 데이터가 너무 많아서 GET으로 구현하는 것은 어려움이 있다
	// 해결을 위해 HTTP BODY를 가지는 방식 중 POST를 사용한다
	@PostMapping("/search") // 회원가입과 구분하기 위해 주소 규칙을 깬다
	public MemberComplexResponseVO search(@RequestBody MemberComplexRequestVO vo){
		
		int count = memberDao.complexSearchCount(vo);
		// 마지막 = 페이징을 안쓰는 경우 or 검색 개수가 종료번호보다 작거나 같은 경우
		boolean last = vo.getEndRow() == null || count <= vo.getEndRow();
		
		MemberComplexResponseVO response = new MemberComplexResponseVO();
		response.setMemberList(memberDao.complexSearch(vo));
		response.setCount(count);
		response.setLast(last);
		return response;
	}
	
	// 로그인 관련 처리 기능
	// - 로그인은 등록/목록(검색)/상세/수정/삭제 중에 해당되는게 없다
	// - 기본적인 기능이 아닌 특수한 목적을 가진 변형된 기능
	// - 표준 규칙을 따르기가 어렵다
	@PostMapping("/login")
	public MemberLoginResponseVO login(@RequestBody MemberLoginRequestVO vo) {
		// 회원 조회
		MemberDto memberDto = memberDao.selectOne(vo.getMemberId());
		if(memberDto == null) throw new TargetNotFoundException("아이디 없음");
		
		// 비밀번호 비교(암호화 여부에 따라 코드가 달라질 수 있음에 주의)
		boolean isValid = vo.getMemberPw().equals(memberDto.getMemberPw());
//		boolean isValid = encoder.matches(vo.getMemberPw(), memberDto.getMemberPw()); // 암호화시 사용
		
		if(isValid) { // 로그인 성공
			MemberLoginResponseVO response = new MemberLoginResponseVO();
			
			// 아이디, 등급 - 화면을 위한 정보
			response.setMemberId(memberDto.getMemberId());
			response.setMemberLevel(memberDto.getMemberLevel());
			
			// 아이디, 등급 + 토큰 - 서버를 위한 정보
			MemberClaimVO claimVO = new MemberClaimVO(); // 아이디
			claimVO.setMemberId(memberDto.getMemberId()); // 등급
			claimVO.setMemberLevel(memberDto.getMemberLevel());
			response.setAccessToken(tokenService.create(claimVO)); // 액세스토큰
			
			memberDao.updateMemberLogin(memberDto.getMemberId()); // 최종 로그인 시각 갱신
			
			return response;
		}
		else { // 로그인 실패
			throw new TargetNotFoundException("ㅎㅎ");
		}
	}
	
	// 차단 목록 검색
	@PostMapping("/block")
	public MemberBlockResponseVO block(@RequestBody MemberBlockRequestVO vo) {
		int count = memberDao.countWithPaging(vo);
		boolean last = vo.getEndRow() == null || count <= vo.getEndRow();
		MemberBlockResponseVO response = new MemberBlockResponseVO();
		response.setMemberBlockList(memberDao.selectListByPaging(vo));
		response.setCount(count);
		response.setLast(last);
		return response;
	}
	
	// 마이페이지
	@GetMapping("/{memberId}")
	public MypageVO detail(@PathVariable String memberId) {
		MypageVO response = new MypageVO();
		MemberDto memberDto = memberDao.selectOne(memberId);
		if(memberDto == null) throw new TargetNotFoundException();
//		BlockDto blockDto = blockDao.selectOne(memberId);
//		ProductDto productDto = productDao.selectOne(memberId); 
//		if(productDto == null) throw new TargetNotFoundException();
		response.setMemberDto(memberDto);
//		response.setBlockDto(blockDto);
//		response.setProductDto(productDto);
		return response;
	}
	
	// 개인정보 변경
	@PutMapping("/edit")
	public void edit(@RequestBody MemberDto memberDto) {
		boolean result = memberDao.update(memberDto);
		if(result == false) throw new TargetNotFoundException();
	}
	
	// 비밀번호 변경
	@GetMapping("/memberId/{memberId}/memberEmail/{memberEmail}")
	public void findPw(@Parameter(required = true) @PathVariable String memberId,
			@Parameter(required = true) @PathVariable String memberEmail) throws IOException, MessagingException {
		// 아이디로 회원 정보 조회
		MemberDto memberDto = memberDao.selectOne(memberId);
		if(memberDto == null) throw new TargetNotFoundException();
		
		// 이메일 비교
		if(!memberEmail.equals(memberDto.getMemberEmail()))
			throw new TargetNotFoundException();
		
		// 템플릿을 불러와 재설정 메일 발송
		emailService.sendResetPw(memberId, memberEmail);
	}
	
	// 비밀번호 재설정
	@PostMapping("/resetPw")
	public void resetPw(@RequestBody MemberCertVO memberCertVO) {
		CertDto certDto = memberCertVO.getCertDto();
		MemberDto memberDto = memberCertVO.getMemberDto();
		// 인증 정보 확인
		boolean isValid = certDao.check(certDto, customCertProperties.getExpire());
		if(!isValid) throw new TargetNotFoundException("인증안됨");
		//인증성공시 인증번호 삭제(1회접근페이지)
		certDao.delete(certDto.getCertEmail());
		
		//비밀번호변경
		memberDao.updateMemberPw(memberDto);		
	}
	
	// 회원 탈퇴 : 넷 다 비밀번호 받는 걸 어떻게 결정할지부터가 우선되어야 함
}
