package com.kh.fa.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.ProductDto;

@Repository
public class ProductDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	// 시퀀스 번호 생성
	public int sequence() {
		return sqlSession.selectOne("product.sequence");
	}
	
	// 등록
	public void insert(ProductDto productDto) {
		sqlSession.insert("product.insert", productDto);
	}

	// 상품 테이블과 첨부 테이블 연결
	public void connect(int productNo, int attachmentNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("productNo", productNo);
		params.put("attachmentNo", attachmentNo);
		sqlSession.insert("product.connect", params);
	}
	
	// 상세 조회
	public ProductDto selectOne(int productNo) {
		return sqlSession.selectOne("product.detail", productNo);
	}

	
	
	
	
}
