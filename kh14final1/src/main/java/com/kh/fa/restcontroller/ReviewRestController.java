package com.kh.fa.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.fa.dao.MemberDao;
import com.kh.fa.dao.ProductDao;
import com.kh.fa.dao.ReviewDao;
import com.kh.fa.dto.MemberDto;
import com.kh.fa.dto.ProductDto;
import com.kh.fa.dto.ReviewDto;
import com.kh.fa.error.TargetNotFoundException;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberClaimVO;
import com.kh.fa.vo.ReviewVO;

@CrossOrigin
@RestController
@RequestMapping("/review")
public class ReviewRestController {
	
	@Autowired
	private ReviewDao reviewDao;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private ProductDao productDao;
	
	
	// 등록
	@PostMapping("/insert/{productNo}")
	public void insert(@RequestHeader("Authorization") String token, 
			@PathVariable int productNo, @RequestBody ReviewDto reviewDto) {
		// 세션 정보 확인 및 아이디 추출
		if(tokenService.isBearerToken(token) == false) throw new TargetNotFoundException("유효하지 않은 토큰");
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
		if(memberDto == null) throw new TargetNotFoundException("존재하지 않는 회원 아이디 : 작성자");
		
		ProductDto productDto = productDao.selectOne(productNo);
		if(productDto == null) throw new TargetNotFoundException("존재하지 않는 상품 번호 : 리뷰 상품");
		
		// 리뷰 정보 DB 저장
		int reviewSeq = reviewDao.sequence();
		reviewDto.setReviewNo(reviewSeq);
		reviewDto.setReviewWriter(claimVO.getMemberId());
		reviewDto.setReviewProduct(productNo);
		reviewDto.setReviewTarget(productDto.getProductMember());
		reviewDao.insert(reviewDto);		
		
		// 회원 정보 테이블의 신뢰지수에 점수 반영
		MemberDto targetDto = memberDao.selectOne(reviewDto.getReviewTarget());
		targetDto.setMemberReliability(targetDto.getMemberReliability()+reviewDto.getReviewScore());
	}
	
	// 목록 (페이징X) : 판매자 대상 검색 리뷰
	@GetMapping("/list/{memberId}")
	public List<ReviewVO> list(@PathVariable String memberId/*, @PathVariable String column, @PathVariable String keyword*/) {
		String column = null; // 경로변수 대입 시 삭제 필수
		String keyword = null; // 경로변수 대입 시 삭제 필수
		return reviewDao.selectList(memberId, column, keyword);
	}
	
	// 목록 (페이징X) : 내가 쓴 리뷰
	@GetMapping("/myList")
	public List<ReviewVO> myList(@RequestHeader("Authorization") String token/*, @PathVariable String column, @PathVariable String keyword*/) {
		// 세션 정보 확인 및 아이디 추출
		if(tokenService.isBearerToken(token) == false) throw new TargetNotFoundException("유효하지 않은 토큰");
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
		if(memberDto == null) throw new TargetNotFoundException("존재하지 않는 회원 아이디 : 작성자");
		
		String column = null; // 경로변수 대입 시 삭제 필수
		String keyword = null; // 경로변수 대입 시 삭제 필수
		return reviewDao.selectList(claimVO.getMemberId(), column, keyword);
	}
	
	// 리뷰 개수 카운트
	@GetMapping("/count/{memberId}") // 판매자 아이디 : 내 아이디X
	public int count(@PathVariable String memberId ) {
		return reviewDao.countReview(memberId);
	}
	
	// 상세
	@GetMapping("/detail/{reviewNo}")
	public ReviewDto detail(@PathVariable int reviewNo) {
		return reviewDao.selectOne(reviewNo);
	}
	
	// 수정
//	@PostMapping("/update")
	@PutMapping("/update")
	public void update(@RequestBody ReviewDto reviewDto) {
		MemberDto targetDto = memberDao.selectOne(reviewDto.getReviewTarget());
		if(targetDto == null) throw new TargetNotFoundException("존재하지 않는 회원 아이디 : 평가대상");
		
		MemberDto writerDto = memberDao.selectOne(reviewDto.getReviewWriter());
		if(writerDto == null) throw new TargetNotFoundException("존재하지 않는 회원 아이디 : 작성자");
		
		ReviewDto findDto = reviewDao.selectOne(reviewDto.getReviewNo());
		if(findDto == null) throw new TargetNotFoundException("존재하지 않는 리뷰 번호");

		// 회원 정보 테이블의 신뢰지수에 점수 반영
		targetDto.setMemberReliability(targetDto.getMemberReliability()-findDto.getReviewScore()+reviewDto.getReviewScore());
		
		reviewDao.update(reviewDto);
	}
	
	// 삭제
	@DeleteMapping("/{reviewNo}")
	public void delete(@PathVariable int reviewNo) {
		ReviewDto findDto = reviewDao.selectOne(reviewNo);
		if(findDto == null) throw new TargetNotFoundException("존재하지 않는 리뷰 번호");
		
		// 회원 정보 테이블의 신뢰지수에 점수 반영
		MemberDto targetDto = memberDao.selectOne(findDto.getReviewTarget());
		targetDto.setMemberReliability(targetDto.getMemberReliability()-findDto.getReviewScore());
		
		reviewDao.delete(reviewNo);
	}
	
	
}
