package com.kh.fa.restcontroller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.fa.configuration.CustomCertProperties;
import com.kh.fa.dao.BlockDao;
import com.kh.fa.dao.CertDao;
import com.kh.fa.dao.MemberDao;
import com.kh.fa.dao.MemberTokenDao;
import com.kh.fa.dao.ProductDao;
import com.kh.fa.dto.BanDto;
import com.kh.fa.dto.BlockDto;
import com.kh.fa.dto.CertDto;
import com.kh.fa.dto.MemberDto;
import com.kh.fa.dto.MemberTokenDto;
import com.kh.fa.error.TargetNotFoundException;
import com.kh.fa.service.EmailService;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberBlockRequestVO;
import com.kh.fa.vo.MemberBlockResponseVO;
import com.kh.fa.vo.MemberChangePwVO;
import com.kh.fa.vo.MemberClaimVO;
import com.kh.fa.vo.MemberComplexRequestVO;
import com.kh.fa.vo.MemberComplexResponseVO;
import com.kh.fa.vo.MemberExitRequestVO;
import com.kh.fa.vo.MemberFindPwVO;
import com.kh.fa.vo.MemberLoginRequestVO;
import com.kh.fa.vo.MemberLoginResponseVO;
import com.kh.fa.vo.MypageVO;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/member")
@CrossOrigin // CORS 해제 설정
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
	@Autowired
	private MemberTokenDao memberTokenDao;
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private BlockDao blockDao;
	@Autowired
	private ProductDao productDao;
	
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
//		boolean isValid = vo.getMemberPw().equals(memberDto.getMemberPw());
		boolean isValid = encoder.matches(vo.getMemberPw(), memberDto.getMemberPw()); // 암호화시 사용  
		
		if(isValid) { // 로그인 성공
			MemberLoginResponseVO response = new MemberLoginResponseVO();
			
			// 아이디, 등급 - 화면을 위한 정보
			response.setMemberId(memberDto.getMemberId());
			response.setMemberLevel(memberDto.getMemberLevel());
			
			// 아이디, 등급 + 토큰 - 서버를 위한 정보
			MemberClaimVO claimVO = new MemberClaimVO(); // 아이디
			claimVO.setMemberId(memberDto.getMemberId()); // 등급
			claimVO.setMemberLevel(memberDto.getMemberLevel());
			response.setAccessToken(tokenService.createAccessToken(claimVO)); // 액세스토큰
			response.setRefreshToken(tokenService.createRefreshToken(claimVO)); // 리프레시토큰
			
			memberDao.updateMemberLogin(memberDto.getMemberId()); // 최종 로그인 시각 갱신
			
			return response;
		}
		else { // 로그인 실패
			throw new TargetNotFoundException("로그인 실패");
		}
	}
	
	// (+추가) 
	// - Refresh Token으로 로그인 하는 기능
	// - 보안이 매우 취약한 기능이므로 보안을 올리기 위해 각종 장치를 추가
	// - DB검증 등...
	// - Authorization이라는 헤더에 있는 값을 읽어서 검사한 뒤 갱신 처리
	// - 검증할 수 없는 토큰 또는 기타 오류 발생 시 404 처리 
	@PostMapping("/refresh")
	public MemberLoginResponseVO refresh(@RequestHeader("Authorization") String refreshToken) {
		// [1] refreshToken이 없거나 Bearer로 시작하지 않으면 안됨
		if(refreshToken == null) throw new TargetNotFoundException("토큰 없음");
		if(tokenService.isBearerToken(refreshToken) == false) throw new TargetNotFoundException("Bearer 토큰 아님");
		
		// [2] 토큰에서 정보를 추출
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(refreshToken)); // Bearer 제거한 토큰을 이용해 유효 검사 및 회원 정보 조회
		if(claimVO.getMemberId() == null) throw new TargetNotFoundException("아이디 없음");
		if(claimVO.getMemberLevel() == null) throw new TargetNotFoundException("등급 없음");
		
		// [3] 토큰 발급 내역을 조회
		MemberTokenDto memberTokenDto = new MemberTokenDto();
		memberTokenDto.setTokenTarget(claimVO.getMemberId());
		memberTokenDto.setTokenValue(tokenService.removeBearer(refreshToken));
		MemberTokenDto resultDto = memberTokenDao.selectOne(memberTokenDto);
		if(resultDto == null) throw new TargetNotFoundException("발급 내역이 없음"); // 발급내역이 없음
		
		// [4] 기존의 리프레시 토큰 삭제
		memberTokenDao.delete(memberTokenDto);
		
		// [5] 로그인 정보 재발급
		MemberLoginResponseVO response = new MemberLoginResponseVO();
		response.setMemberId(claimVO.getMemberId());
		response.setMemberLevel(claimVO.getMemberLevel());
		response.setAccessToken(tokenService.createAccessToken(claimVO)); // 액세스토큰 재발급
		response.setRefreshToken(tokenService.createRefreshToken(claimVO)); // 리프레시토큰 재발급
		
		return response;
	}
	
	// 마이페이지 - 회원 정보 뿐만 아니라 거래 이력 및 차단 목록 등의 조회를 위하여 Dto가 아닌 VO를 전송
	@GetMapping("/mypage")
	public MemberDto mypage(@RequestHeader("Authorization") String accessToken) {
		if(tokenService.isBearerToken(accessToken) == false) throw new TargetNotFoundException("유효하지 않은 토큰");
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(accessToken));
		
		MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
		if(memberDto == null) throw new TargetNotFoundException("존재하지 않는 회원");
		memberDto.setMemberPw(null); // 비밀번호 제거
		return memberDto;
	}
	
	// 내가 액티브한 상품 목록들 조회 >> 액티브 : 좋아요, 등록상품,
	@GetMapping("/active")
	public MypageVO active(@RequestHeader("Authorization") String token) {
		// 토큰 변환
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		// 유효 검증
		MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
		if(memberDto == null) throw new TargetNotFoundException("존재하지 않는 회원");
		
		// 액티브 상품 목록 전송 준비
		MypageVO mypageVO = new MypageVO();
		mypageVO.setLikeList(productDao.selectLikeList(claimVO.getMemberId())); // 좋아요 누른 상품 목록
		mypageVO.setMyList(productDao.selectMyList(claimVO.getMemberId())); // 내가 등록한 상품 목록
		return mypageVO;
	}
	
	// 마이페이지 - 다른 회원 정보 조회를 위해 남겨둠
//	@GetMapping("/{memberId}")
//	public MypageVO detail(@PathVariable String memberId) {
//		MypageVO response = new MypageVO();
//		MemberDto memberDto = memberDao.selectOne(memberId);
//		if(memberDto == null) throw new TargetNotFoundException();
//		BlockDto blockDto = blockDao.selectOne(memberId);
//		ProductDto productDto = productDao.selectOne(memberId); 
//		if(productDto == null) throw new TargetNotFoundException();
//		response.setMemberDto(memberDto);
//		response.setBlockDto(blockDto);
//		response.setProductDto(productDto);
//		return response;
//	}
	
	// 개인정보 변경
	@PutMapping("/edit")
	public void edit(@RequestBody MemberDto memberDto) {
		MemberDto findDto = memberDao.selectOne(memberDto.getMemberId());
		boolean emailCheck = ( "인증회원".equals(memberDto.getMemberLevel()) || "안전회원".equals(memberDto.getMemberLevel())) 
							&& findDto.getMemberEmail() != null 
							&& findDto.getMemberEmail().equals(memberDto.getMemberEmail()) == false;
		if(emailCheck) { // 이메일 변경 여부를 통해 인증회원을 일반회원으로 등급 이동
			memberDto.setMemberLevel("일반회원");
		}
		boolean result = memberDao.update(memberDto);
		if(result == false) throw new TargetNotFoundException("개인정보 변경 실패");
	}
	
	// 비밀번호 찾기를 위한 이메일 전송 - 비로그인
	@GetMapping("/memberId/{memberId}/memberEmail/{memberEmail}")
	public boolean findPw(@PathVariable String memberId, @PathVariable String memberEmail) throws IOException, MessagingException {
		// 아이디로 회원 정보 조회
		MemberDto memberDto = memberDao.selectOne(memberId);
		if(memberDto == null) throw new TargetNotFoundException("아이디 정보 불일치");
		
		// 이메일 비교
		if(!memberEmail.equals(memberDto.getMemberEmail()))
			throw new TargetNotFoundException("이메일 정보 불일치");
		
		// 이메일로 인증번호 전송
		emailService.sendResetPw(memberId, memberEmail);
		
		return memberDto != null && memberEmail.equals(memberDto.getMemberEmail());
	}
	
	// 비밀번호 찾기 - 비로그인
	@PostMapping("/resetPw")
	public void resetPw(@RequestBody MemberFindPwVO findPwVO) {
		// 인증 정보 확인
		CertDto certDto = findPwVO.getCertDto();
		boolean isCertValid = certDao.check(certDto, customCertProperties.getExpire());
		if(isCertValid == false) throw new TargetNotFoundException("FindPw : 인증 안됨");
		//인증성공시 인증번호 삭제(1회접근페이지)
		certDao.delete(certDto.getCertEmail());
		
		// 존재하는 아이디인지 확인
		MemberDto memberDto = memberDao.selectOne(findPwVO.getMemberId());
		if(memberDto == null) throw new TargetNotFoundException("FindPw : 존재하지 않는 회원 아이디");
		
		//비밀번호변경
		memberDao.updateMemberPw(findPwVO.getMemberId(), findPwVO.getChangePw());		
		
	}
	
	// 비밀번호 변경 - 로그인 상태
	@PostMapping("/changePw")
	public void changePw(@RequestHeader("Authorization") String accessToken,
						@RequestBody MemberChangePwVO changePwVO) {
		if(tokenService.isBearerToken(accessToken) == false) throw new TargetNotFoundException("유효하지 않은 토큰");
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(accessToken));
		
		MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
		if(memberDto == null) throw new TargetNotFoundException("존재하지 않는 회원");
		
		// 현재 비밀번호 일치 여부 확인
//		boolean isPwValid = memberDto.getMemberPw().equals(changePwVO.getCurrentPw());
		boolean isPwValid = encoder.matches(changePwVO.getCurrentPw(), memberDto.getMemberPw()); // 암호화시 사용
		if(isPwValid == false) throw new TargetNotFoundException("변경 : 비밀번호 불일치");
		
//		// 인증 정보 확인
//		CertDto certDto = memberCertVO.getCertDto();
//		boolean isCertValid = certDao.check(certDto, customCertProperties.getExpire());
//		if(isCertValid == false) throw new TargetNotFoundException("인증 안됨");
//		//인증성공시 인증번호 삭제(1회접근페이지)
//		certDao.delete(certDto.getCertEmail());
		
		//비밀번호변경
		memberDao.updateMemberPw(claimVO.getMemberId(), changePwVO.getChangePw());		
	}
	
	// 회원 가입
	@PostMapping("/join")
	public void join(@RequestBody MemberDto memberDto) {
		memberDao.insert(memberDto);
	}
	
	// 회원 가입 시 아이디 중복 검사
	@GetMapping("/checkId/{memberId}")
	public boolean checkId(@PathVariable String memberId) {
		MemberDto memberDto = memberDao.selectOne(memberId);
		return memberDto == null;
	}
	
	// 회원 탈퇴 : 액세스 토큰과 비밀번호를 담은 VO 전송
	@PostMapping("/exit")
	public void exit(@RequestHeader("Authorization") String accessToken, @RequestBody MemberExitRequestVO exitVO) {
		if(tokenService.isBearerToken(accessToken) == false) throw new TargetNotFoundException("유효하지 않은 토큰");
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(accessToken));
		
		MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
		if(memberDto == null) throw new TargetNotFoundException("존재하지 않는 회원");
		boolean isValid = exitVO.getMemberPw().equals(memberDto.getMemberPw());
		if(isValid == false) throw new TargetNotFoundException("비밀번호 불일치");
		
		// 기존의 리프레시 토큰 삭제 - 안지워도 어차피 외래키 cascade라서 지워진다
		// memberTokenDao.remove(claimVO.getMemberId());
		
		// 회원 정보 삭제
		memberDao.delete(memberDto.getMemberId());		
	}
	
	// 차단 목록 검색
	@PostMapping("/block/list")
	public MemberBlockResponseVO block(@RequestHeader("Authorization") String token,
										@RequestBody MemberBlockRequestVO vo) {
		// 세션 정보 확인 및 아이디 추출
		if(tokenService.isBearerToken(token) == false) throw new TargetNotFoundException("유효하지 않은 토큰");
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
		if(memberDto == null) throw new TargetNotFoundException("존재하지 않는 회원");
		vo.setBlockOwner(claimVO.getMemberId());
		int count = blockDao.countWithPaging(vo);
		boolean last = vo.getEndRow() == null || count <= vo.getEndRow();
		MemberBlockResponseVO response = new MemberBlockResponseVO();
		response.setMemberBlockList(blockDao.selectListByPaging(vo));
		response.setCount(count);
		response.setLast(last);
		return response;
	}
		
	// 상대방 차단 등록
	@PostMapping("/block/insert")
	public void insertBlock(@RequestHeader("Authorization") String token,
							@RequestBody BlockDto blockDto) {
		// 세션 정보 확인 및 아이디 추출
		if(tokenService.isBearerToken(token) == false) throw new TargetNotFoundException("유효하지 않은 토큰");
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
		if(memberDto == null) throw new TargetNotFoundException("존재하지 않는 회원");
		// 추출한 아이디를 작성자로 설정 (프론트에서 blockDto에 받아올 시 안해도 됨)
		blockDto.setBlockOwner(claimVO.getMemberId()); 
		// 상대방의 마지막 차단/해제 상태 확인
		BlockDto lastDto = blockDao.selectLastOne(blockDto);
		boolean isBlock = lastDto == null || lastDto.getBlockType().equals("해제");
		if(isBlock == false) throw new TargetNotFoundException("이미 차단한 상대");
		// 차단 등록
		blockDao.insertBlock(blockDto); 
	}
	
	// 상대방 차단 해제
	@PostMapping("/block/cancel")
	public void cancelBlock(@RequestHeader("Authorization") String token,
							@RequestBody BlockDto blockDto) {
		// 세션 정보 확인 및 아이디 추출
		if(tokenService.isBearerToken(token) == false) throw new TargetNotFoundException("유효하지 않은 토큰");
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
		if(memberDto == null) throw new TargetNotFoundException("존재하지 않는 회원");
		// 추출한 아이디를 작성자로 설정 (프론트에서 blockDto에 받아올 시 안해도 됨)
		blockDto.setBlockOwner(claimVO.getMemberId()); 
		// 상대방의 마지막 차단/해제 상태 확인
		BlockDto lastDto = blockDao.selectLastOne(blockDto);
		boolean isBlock = lastDto == null || lastDto.getBlockType().equals("해제");
		if(isBlock == true) throw new TargetNotFoundException("이미 해제한 상대");
		// 차단 해제
		blockDao.cancelBlock(blockDto); 
	}
	
	// 타회원 상세정보 조회
	@GetMapping("/detail/{memberId}")
	public MemberDto detail(@PathVariable String memberId) {
		MemberDto memberDto = memberDao.selectOne(memberId);
		if(memberDto == null) throw new TargetNotFoundException("존재하지 않는 회원 아이디");
		return memberDto;
	}
	
//	@GetMapping("/{memberId}")
//	public MemberDetailVO detail(
//			@PathVariable String memberId) {
//		MemberDetailVO memberDetailVO = new MemberDetailVO();
//		memberDetailVO = memberDao.selectMemberDetail(memberId);
//		
//		return memberDetailVO;
//	}

	// 특정 회원의 상품목록 조회	
//	@GetMapping("/product/{memberId}")
//	public List<ProductDto> memberProductList(@PathVariable String memberId){
//		if(memberDao.selectMemberProduct(memberId)==null) {
//			List<ProductDto> list =null;			
//		}
//		List<ProductDto> list = memberDao.selectMemberProduct(memberId); 
//		return list;
//	}
	
	//차단 상태 조회 매핑
	@GetMapping("/banCheck/{memberId}")
	public ResponseEntity<Boolean> banCheck(@PathVariable String memberId) {
		BanDto banDto = memberDao.selectBanCheck(memberId);
		
		return ResponseEntity.ok(banDto != null && "차단".equals(banDto.getBanType()));
	}
	
	// 정보 딱 하나만 바꿀 때 쓰는 매핑 : 인증회원 등업, 포인트 변경, 유저평가점수 변경 등
	@PatchMapping("/patch")
	public void patch(@RequestHeader("Authorization") String token,  @RequestBody MemberDto memberDto) {
		if(tokenService.isBearerToken(token) == false) throw new TargetNotFoundException("유효하지 않은 토큰");
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		
		MemberDto findDto = memberDao.selectOne(claimVO.getMemberId());
		if(findDto == null) throw new TargetNotFoundException("존재하지 않는 회원");
		
		memberDto.setMemberId(claimVO.getMemberId());
		memberDao.updateOne(memberDto);
	}
	
	

}
