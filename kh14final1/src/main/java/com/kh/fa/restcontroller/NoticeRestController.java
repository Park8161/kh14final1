package com.kh.fa.restcontroller;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.fa.dao.MemberDao;
import com.kh.fa.dao.NoticeDao;
import com.kh.fa.dto.MemberDto;
import com.kh.fa.dto.NoticeDto;
import com.kh.fa.error.TargetNotFoundException;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberClaimVO;
import com.kh.fa.vo.NoticeListRequestVO;
import com.kh.fa.vo.NoticeListResponseVO;
import com.kh.fa.vo.ProductInsertRequestVO;

import io.swagger.v3.oas.annotations.Parameter;

@CrossOrigin
@RestController
@RequestMapping("/notice")
public class NoticeRestController {

	@Autowired
	private NoticeDao noticeDao;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private MemberDao memberDao;

	//등록
	@PostMapping(value = "/insert")
	public void insert(@RequestBody NoticeDto noticeDto,
			@RequestHeader("Authorization") String token) 
					throws IllegalStateException, IOException {
	// 토큰 변환
	MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
	// 유효 검증
	MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
	if(memberDto == null) throw new TargetNotFoundException("존재하지 않는 회원");
	
//	@PostMapping("/insert")
//	public void insert(@RequestBody NoticeDto noticeDto, @RequestHeader("Authorization") String token) {
		int noticeNo = noticeDao.sequence();
	
		noticeDto.setNoticeWriter(claimVO.getMemberId());
		noticeDto.setNoticeNo(noticeNo);
		System.out.println(noticeDto);
		System.out.println(noticeNo);
		noticeDao.insert(noticeDto);
	
	}
	@GetMapping("/detail/{noticeNo}")//상세
	public NoticeDto detail(
			@Parameter(required = true, description = "글 번호(PK)")
			@PathVariable int noticeNo) {
		
		NoticeDto noticeDto = noticeDao.selectOne(noticeNo);
		if(noticeDto == null) throw new TargetNotFoundException();
		return noticeDto;
	}
	
	@PostMapping("/list")//목록 + 페이징 + 검색
	public NoticeListResponseVO list(@RequestBody NoticeListRequestVO vo){
		int count = noticeDao.countWithPaging(vo);
		boolean last = vo.getEndRow() == null  || count <= vo.getEndRow();
		NoticeListResponseVO response = new NoticeListResponseVO();
		response.setNoticeList(noticeDao.selectListByPaging(vo));
		response.setCount(count);
		response.setLast(last);
		return response;
	}	
	
	@DeleteMapping("/delete/{noticeNo}")//삭제
	public void delete(@PathVariable int noticeNo) {
		noticeDao.delete(noticeNo);
	}
	
}