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
import com.kh.fa.dao.QnaDao;
import com.kh.fa.dao.ReplyDao;
import com.kh.fa.dto.MemberDto;
import com.kh.fa.dto.ReplyDto;
import com.kh.fa.error.TargetNotFoundException;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberClaimVO;

import io.swagger.v3.oas.annotations.Parameter;

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
	@Autowired
	private QnaDao qnaDao;
	
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
	    qnaDao.updateQnaReplies(replyDto.getReplyQna());
	}

	//목록
	@GetMapping("/list")
	public List<ReplyDto> list(@RequestParam int replyQna){
		return replyDao.selectList(replyQna);
	}
	@PutMapping("/edit/{replyNo}")
	public void update(
	        @RequestHeader("Authorization") String token,
	        @PathVariable int replyNo,
	        @RequestBody ReplyDto updatedReply) throws IllegalStateException, IOException {
	    // 토큰 변환
	    MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
	    
	    // DB에서 해당 답글 조회
	    ReplyDto replyDto = replyDao.selectOne(replyNo);
	    if (replyDto == null) {
	        throw new TargetNotFoundException("해당 답글이 존재하지 않습니다.");
	    }

	    // 작성자 확인
	    boolean isOwner = replyDto.getReplyWriter().equals(claimVO.getMemberId());
	    if (!isOwner) {
	        throw new IllegalStateException("본인의 답글만 수정할 수 있습니다.");
	    }

	    // 답글 수정
	    replyDto.setReplyContent(updatedReply.getReplyContent()); // 수정할 내용을 설정
	    replyDao.update(replyDto); // 업데이트 메서드를 호출하여 수정
	}

		    
	//삭제
	@DeleteMapping("/delete/{replyNo}")
	public void delete(
            @RequestHeader("Authorization") String token,
             @PathVariable int replyNo) throws IllegalStateException, IOException {
	    // 토큰 변환
	    MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
	    
	    
	    if (!claimVO.getMemberId().equals(claimVO.getMemberId())) {
            throw new IllegalStateException("본인의 답글만 삭제할 수 있습니다.");
        }
	    ReplyDto replyDto = replyDao.selectOne(replyNo);
	    if(replyDto == null) {
			throw new TargetNotFoundException("");
		}
	    boolean isOwner =  replyDto.getReplyWriter().equals(claimVO.getMemberId());
	    if(isOwner) {
	    	replyDao.delete(replyNo);
	    	qnaDao.updateQnaReplies(replyDto.getReplyQna());//댓글 수 최신화
	    }
	}
}
