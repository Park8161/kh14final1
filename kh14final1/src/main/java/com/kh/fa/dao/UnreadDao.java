package com.kh.fa.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.RoomMemberDto;
import com.kh.fa.dto.UnreadDto;

@Repository
public class UnreadDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public UnreadDto selectOne(UnreadDto unreadDto) {
		return sqlSession.selectOne("unread.find", unreadDto);
	}
	
	public void insert(UnreadDto unreadDto) {
		System.out.println("insert/unreadDao 실행");
		sqlSession.insert("unread.insert", unreadDto);
	}
	
	public void update(UnreadDto unreadDto) {
		System.out.println("update/unreadDao실행");
		sqlSession.update("unread.update", unreadDto);
	}
	
	public void setZero(UnreadDto unreadDto) {
		System.out.println("setZero/unreadDao 실행");
		sqlSession.update("unread.setZero", unreadDto);
	}
	
	public int count(RoomMemberDto roomMemberDto) {
		if(roomMemberDto == null) return 0;
		else {
			Integer result = sqlSession.selectOne("unread.count", roomMemberDto);
			if(result == null) return 0;
			else return result;
		}
	}

	public int count(UnreadDto unreadDto) {
		if(unreadDto == null) {
			return 0;
		}
		else {
			Integer result = sqlSession.selectOne("unread.count", unreadDto);
			if(result == null) return 0;
			else return result;
		}
	}
	
}
