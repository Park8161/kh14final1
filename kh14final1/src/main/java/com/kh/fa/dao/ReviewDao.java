package com.kh.fa.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.ReviewDto;
import com.kh.fa.vo.ReviewVO;

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
	
	// 목록 + 검색 (페이징 추가는 스킵) : 판매자 대상 리뷰
	public List<ReviewVO> selectList(String memberId ,String column, String keyword) {
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("column", column);
		params.put("keyword", keyword);
		return sqlSession.selectList("review.list", params);
	}
	
	// 목록 + 검색 (페이징 추가는 스킵) : 내가 쓴 리뷰 검색
	public List<ReviewVO> selectMyList(String memberId ,String column, String keyword) {
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("column", column);
		params.put("keyword", keyword);
		return sqlSession.selectList("review.myList", params);
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
	
	// 판매자 리뷰 개수 카운트
	public int countReview(String memberId) { // 판매자의 아이디
		return sqlSession.selectOne("review.countReview", memberId);
	}
}
