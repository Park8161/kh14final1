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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.fa.dao.MemberDao;
import com.kh.fa.dao.ReviewDao;
import com.kh.fa.dto.MemberDto;
import com.kh.fa.dto.ReviewDto;
import com.kh.fa.error.TargetNotFoundException;

@CrossOrigin
@RestController
@RequestMapping("/review")
public class ReviewController {
	
	@Autowired
	private ReviewDao reviewDao;
	@Autowired
	private MemberDao memberDao;
	
	
	// 등록
	@PostMapping("/insert")
	public void insert(@RequestBody ReviewDto reviewDto) {
		MemberDto targetDto = memberDao.selectOne(reviewDto.getReviewTarget());
		if(targetDto == null) throw new TargetNotFoundException("존재하지 않는 회원 아이디 : 평가대상");
		
		MemberDto writerDto = memberDao.selectOne(reviewDto.getReviewWriter());
		if(writerDto == null) throw new TargetNotFoundException("존재하지 않는 회원 아이디 : 작성자");
		
		int reviewSeq = reviewDao.sequence();
		reviewDto.setReviewNo(reviewSeq);
		
		reviewDao.insert(reviewDto);		
	}
	
	// 목록 (페이징X)
	@GetMapping("/list/column/{column}/keyword/{keyword}")
	public List<ReviewDto> list(@PathVariable String column, @PathVariable String keyword){
		return reviewDao.selectList(column, keyword);
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
		
		reviewDao.update(reviewDto);
	}
	
	// 삭제
	@DeleteMapping("/{reviewNo}")
	public void delete(@PathVariable int reviewNo) {
		ReviewDto findDto = reviewDao.selectOne(reviewNo);
		if(findDto == null) throw new TargetNotFoundException("존재하지 않는 리뷰 번호");
		
		reviewDao.delete(reviewNo);
	}
	
	
}
