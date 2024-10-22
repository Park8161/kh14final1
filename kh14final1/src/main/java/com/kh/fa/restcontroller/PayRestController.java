package com.kh.fa.restcontroller;

import java.net.URISyntaxException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.fa.dao.PaymentDao;
import com.kh.fa.dao.ProductDao;
import com.kh.fa.dto.ProductDto;
import com.kh.fa.service.KakaoPayService;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberClaimVO;
import com.kh.fa.vo.ProductBuyRequestVO;
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
}
