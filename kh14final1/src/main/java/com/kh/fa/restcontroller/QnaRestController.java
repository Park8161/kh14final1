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
import com.kh.fa.dto.QnaDto;
import com.kh.fa.dto.ReplyDto;
import com.kh.fa.error.TargetNotFoundException;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberClaimVO;

import io.swagger.v3.oas.annotations.Parameter;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/qna")
public class QnaRestController {

	@Autowired
	private QnaDao qnaDao;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private ReplyDao replyDao;
	
	//등록
	@PostMapping(value = "/insert")
	public void insert(@RequestBody QnaDto qnaDto,
			@RequestHeader("Authorization") String token)
					throws IllegalStateException, IOException{
		
	//토큰변환
	MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
	//유효 검증
	MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
	if(memberDto  == null) throw new TargetNotFoundException("존재하지 않는 회원");
		
	int qnaNo = qnaDao.sequence();
	qnaDto.setQnaWriter(claimVO.getMemberId());
	qnaDto.setQnaNo(qnaNo);
	qnaDao.insert(qnaDto);
	}
	@GetMapping("/detail/{qnaNo}")//상세
	public QnaDto detail(
			@Parameter(required = true, description = "글 번호(PK)")
			@PathVariable int qnaNo) {
		
		QnaDto qnaDto = qnaDao.selectOne(qnaNo);
		if(qnaDto == null) throw new TargetNotFoundException();
		return qnaDto;
	}
	
//	@PostMapping("/list")//목록 + 페이징 + 검색
//	public QnaListResponseVO list(@RequestBody QnaListRequestVO vo){
//		int count = qnaDao.countWithPaging(vo);
//		boolean last = vo.getEndRow() == null  || count <= vo.getEndRow();
//		QnaListResponseVO response = new QnaListResponseVO();
//		response.setQnaList(qnaDao.selectListByPaging(vo));
//		response.setCount(count);
//		response.setLast(last);
//		return response;
//	}
	
	@GetMapping("/list")
	public List<QnaDto> list(){
		return qnaDao.selectList();
	}
	
	@GetMapping("/list/column/{column}/keyword/{keyword}")
	public List<QnaDto> list(@PathVariable String column, @PathVariable String keyword) {
		return qnaDao.selectList(column, keyword);
	}
	
	@PutMapping("/edit/{qnaNo}")//수정
	public void update(
			@RequestHeader("Authorization") String token,
			@PathVariable int qnaNo,
			@RequestBody QnaDto updatedQna) throws IllegalStateException, IOException {
		// 토큰 변환
	    MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
	    
	    // DB에서 해당 답글 조회
	    QnaDto qnaDto = qnaDao.selectOne(qnaNo);
	    if (qnaDto == null) {
	        throw new TargetNotFoundException("해당 글이 존재하지 않습니다.");
	    }
	    
	    //작성자 확인
		boolean isOwner = qnaDto.getQnaWriter().equals(claimVO.getMemberId());
		if(!isOwner) {
			throw new IllegalStateException("본인의 글만 수정할 수 있습니다.");
		}
		
		
	    qnaDto.setQnaContent(updatedQna.getQnaContent()); // 수정할 내용을 설정
	    qnaDao.update(qnaDto); // 업데이트 메서드를 호출하여 수정
	}
	
	
	@DeleteMapping("/delete/{qnaNo}")//삭제
	public void delete(@RequestHeader("Authorization") String token,
			@PathVariable int qnaNo) throws IllegalStateException, IOException{
		
		// 토큰 변환
	    MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
	    
	    if (!claimVO.getMemberId().equals(claimVO.getMemberId())) {
            throw new IllegalStateException("본인의 글만 삭제할 수 있습니다.");
        }
	    QnaDto qnaDto = qnaDao.selectOne(qnaNo);
	    if(qnaDto == null) {
			throw new TargetNotFoundException("존재하지 않는 게시글");
		}
	    boolean isOwner =  qnaDto.getQnaWriter().equals(claimVO.getMemberId());
	    if(isOwner) {
	    	qnaDao.delete(qnaNo);
	    }
	}
}