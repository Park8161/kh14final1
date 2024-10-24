package com.kh.fa.restcontroller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.fa.dao.MemberDao;
import com.kh.fa.dao.ReplyDao;
import com.kh.fa.dto.MemberDto;
import com.kh.fa.dto.ReplyDto;
import com.kh.fa.error.TargetNotFoundException;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberClaimVO;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/qna/reply")
public class ReplyRestController {

	@Autowired
	private ReplyDao replyDao;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private MemberDao memberDao;
	
	@PostMapping(value = "/insert")
	public void insert(@RequestBody ReplyDto replyDto,
	                   @RequestHeader("Authorization") String token,
	                   @RequestParam int replyQna) // replyQna를 요청 파라미터로 받기
	                   throws IllegalStateException, IOException {
	    // 토큰 변환
	    MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
	    
	    // 유효 검증
	    MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
	    if (memberDto == null) throw new TargetNotFoundException("존재하지 않는 회원");
	    
	    int seq = replyDao.sequence();
	    
	    replyDto.setReplyWriter(claimVO.getMemberId());
	    replyDto.setReplyNo(seq);
	    replyDto.setReplyQna(replyQna); // replyQna 설정
	    replyDao.insert(replyDto);
	}

	//목록
	@GetMapping("/list")
	public List<ReplyDto> list(@RequestParam int replyQna){
		return replyDao.selectList(replyQna);
	}
	//수정
	@PutMapping("/edit/{replyNo}")
	public void update(@PathVariable int replyNo, @RequestBody ReplyDto replyDto) {
		boolean result = replyDao.update(replyDto);
		if(result == false) {
			throw new TargetNotFoundException();
		}
	}
	//삭제
	@DeleteMapping("/delete/{replyNo}")
	public void delete(@PathVariable int replyNo) {
		replyDao.delete(replyNo);
	}
}
