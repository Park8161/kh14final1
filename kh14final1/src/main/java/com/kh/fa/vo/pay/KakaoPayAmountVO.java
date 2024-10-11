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
public class KakaoPayAmountVO {
	private int total; // 전체 금액
	private int taxFree; // 비과세
	private int vat; // 부가세
	private int point; // 포인트 사용 금액
	private int discount; // 할인 금액
	private int greenDeposit; // 컵 보증금(환경부담금)
}
