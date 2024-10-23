package com.kh.fa.restcontroller;

import java.net.URISyntaxException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.fa.ProductApproveRequestVO;
import com.kh.fa.dao.PaymentDao;
import com.kh.fa.dao.ProductDao;
import com.kh.fa.dto.PaymentDetailDto;
import com.kh.fa.dto.PaymentDto;
import com.kh.fa.dto.ProductDto;
import com.kh.fa.service.KakaoPayService;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberClaimVO;
import com.kh.fa.vo.ProductBuyRequestVO;
import com.kh.fa.vo.pay.KakaoPayApproveRequestVO;
import com.kh.fa.vo.pay.KakaoPayApproveResponseVO;
import com.kh.fa.vo.pay.KakaoPayReadyRequestVO;
import com.kh.fa.vo.pay.KakaoPayReadyResponseVO;

@RestController
@CrossOrigin
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
	public KakaoPayApproveResponseVO approve(
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
		paymentDao.paymentInsert(paymentDto);//대표정보 등록
		
		
//		int paymentDetailSeq = paymentDao.paymentDetailSequence();//번호 추출
//		PaymentDetailDto paymentDetailDto = new PaymentDetailDto();
//		paymentDetailDto.setPaymentDetailNo(paymentDetailSeq);//번호 설정
//		paymentDetailDto.setPaymentDetailName(productDto.getProductName());//상품명(도서명) 설정
//		paymentDetailDto.setPaymentDetailPrice(productDto.getProductPrice());//상품판매가(도서가격) 설정
//		paymentDetailDto.setPaymentDetailItem(prRequest.getProductNo());//상품번호(도서번호) 설정
//		paymentDetailDto.setPaymentDetailQty(1);//구매수량
//		paymentDetailDto.setPaymentDetailOrigin(paymentSeq);//결제대표번호
//		paymentDao.paymentDetailInsert(paymentDetailDto);
		
		return responseVO;
	}
}
