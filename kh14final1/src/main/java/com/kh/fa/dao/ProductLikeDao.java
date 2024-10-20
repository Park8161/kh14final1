package com.kh.fa.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProductLikeDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	// 좋아요 기록 유무 조회
	public boolean check(int productNo, String memberId) {
		Map<String, Object> params = new HashMap<>();
		params.put("productNo", productNo);
		params.put("memberId", memberId);
		int result = sqlSession.selectOne("productLike.check", params);
		return result > 0;
	}
	
	// 좋아요 카운트
	public int count(int productNo) {
		return sqlSession.selectOne("productLike.count", productNo);
	}

	// 좋아요 추가
	public void insert(String memberId, int productNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("productNo", productNo);
		params.put("memberId", memberId);
		sqlSession.insert("productLike.insert", params);
	}
	
	// 좋아요 취소
	public boolean delete(String memberId, int productNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("productNo", productNo);
		params.put("memberId", memberId);
		return sqlSession.delete("productLike.delete", params) > 0;
	}	
	
}
