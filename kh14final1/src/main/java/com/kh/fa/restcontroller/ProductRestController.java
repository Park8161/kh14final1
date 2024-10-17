package com.kh.fa.restcontroller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.fa.dao.MemberDao;
import com.kh.fa.dao.ProductDao;
import com.kh.fa.dto.MemberDto;
import com.kh.fa.dto.ProductDto;
import com.kh.fa.error.TargetNotFoundException;
import com.kh.fa.service.AttachmentService;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberClaimVO;

@CrossOrigin
@RestController
@RequestMapping("/product")
public class ProductRestController {
	
	@Autowired
	private ProductDao productDao;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private AttachmentService attachmentService;
	
	
	// 등록
	@PostMapping("/insert")
	public void insert(@RequestHeader("Authorization") String token, 
					@RequestBody ProductDto productDto, 
					@RequestPart(value = "file", required = false) List<MultipartFile> attachList) 
					throws IllegalStateException, IOException {
		System.out.println("productDto : "+productDto);
		// 토큰 변환
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		// 유효 검증
		MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
		if(memberDto == null) throw new TargetNotFoundException("존재하지 않는 회원");
		// 상품 정보 등록 - productDto에는 상품 번호와 상품등록자(판매자)가 빠져있다
		int productNo = productDao.sequence();
		productDto.setProductNo(productNo);
		productDto.setProductMember(claimVO.getMemberId());
		productDao.insert(productDto);
		
		// 파일 등록
		for(MultipartFile attach : attachList) {
			if(attach.isEmpty()) continue; // 파일 없으면 스킵
			
			int attachmentNo = attachmentService.save(attach);
			productDao.connect(productNo, attachmentNo);
		}
		
	}
	
	
}
