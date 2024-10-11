package com.kh.fa.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.PaymentDetailDto;
import com.kh.fa.dto.PaymentDto;
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

	public List<PaymentDetailDto> selectDetailList(int paymentNo) {
		return sqlSession.selectList("payment.findDetail", paymentNo);
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
	
	// 부분 취소
	public boolean cancelItem(int paymentDetailNo) {
		return sqlSession.update("payment.cancelItem", paymentDetailNo) > 0;
	}
	public boolean decreaseItemRemain(int paymentNo, int money) {
//		Map<Object, Object> map = Map.of("paymentNo", paymentNo, "money", money); // JAVA 9 이상만 가능
		Map<Object, Object> map = new HashMap<>();
		map.put("paymentNo", paymentNo);
		map.put("money", money);
		return sqlSession.update("payment.decreaseItemRemain", map) > 0;
	}

	public PaymentDetailDto selectDetailOne(int paymentDetailNo) {
		return sqlSession.selectOne("payment.selectDetailOne", paymentDetailNo);
	}
	
}
