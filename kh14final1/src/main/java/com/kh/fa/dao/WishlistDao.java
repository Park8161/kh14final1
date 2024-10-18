package com.kh.fa.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.WishlistDto;

@Repository
public class WishlistDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	// 관심 추가
	public void insert(WishlistDto wishlistDto) {
		sqlSession.insert("wishlist.insert", wishlistDto);
	}
	
	// 관심 취소
	public boolean delete(String memberId, int productNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("productNo", productNo);
		return sqlSession.delete("wishlist.delete", params) > 0;
	}
	
	// 관심 목록
	public List<WishlistDto> selectList(){
		return sqlSession.selectList("wishlist.list");
	}
	
}
