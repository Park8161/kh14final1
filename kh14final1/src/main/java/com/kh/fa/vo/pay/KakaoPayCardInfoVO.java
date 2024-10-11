package com.kh.fa.vo.pay;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
//snake case를 camel case로 변환하는 기능
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
//적힌 필드 외에 모르는 항목이 있으면 스킵하는 기능
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoPayCardInfoVO {
	private String kakaoPayPurchaseCorp; // 카카오페이 매입사명
	private String kakaoPayPurchaseCorpCode; // 카카오페이 매입사코드
	private String kakaoPayIssuerCorp; // 카카오페이 발급사명
	private String kakaoPayIssuerCorpCode; // 카카오페이 발급사코드
	private String bin; // 카드 BIN
	private String cardType; // 카드 타입
	private String installMonth; // 할부 개월수
	private String approvedId; // 카드사 승인번호
	private String cardMid; // 카드가 가맹점 번호
	private String interestFreeInstall; // 무이자 할부 여부 (Y/N)
	private String installmentType; // 할부 유형(CARD_INSTALLMENT-업종무이자/SHARE_INSTALLMENT-분담무이자)
	private String cardItemCode; // 카드 상품 코드
}
