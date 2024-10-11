package com.kh.fa.vo;

import java.util.List;

import com.kh.fa.dto.PaymentDetailDto;
import com.kh.fa.dto.PaymentDto;
import com.kh.fa.vo.pay.KakaoPayOrderResponseVO;

import lombok.Data;

@Data
public class PaymentInfoVO {
	private PaymentDto paymentDto;
	private List<PaymentDetailDto> paymentDetailList;
	private KakaoPayOrderResponseVO responseVO;
}
