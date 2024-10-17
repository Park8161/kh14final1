package com.kh.fa.restcontroller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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
import com.kh.fa.vo.ProductInsertRequestVO;
import com.kh.fa.vo.ProductListRequestVO;
import com.kh.fa.vo.ProductListResponseVO;

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
	@Transactional
	@PostMapping(value = "/insert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void insert(@RequestHeader("Authorization") String token,
						@ModelAttribute ProductInsertRequestVO requestVO) 
					throws IllegalStateException, IOException {
		// 토큰 변환
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		// 유효 검증
		MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
		if(memberDto == null) throw new TargetNotFoundException("존재하지 않는 회원");
		// 상품 정보 등록 - productDto에는 상품 번호와 상품등록자(판매자)가 빠져있다
		int productNo = productDao.sequence();
		ProductDto productDto = new ProductDto();
		productDto.setProductNo(productNo);
		productDto.setProductMember(claimVO.getMemberId());
		productDto.setProductName(requestVO.getProductName());
		productDto.setProductCategory(requestVO.getProductCategory());
		productDto.setProductPrice(requestVO.getProductPrice());
		productDto.setProductDetail(requestVO.getProductDetail());
		productDto.setProductQty(requestVO.getProductQty());
		productDao.insert(productDto);
		// 파일 등록
		for(MultipartFile attach : requestVO.getAttachList()) {
			if(attach.isEmpty()) continue; // 파일 없으면 스킵
			
			int attachmentNo = attachmentService.save(attach);
			productDao.connect(productNo, attachmentNo);
		}		
	}
	
	// 목록 조회
	@PostMapping("/list")
	public ProductListResponseVO list(@RequestBody ProductListRequestVO requestVO){
		
		int count = productDao.countWithPaging(requestVO);
		boolean last = requestVO.getEndRow() == null || count <= requestVO.getEndRow();
		ProductListResponseVO responseVO = new ProductListResponseVO();
		responseVO.setProductList(productDao.selectListByPaging(requestVO));
		responseVO.setCount(count);
		responseVO.setLast(last);
		return responseVO;
	}
	
	
}
