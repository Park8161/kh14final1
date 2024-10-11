package com.kh.fa.vo;

import java.util.List;

import com.kh.fa.dto.PaymentDetailDto;
import com.kh.fa.dto.PaymentDto;

import lombok.Data;

@Data
public class PaymentTotalVO {
	private PaymentDto paymentDto;
	private List<PaymentDetailDto> paymentDetailList;
}
