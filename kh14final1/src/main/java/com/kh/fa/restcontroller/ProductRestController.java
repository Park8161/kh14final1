package com.kh.fa.restcontroller;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.kh.fa.vo.ProductDetailResponseVO;
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
	
	// 상품 목록 + 페이징 + 검색
	@PostMapping("/list")
	public ProductListResponseVO list(@RequestBody ProductListRequestVO requestVO){
		// 페이징 정보 정리
		int count = productDao.countWithPaging(requestVO);
		boolean last = requestVO.getEndRow() == null || count <= requestVO.getEndRow();
		// 출력 클래스에 입력
		ProductListResponseVO responseVO = new ProductListResponseVO();
		// 각 상품 정보들의 이미지 번호를 조회하여 상품 객체에 첨부
		// 상품 정보와 같은 상품번호를 공유하는 첨부 테이블 정보를 한번에 조회하여
		// ProductList와 Images 대신에 productListVO를 리스트화 하여 responseVO에 넣어 전달
		responseVO.setProductList(productDao.selectListByPaging(requestVO));
		responseVO.setCount(count);
		responseVO.setLast(last);
		
		return responseVO;
	}
	
	// 상세 정보 조회
	@PostMapping("/detail/{productNo}")
	public ProductDetailResponseVO detail(@PathVariable int productNo) {
		ProductDto productDto = productDao.selectOne(productNo);
		if(productDto == null) throw new TargetNotFoundException("존재하지 않는 상품 번호");
		// 이 도서의 이미지 번호들을 조회하여 전달
		List<Integer> images = productDao.findImages(productNo);
		
		ProductDetailResponseVO responseVO = new ProductDetailResponseVO();
		responseVO.setProductDto(productDto);
		responseVO.setImages(images);
		return responseVO; 	
	}
	
	// 상품 정보 수정
	// 상세 정보의 수정된 정보를 그대로 받아 requestVO로 사용
	// 첨부파일 수정은 수정 전/후를 비교하여 삭제 대상을 찾아 삭제 처리
	@PostMapping("/edit")
	public void edit(@ModelAttribute ProductDetailResponseVO RequestVo) {
		ProductDto originDto = productDao.selectOne(RequestVo.getProductDto().getProductNo());
		if(originDto == null) throw new TargetNotFoundException("존재하지 않는 상품 번호");
		
		// 수정 전 
		Set<Integer> before = new HashSet<>();
		Document beforeDocument = Jsoup.parse(originDto.getProductDetail());
		for(Element el : beforeDocument.select(".product-attach")) { // .product-attach 찾아서 반복해라
			String keyStr = el.attr("data-key"); // data-key 속성 추출
			int key = Integer.parseInt(keyStr); // int로 변환
			before.add(key); // 저장소에 추가
		}		
		
		// 수정 후
		Set<Integer> after = new HashSet<>();
		Document afterDocument = Jsoup.parse(RequestVo.getProductDto().getProductDetail()); // 수정글 내용 해석
		for(Element el : afterDocument.select(".board-attach")) { // .product-attach 찾아서 반복해라
			String keyStr = el.attr("data-key"); // data-key 속성 추출
			int key = Integer.parseInt(keyStr); // int로 변환
			after.add(key); // 저장소에 추가
		}
		
		// 수정전 - 수정후 계산
		before.removeAll(after);
		
		// before에 남아있는 번호에 해당하는 파일을 모두 삭제
		for(int attachmentNo : before) {
			attachmentService.delete(attachmentNo); // db+파일 삭제
		}
		
		// 상품 정보 수정
		// 글처리 다했으므로 글이 없는지 파악할 필요가 없다
		productDao.update(RequestVo.getProductDto());
		
		
		
	}
	
	// 상품 정보 삭제
	@DeleteMapping("/{productNo}")
	public void delete(@PathVariable int productNo) {
		ProductDto productDto = productDao.selectOne(productNo);
		if(productDto == null) throw new TargetNotFoundException("존재하지 않는 상품 번호");
		
		
	}
	
}
