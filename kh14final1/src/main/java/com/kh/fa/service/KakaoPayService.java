package com.kh.fa.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.kh.fa.configuration.KakaoPayProperties;
import com.kh.fa.vo.pay.KakaoPayApproveRequestVO;
import com.kh.fa.vo.pay.KakaoPayApproveResponseVO;
import com.kh.fa.vo.pay.KakaoPayCancelRequestVO;
import com.kh.fa.vo.pay.KakaoPayCancelResponseVO;
import com.kh.fa.vo.pay.KakaoPayOrderRequestVO;
import com.kh.fa.vo.pay.KakaoPayOrderResponseVO;
import com.kh.fa.vo.pay.KakaoPayReadyRequestVO;
import com.kh.fa.vo.pay.KakaoPayReadyResponseVO;

@Service
public class KakaoPayService {
	
	@Autowired
	private KakaoPayProperties kakaoPayProperties; // cid, secret
	@Autowired
	private RestTemplate template; // 전송 도구
	@Autowired
	private HttpHeaders headers; // 전송 헤더
	
	// 결제 준비(ready)
	public KakaoPayReadyResponseVO ready(KakaoPayReadyRequestVO request) throws URISyntaxException {
		// 주소 생성
		URI uri = new URI("https://open-api.kakaopay.com/online/v1/payment/ready"); // 유효하지 않은 주소면 오류가 발생
		
		// 바디 생성
		Map<String, String> body = new HashMap<>();
		body.put("cid", kakaoPayProperties.getCid()); // 가맹점 번호
		body.put("partner_order_id", request.getPartnerOrderId()); // 거래 번호
		body.put("partner_user_id", request.getPartnerUserId()); // 거래자 아이디
		body.put("item_name", request.getItemName());
		body.put("quantity", "1");
		body.put("total_amount", String.valueOf(request.getTotalAmount())); // int를 String으로 변환
		body.put("tax_free_amount", "0");
		body.put("approval_url", request.getApprovalUrl() + "/" + request.getPartnerOrderId());
		body.put("cancel_url", request.getCancelUrl());
		body.put("fail_url", request.getFailUrl());
		
		// 통신 요청 정보 객체 생성
		HttpEntity entity = new HttpEntity(body, headers);
		
		// 전송 후 응답 받기
		KakaoPayReadyResponseVO response = template.postForObject(uri, entity, KakaoPayReadyResponseVO.class);
		
		return response;
	}
	
	// 결제 승인(approve)
	public KakaoPayApproveResponseVO approve(KakaoPayApproveRequestVO request) throws URISyntaxException {
		// 주소 생성
		URI uri = new URI("https://open-api.kakaopay.com/online/v1/payment/approve"); // 유효하지 않은 주소면 오류가 발생
		// 바디 생성
		Map<String, String> body = new HashMap<>();
		body.put("cid", kakaoPayProperties.getCid()); // 가맹점 번호
		body.put("partner_order_id", request.getPartnerOrderId()); // 준비 단계의 거래 번호
		body.put("partner_user_id", request.getPartnerUserId()); // 준비 단계의 거래자 아이디
		body.put("tid", request.getTid()); // 거래 번호
		body.put("pg_token", request.getPgToken()); // 인증(검수용) 토큰
		
		// 통신 요청 정보 객체 생성
		HttpEntity entity = new HttpEntity(body, headers);
		
		// 전송 후 응답 받기
		KakaoPayApproveResponseVO response = template.postForObject(uri, entity, KakaoPayApproveResponseVO.class);
		
		return response;
	}
	

	// 결제 조회
	public KakaoPayOrderResponseVO order(KakaoPayOrderRequestVO request) throws URISyntaxException {
		URI uri = new URI("https://open-api.kakaopay.com/online/v1/payment/order");
		
		Map<String, String> body = new HashMap<>();
		body.put("cid", kakaoPayProperties.getCid());
		body.put("tid", request.getTid());
		
		HttpEntity entity = new HttpEntity(body, headers);
		
		KakaoPayOrderResponseVO response = template.postForObject(uri, entity, KakaoPayOrderResponseVO.class);
		
		return response;
	}
	
	// 결제 취소
	public KakaoPayCancelResponseVO cancel(KakaoPayCancelRequestVO request) throws URISyntaxException {
		URI uri = new URI("https://open-api.kakaopay.com/online/v1/payment/cancel");
		
		Map<String, String> body = new HashMap<>();
		body.put("cid", kakaoPayProperties.getCid());
		body.put("tid", request.getTid());
		body.put("cancel_amount", String.valueOf(request.getCancelAmount()));
		body.put("cancel_tax_free_amount", String.valueOf(request.getCancelTaxFreeAmount()));
		
		HttpEntity entity = new HttpEntity(body, headers);
		
		KakaoPayCancelResponseVO response = template.postForObject(uri, entity, KakaoPayCancelResponseVO.class);
		
		return response;
	}
	
}
