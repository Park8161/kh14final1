package com.kh.fa.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.ReviewDto;

@Repository
public class ReviewDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	// 시퀀스
	public int sequence() {
		return sqlSession.selectOne("review.sequence");
	}
	
	// 등록
	public void insert(ReviewDto reviewDto) {
		sqlSession.insert("review.insert", reviewDto);
	}
	
	// 목록 + 검색 (페이징 추가는 스킵)
	public List<ReviewDto> selectList(String column, String keyword) {
		Map<String, Object> params = new HashMap<>(); 
		params.put("column", column);
		params.put("keyword", keyword);
		return sqlSession.selectList("review.list", params);
	}
	
	// 상세
	public ReviewDto selectOne(int reviewNo) {
		return sqlSession.selectOne("review.detail", reviewNo);
	}
	
	// 수정
	public boolean update(ReviewDto reviewDto) {
		return sqlSession.update("review.update", reviewDto) > 0;
	}
	
	// 삭제
	public boolean delete(int reviewNo) {
		return sqlSession.update("review.delete", reviewNo) > 0;
	}
	
}
