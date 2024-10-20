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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.fa.dao.CategoryDao;
import com.kh.fa.dao.MemberDao;
import com.kh.fa.dao.ProductDao;import com.kh.fa.dao.ProductLikeDao;
import com.kh.fa.dto.MemberDto;
import com.kh.fa.dto.ProductDto;
import com.kh.fa.error.TargetNotFoundException;
import com.kh.fa.service.AttachmentService;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.CategoryNameVO;
import com.kh.fa.vo.MemberClaimVO;
import com.kh.fa.vo.ProductDetailResponseVO;
import com.kh.fa.vo.ProductEditRequestVO;
import com.kh.fa.vo.ProductInsertRequestVO;
import com.kh.fa.vo.ProductLikeVO;
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
	@Autowired
	private CategoryDao categoryDao;
	@Autowired
	private ProductLikeDao productLikeDao;
	
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

	// 연관 상품 목록
	@GetMapping("/relation/{productNo}")
	public ProductListResponseVO relation(@PathVariable int productNo) {
		ProductDto productDto = productDao.selectOne(productNo);
		if(productDto == null) throw new TargetNotFoundException("존재하지 않는 상품 번호");
		ProductListResponseVO responseVO = new ProductListResponseVO();
		// 연관 상품 목록 추가
		responseVO.setProductList(productDao.selectRelationList(productNo, productDto.getProductCategory()));
		// 페이징 아니고 그냥 5개만 보여줄거라 임의로 값을 넣어서 고정시킴
		responseVO.setCount(6);
		responseVO.setLast(true);
		
		return responseVO;
	}	
	
	// 상세 정보 조회
	@GetMapping("/detail/{productNo}")
	public ProductDetailResponseVO detail(@PathVariable int productNo) {
		ProductDto productDto = productDao.selectOne(productNo);
		if(productDto == null) throw new TargetNotFoundException("존재하지 않는 상품 번호");
		// 이 도서의 이미지 번호들을 조회하여 전달
		List<Integer> images = productDao.findImages(productNo);
		// 상품번호를 이용해 해당 상품의 대중소분류 카테고리 이름 조회
		CategoryNameVO categoryNameVO = categoryDao.selectNameList(productNo);
		
		ProductDetailResponseVO responseVO = new ProductDetailResponseVO();
		responseVO.setProductDto(productDto);
		responseVO.setCategoryNameVO(categoryNameVO);
		responseVO.setImages(images);
		return responseVO; 	
	}
	
	// 상품 정보 수정
	// 상세 정보의 수정된 정보를 그대로 받아 requestVO로 사용
	// 첨부파일 수정은 수정 전/후를 비교하여 삭제 대상을 찾아 삭제 처리
	@Transactional
	@PostMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void edit(@ModelAttribute ProductEditRequestVO requestVO) throws IllegalStateException, IOException {
		ProductDto originDto = productDao.selectOne(requestVO.getProductNo());
		if(originDto == null) throw new TargetNotFoundException("존재하지 않는 상품 번호");
		
		// 수정 전 
		Set<Integer> before = new HashSet<>();
		List<Integer> beforeList = productDao.findImages(originDto.getProductNo());
		for(int i=0; i<beforeList.size(); i++) {
			before.add(beforeList.get(i)); // 저장소에 추가
		}		
		
		// 수정 후
		Set<Integer> after = new HashSet<>();
		int attachListSize = requestVO.getAttachList().size();
		for(int i=0; i<attachListSize; i++) {
			int attachmentNo = attachmentService.save(requestVO.getAttachList().get(i));
			productDao.connect(requestVO.getProductNo(), attachmentNo);
			after.add(attachmentNo); // 저장소에 추가
		}
		
		// 수정전 - 수정후 계산
		before.removeAll(after);
		
		// before에 남아있는 번호에 해당하는 파일을 모두 삭제
		for(int attachmentNo : before) {
			attachmentService.delete(attachmentNo); // db+파일 삭제
		}
		
		// 상품 정보 수정
		// 글처리 다했으므로 글이 없는지 파악할 필요가 없다
		ProductDto productDto = productDao.selectOne(requestVO.getProductNo());
		productDto.setProductName(requestVO.getProductName());
		productDto.setProductMember(requestVO.getProductMember());
		productDto.setProductPrice(requestVO.getProductPrice());
		productDto.setProductDetail(requestVO.getProductDetail());
		productDto.setProductState(requestVO.getProductState());
		productDto.setProductQty(requestVO.getProductQty());
		productDto.setProductCategory(requestVO.getProductCategory());
//		System.out.println("여기야!! : "+productDto);
		productDao.update(productDto);
	}
	
	// 상품 정보 삭제
	@DeleteMapping("/{productNo}")
	public void delete(@PathVariable int productNo) {
		ProductDto productDto = productDao.selectOne(productNo);
		if(productDto == null) throw new TargetNotFoundException("존재하지 않는 상품 번호");
		
		// 1. select attachment from product_image where product = #{product}
		// 를 이용하여 attachmentNo를 조사후 list화 한다
		// 2. 리스트 사이즈 만큼 반복문 돌리면서 삭제
		
		List<Integer> list = productDao.findImages(productNo);
		for(int i=0; i<list.size(); i++) {
			System.out.println("지워지는번호 : "+list.get(i));
			attachmentService.delete(list.get(i));
		}
		
		productDao.delete(productNo); // 상품 정보 삭제
		productDao.deleteImage(productNo); // 연결 테이블 정보 삭제
	}
	
	// 상품 좋아요 확인 매핑
	@GetMapping("/check/{productNo}")
	public ProductLikeVO check(@RequestHeader("Authorization") String token, @PathVariable int productNo) {
		// 토큰 변환
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		// 유효 검증
		MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
		if(memberDto == null) throw new TargetNotFoundException("존재하지 않는 회원");
		
		ProductLikeVO productLikeVO = new ProductLikeVO();
		
		// 설정
		productLikeVO.setChecked(productLikeDao.check(productNo, claimVO.getMemberId()));
		productLikeVO.setCount(productLikeDao.count(productNo));
		
		return productLikeVO;
	}
	
	// 상품 좋아요 및 취소 기능
	@GetMapping("/like/{productNo}")
	public ProductLikeVO like(@RequestHeader("Authorization") String token, @PathVariable int productNo) {
		// 토큰 변환
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		// 유효 검증
		MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
		if(memberDto == null) throw new TargetNotFoundException("존재하지 않는 회원");
		
		boolean isChecked = productLikeDao.check(productNo, claimVO.getMemberId());
		if(isChecked) { // 삭제(등록 이력 있음)
			productLikeDao.delete(claimVO.getMemberId(), productNo);
		}
		else { // 등록(등록 이력 없음)
			productLikeDao.insert(claimVO.getMemberId(), productNo);
		}
		
		// 갱신(최적화) - 반정규화
		productDao.updateLikes(productNo);
		
		// 설정
		ProductLikeVO productLikeVO = new ProductLikeVO();
		productLikeVO.setChecked(isChecked);
		productLikeVO.setCount(productLikeDao.count(productNo));
		
		return productLikeVO;
	}
	
}
