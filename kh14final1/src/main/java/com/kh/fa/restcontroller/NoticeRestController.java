package com.kh.fa.restcontroller;


import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.fa.dao.MemberDao;
import com.kh.fa.dao.NoticeDao;
import com.kh.fa.dto.MemberDto;
import com.kh.fa.dto.NoticeDto;
import com.kh.fa.error.TargetNotFoundException;
import com.kh.fa.service.AttachmentService;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberClaimVO;
import com.kh.fa.vo.NoticeDetailResponseVO;
import com.kh.fa.vo.NoticeEditRequestVO;
import com.kh.fa.vo.NoticeInsertImageRequestVO;

import io.swagger.v3.oas.annotations.Parameter;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/notice")
public class NoticeRestController {

	@Autowired
	private NoticeDao noticeDao;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private AttachmentService attachmentService;

	//등록
	@Transactional
	@PostMapping(value = "/insert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void insert(@ModelAttribute NoticeDto noticeDto,
			@RequestHeader("Authorization") String token,
			@ModelAttribute NoticeInsertImageRequestVO requestVO) 
					throws IllegalStateException, IOException {
	// 토큰 변환
	MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
	// 유효 검증
	MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
	if(memberDto == null) throw new TargetNotFoundException("존재하지 않는 회원");
	
		int noticeNo = noticeDao.sequence();
	
		noticeDto.setNoticeWriter(claimVO.getMemberId());
		noticeDto.setNoticeNo(noticeNo);
//		System.out.println(noticeDto);
//		System.out.println(noticeNo);
		noticeDao.insert(noticeDto);
		
		//파일 등록
		if(requestVO.getAttachList() != null) {
			for(MultipartFile attach : requestVO.getAttachList()) {
			if(attach.isEmpty()) continue; // 파일이 없다면 스킵
					
			int attachmentNo = attachmentService.save(attach);
			noticeDao.connect(noticeNo, attachmentNo);
			}			
		}
	
	}
	@GetMapping("/detail/{noticeNo}")//상세
	public NoticeDetailResponseVO detail(
			@Parameter(required = true, description = "글 번호(PK)")
			@PathVariable int noticeNo) {
		
		NoticeDto noticeDto = noticeDao.selectOne(noticeNo);
		if(noticeDto == null) throw new TargetNotFoundException();
		
		// 해당 게시글의 이미지 번호들을 조회하여 전달
		List<Integer> images = noticeDao.findImages(noticeNo);
		
		NoticeDetailResponseVO responseVO = new NoticeDetailResponseVO();
		responseVO.setNoticeDto(noticeDto);
		responseVO.setImages(images);
		return responseVO;
//		return noticeDto;
	}
	
//	@PostMapping("/list")//목록 + 페이징 + 검색
//	public NoticeListResponseVO list(@RequestBody NoticeListRequestVO vo){
//		int count = noticeDao.countWithPaging(vo);
//		boolean last = vo.getEndRow() == null  || count <= vo.getEndRow();
//		NoticeListResponseVO response = new NoticeListResponseVO();
//		response.setNoticeList(noticeDao.selectListByPaging(vo));
//		response.setCount(count);
//		response.setLast(last);
//		return response;
//	}	
	
	@GetMapping("/list")
	public List<NoticeDto> list(){
		return noticeDao.selectList();
	}
	
	@GetMapping("/list/column/{column}/keyword/{keyword}")
	public List<NoticeDto> list(@PathVariable String column, @PathVariable String keyword) {
		return noticeDao.selectList(column, keyword);
	}
	
	//수정
	@Transactional
	@PutMapping(value = "/edit/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void update(
			@ModelAttribute NoticeEditRequestVO requestVO) throws IllegalStateException, IOException {
		//공지사항 업데이트
//		boolean result = noticeDao.update(noticeDto);
//		if(result == false) {
//			throw new TargetNotFoundException();
//		}
		NoticeDto originDto = noticeDao.selectOne(requestVO.getNoticeNo());
		if(originDto == null) throw new TargetNotFoundException("존재지 않는 게시글");
		
		// 이미지 처리 수정 전
		Set<Integer> before = new HashSet<>();
		List<Integer> beforeList = noticeDao.findImages(originDto.getNoticeNo());
		for(int i=0; i<beforeList.size();i++){
			before.add(beforeList.get(i));
		}
		
		//이미지 처리 수정 후
//		List<Integer> beforeImages = noticeDao.findImages(originDto.getNoticeNo()); // 기존 이미지 목록
		Set<Integer> after = new HashSet<>(); // 기존 이미지 세트
		noticeDao.deleteImage(requestVO.getNoticeNo());
		int afterSize = requestVO.getOriginList().size();
		for(int i=0; i<afterSize;i++) {
			int attachmentNo = requestVO.getOriginList().get(i);
			noticeDao.connect(requestVO.getNoticeNo(), attachmentNo);
			after.add(attachmentNo);
		}
		//ㅋㅋ
		//수정전 - 수정후 계산
		before.removeAll(after);
		
		//before에 남아있는 번호에 해당하는 파일 모두 삭제
		for(int attachmentNo : before) {
			attachmentService.delete(attachmentNo);
		}
		//attachList 신규첨부
		if(requestVO.getAttachList() != null) {
			int attachListSize = requestVO.getAttachList().size();
			for(int i=0; i<attachListSize; i++) {
				int attachmentNo = attachmentService.save(requestVO.getAttachList().get(i));
				noticeDao.connect(requestVO.getNoticeNo(), attachmentNo);
				after.add(attachmentNo);
			}
		}
		//게시글 정보 수정
		NoticeDto noticeDto = new NoticeDto();
		noticeDto.setNoticeTitle(requestVO.getNoticeTitle());
		noticeDto.setNoticeContent(requestVO.getNoticeContent());
		noticeDto.setNoticeType(requestVO.getNoticeType());
		
		noticeDao.update(noticeDto);	
	}
	
	@DeleteMapping("/delete/{noticeNo}")//삭제
	public void delete(@PathVariable int noticeNo) {
		NoticeDto noticeDto = noticeDao.selectOne(noticeNo);
		if(noticeDto == null) throw new TargetNotFoundException("존재하지 않는 게시글");
		
		List<Integer> list = noticeDao.findImages(noticeNo);
		for(int i=0;i<list.size();i++) {
			attachmentService.delete(list.get(i));
		}
		
		noticeDao.delete(noticeNo);// 게시글 삭제
		noticeDao.deleteImage(noticeNo);// 연결 테입르 정보 삭제
	}
	
}