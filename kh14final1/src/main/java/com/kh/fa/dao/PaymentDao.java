package com.kh.fa.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.PaymentDetailDto;
import com.kh.fa.dto.PaymentDto;
import com.kh.fa.vo.PaymentImageVO;
import com.kh.fa.vo.PaymentTotalVO;

@Repository
public class PaymentDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public int paymentSequence() {
		return sqlSession.selectOne("payment.paymentSequence");
	}

	public int paymentDetailSequence() {
		return sqlSession.selectOne("payment.paymentDetailSequence");
	}
	
	public void paymentInsert(PaymentDto paymentDto) {
		sqlSession.insert("payment.paymentInsert", paymentDto);
	}

	public void paymentDetailInsert(PaymentDetailDto paymentDetailDto) {
		sqlSession.insert("payment.paymentDetailInsert", paymentDetailDto);
	}

	public List<PaymentDto> selectList(String memberId) {
		return sqlSession.selectList("payment.list", memberId);
	}

	public PaymentDto selectOne(int paymentNo) {
		return sqlSession.selectOne("payment.find", paymentNo);
	}
	
	// mybatis의 resultMap을 이용한 리스트 조회
	public List<PaymentTotalVO> selectTotalList(String memberId){
		return sqlSession.selectList("payment.findTotal", memberId);
	}
	
	// 전체 취소
	public boolean cancelAll(int paymentNo) {
		return sqlSession.update("payment.cancelAll", paymentNo) > 0;
	}
	public boolean cancelAllItem(int paymentNo) {
		return sqlSession.update("payment.cancelAllItem", paymentNo) > 0;
	}
	
	
	public boolean setSoldOut(int productNo) {
		return sqlSession.update("payment.setSoldOut", productNo) > 0;
	}
	
	public List<Integer> selectPaidPr(String memberId){
		List<Integer> list = sqlSession.selectList("payment.selectPaidPr", memberId);
		return list;
	} 
	
//	결제 내역과 결제 내역 상품에 대한 한장의 이미지번호를 추출
	public PaymentImageVO selectPaymentImage(int productNo) {
		return sqlSession.selectOne("payment.selectPaymentImage", productNo);
	}

	// 구매확정
	public void confirmBuy(int productNo) {
		sqlSession.update("payment.confirmBuy", productNo);
	}
	
	// 구매취소
	public void cancelBuy(int productNo) {
		sqlSession.update("payment.cancelBuy", productNo);
	}

//	거래횟수 조회
	public int countPayment(String memberId) {
	return sqlSession.selectOne("payment.countPayment",memberId);
	}
	
}