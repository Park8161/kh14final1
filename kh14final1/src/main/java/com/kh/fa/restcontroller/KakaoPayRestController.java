package com.kh.fa.restcontroller;

import java.net.URISyntaxException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.fa.dao.PaymentDao;
import com.kh.fa.dto.PaymentDto;
import com.kh.fa.error.TargetNotFoundException;
import com.kh.fa.service.KakaoPayService;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberClaimVO;
import com.kh.fa.vo.pay.KakaoPayApproveRequestVO;
import com.kh.fa.vo.pay.KakaoPayApproveResponseVO;
import com.kh.fa.vo.pay.KakaoPayCancelRequestVO;
import com.kh.fa.vo.pay.KakaoPayCancelResponseVO;
import com.kh.fa.vo.pay.KakaoPayOrderRequestVO;
import com.kh.fa.vo.pay.KakaoPayOrderResponseVO;
import com.kh.fa.vo.pay.KakaoPayReadyRequestVO;
import com.kh.fa.vo.pay.KakaoPayReadyResponseVO;

@RestController
@RequestMapping("/kakaopay")
@CrossOrigin // CORS 해제 설정
public class KakaoPayRestController {
	
	@Autowired
	private KakaoPayService kakaoPayService;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private PaymentDao paymentDao;
	
	// 결제 준비(ready)
	@PostMapping("/ready")
	public KakaoPayReadyResponseVO ready(@RequestHeader("Authorization") String token, 
										@RequestBody KakaoPayReadyRequestVO request) throws URISyntaxException {
		
		// request에는 2개의 정보만 들어온다(itemName, totalAmount)
		// 나머지 정보는 백엔드가 생성하거나 추출하여 설정헤줘야 한다
		
		request.setPartnerOrderId(UUID.randomUUID().toString());
		
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		request.setPartnerUserId(claimVO.getMemberId());
		
		KakaoPayReadyResponseVO response = kakaoPayService.ready(request);
		return response;
	}
	
	// 결제 승인(approve)
	@PostMapping("/approve")
	public KakaoPayApproveResponseVO approve(@RequestHeader("Authorization") String token, 
											@RequestBody KakaoPayApproveRequestVO request) throws URISyntaxException {
		
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		request.setPartnerUserId(claimVO.getMemberId()); // 주문자 정보 추가
		
		KakaoPayApproveResponseVO response = kakaoPayService.approve(request);
		return response;
	}
	
	// 결제 조회(order) - 문서에는 postmapping이지만 tid하나만 필요하므로 getmapping과 경로변수로 대체 가능
	@GetMapping("/order/{tid}")
	public KakaoPayOrderResponseVO order(@PathVariable String tid) throws URISyntaxException {
		KakaoPayOrderRequestVO request = new KakaoPayOrderRequestVO();
		request.setTid(tid);		
		return kakaoPayService.order(request);
	}
	
//	// 결제 상세 내역
//	@GetMapping("/detail/{paymentNo}")
//	public PaymentInfoVO detail(@RequestHeader("Authorization") String token, 
//			@PathVariable int paymentNo) throws URISyntaxException {
//		// 결제 내역
//		PaymentDto paymentDto = paymentDao.selectOne(paymentNo);
//		if(paymentDto == null) throw new TargetNotFoundException("존재하지 않는 결제내역");
//		// 본인 소유 검증
//		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
//		if(paymentDto.getPaymentBuyer().equals(claimVO.getMemberId()) == false) throw new TargetNotFoundException("결제내역의 소유자가 아닙니다");
//		// 결제 상세 내역
//		List<PaymentDetailDto> list = paymentDao.selectDetailList(paymentNo);
//		// 카카오페이 조회내역
//		KakaoPayOrderRequestVO requestVO = new KakaoPayOrderRequestVO();
//		requestVO.setTid(paymentDto.getPaymentTid());
//		KakaoPayOrderResponseVO responseVO = kakaoPayService.order(requestVO);
//		// 반환 형태 생성
//		PaymentInfoVO infoVO = new PaymentInfoVO();
//		infoVO.setPaymentDto(paymentDto);
//		infoVO.setPaymentDetailList(list);
//		infoVO.setResponseVO(responseVO);
//		return infoVO;
//	}
	
	// 결제 취소 - 전체취소는 paymentNo 요구, 항목취소는 paymentDetailNo 요구
	// 전체 취소
	@Transactional
	@DeleteMapping("/cancelAll/{paymentNo}")
	public KakaoPayCancelResponseVO cancelAll(@RequestHeader("Authorization") String token, @PathVariable int paymentNo) throws URISyntaxException {
		// 결제 내역 확인
		PaymentDto paymentDto = paymentDao.selectOne(paymentNo);
		if(paymentDto == null) throw new TargetNotFoundException("존재하지 않는 결제정보");		
		// 본인 소유 검증
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		if(paymentDto.getPaymentBuyer().equals(claimVO.getMemberId()) == false) throw new TargetNotFoundException("소유자 불일치");
		if(paymentDto.getPaymentRemain() == 0) throw new TargetNotFoundException("이미 취소된 결제");
		// [1] 카카오페이에 해당 결제 거래번호에 대한 남은 금액을 취소해달라고 요청
		KakaoPayCancelRequestVO request = new KakaoPayCancelRequestVO();
		request.setTid(paymentDto.getPaymentTid());
		request.setCancelAmount(paymentDto.getPaymentRemain());
		KakaoPayCancelResponseVO response = kakaoPayService.cancel(request);
		// [2] payment 테이블의 잔여금액을 0으로 변경
		paymentDao.cancelAll(paymentNo); // 될거면 한번에 다 되어야 함 >> Transactional
		// [3] payment_detail 테이블의 관련항목의 상태를 취소로 변경
		paymentDao.cancelAllItem(paymentNo); // 될거면 한번에 다 되어야 함 >> Transactional
		
		// 구매 취소함으로 인해 판매상태 다시 판매중으로 롤백
		paymentDao.cancelBuy(paymentDto.getProductNo());
		
		return response;
	}

	
	
}