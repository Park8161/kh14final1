package com.kh.fa.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.UnreadDto;

@Repository
public class UnreadDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public boolean selectOne(UnreadDto unreadDto) {
		int result = sqlSession.selectOne("unread.isExist", unreadDto);
		return result > 0;
	}
	
	public void insert(UnreadDto unreadDto) {
		sqlSession.insert("unread.insert", unreadDto);
	}
	
	public void update(UnreadDto unreadDto) {
		sqlSession.insert("unread.update", unreadDto);
	}
	
}
