package com.kh.fa.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.ProductDto;
import com.kh.fa.vo.ProductListRequestVO;

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
	
	// 목록 + 페이징 + 검색
	public List<ProductDto> selectListByPaging(ProductListRequestVO requestVO){
		return sqlSession.selectList("product.list", requestVO);
	}
	
	// 목록 카운트
	public int countWithPaging(ProductListRequestVO requestVO) {
		return sqlSession.selectOne("product.count", requestVO);
	}
	
	// 상세 조회
	public ProductDto selectOne(int productNo) {
		return sqlSession.selectOne("product.detail", productNo);
	}

	
	
	
	
}
