package com.kh.fa.restcontroller;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.fa.ProductApproveRequestVO;
import com.kh.fa.dao.PaymentDao;
import com.kh.fa.dao.ProductDao;
import com.kh.fa.dto.PaymentDto;
import com.kh.fa.dto.ProductDto;
import com.kh.fa.error.TargetNotFoundException;
import com.kh.fa.service.KakaoPayService;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberClaimVO;
import com.kh.fa.vo.PaymentImageVO;
import com.kh.fa.vo.ProductBuyRequestVO;
import com.kh.fa.vo.pay.KakaoPayApproveRequestVO;
import com.kh.fa.vo.pay.KakaoPayApproveResponseVO;
import com.kh.fa.vo.pay.KakaoPayReadyRequestVO;
import com.kh.fa.vo.pay.KakaoPayReadyResponseVO;

@CrossOrigin
@RestController
@RequestMapping("/pay")
public class PayRestController {
	
	@Autowired
	private PaymentDao paymentDao;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private KakaoPayService kakaoPayService;
	
	@PostMapping("/buy")
	public KakaoPayReadyResponseVO buy(
			@RequestHeader("Authorization") String token,
			@RequestBody ProductBuyRequestVO prRequest
			) throws URISyntaxException {
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));		
		int productNo = prRequest.getProductNo();
		if(productDao.selectOne(productNo).getProductState().equals("판매완료")
				) {throw new TargetNotFoundException("이미 판매된 상품입니다");}
		
		if(productDao.selectOne(productNo).getProductState().equals("거래중")
				) {throw new TargetNotFoundException("거래중인 상품입니다");}
		
		ProductDto productDto = productDao.selectOne(productNo);
		
		KakaoPayReadyRequestVO kkoRequest = new KakaoPayReadyRequestVO();
		kkoRequest.setPartnerOrderId(UUID.randomUUID().toString());
		kkoRequest.setPartnerUserId(claimVO.getMemberId());
		kkoRequest.setItemName(productDto.getProductName());
		kkoRequest.setTotalAmount(prRequest.getTotalPrice());
		kkoRequest.setApprovalUrl(prRequest.getApprovalUrl());
		kkoRequest.setCancelUrl(prRequest.getCancelUrl());
		kkoRequest.setFailUrl(prRequest.getFailUrl());
		
		KakaoPayReadyResponseVO responseVO = kakaoPayService.ready(kkoRequest);
		
		return responseVO;
	} 
	
	@Transactional
	@PostMapping("/approve")
	public PaymentDto approve(
			@RequestHeader("Authorization") String token,
			@RequestBody ProductApproveRequestVO prRequest
			) throws URISyntaxException {
		MemberClaimVO claimVO =
				tokenService.check(tokenService.removeBearer(token));
		
		KakaoPayApproveRequestVO kkoRequest = new KakaoPayApproveRequestVO();
		kkoRequest.setPartnerOrderId(prRequest.getPartnerOrderId());
		kkoRequest.setPartnerUserId(claimVO.getMemberId());
		kkoRequest.setTid(prRequest.getTid());
		kkoRequest.setPgToken(prRequest.getPgToken());

		KakaoPayApproveResponseVO responseVO = kakaoPayService.approve(kkoRequest);
		
		ProductDto productDto = productDao.selectOne(prRequest.getProductNo());
		int paymentSeq = paymentDao.paymentSequence();
		
		PaymentDto paymentDto = new PaymentDto();
		paymentDto.setPaymentNo(paymentSeq);
		//[1] 대표 정보 등록
		paymentDto.setPaymentNo(paymentSeq);//결제번호
		paymentDto.setPaymentTid(responseVO.getTid());//거래번호
		paymentDto.setPaymentName(responseVO.getItemName());//거래상품명
		paymentDto.setPaymentTotal(responseVO.getAmount().getTotal());//거래금액
		paymentDto.setPaymentRemain(paymentDto.getPaymentTotal());//취소가능금액
		paymentDto.setPaymentBuyer(claimVO.getMemberId());//구매자ID
		paymentDto.setPaymentSeller(productDto.getProductMember());//판매자 ID
		paymentDto.setProductNo(prRequest.getProductNo());
		paymentDto.setPaymentStatus("승인");
		paymentDao.paymentInsert(paymentDto);//대표정보 등록
		
		paymentDao.setSoldOut(prRequest.getProductNo());
		
		PaymentDto result = paymentDao.selectOne(paymentSeq);
		
		return result;
	}
	
	@GetMapping("/list")
	public List<PaymentDto> list(@RequestHeader("Authorization") String token) {
		MemberClaimVO claimVO =
				tokenService.check(tokenService.removeBearer(token));
		List<PaymentDto> list = paymentDao.selectList(claimVO.getMemberId());
		return list;
	}
	
	@GetMapping("/listWithImage")
	public List<PaymentImageVO> listWithImage(@RequestHeader("Authorization") String token){
		MemberClaimVO claimVO =
				tokenService.check(tokenService.removeBearer(token));
//		결제한 상품 번호 리스트
		List<Integer> prNoList = paymentDao.selectPaidPr(claimVO.getMemberId());
	    List<PaymentImageVO> paymentImageList = new ArrayList<>();

	    for(int no : prNoList) {
	    	// 상품번호에 따른 이미지 번호와 결제내역 데이터
	        PaymentImageVO paymentImageVO = paymentDao.selectPaymentImage(no); 
	        if(paymentImageVO != null) {
	            paymentImageList.add(paymentImageVO); //
	        }
	    }
		return paymentImageList;
	}
	
	@PostMapping("/confirmBuy/{paymentNo}")
	public void confirmBuy(
			@PathVariable int paymentNo) {
		//구매 내역의 상태를 확정으로 변경
		paymentDao.confirmBuy(paymentNo);
	}
	
//	거래횟수 카운트 
	@GetMapping("/count/{memberId}")
	public int countPayment(@PathVariable String memberId) {
		return paymentDao.countPayment(memberId);
	}
	
}