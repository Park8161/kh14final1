package com.kh.fa.dao;

import java.util.HashMap;

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
		sqlSession.insert("unread.insert", unreadDto);
	}
	
	public void update(UnreadDto unreadDto) {
		sqlSession.update("unread.update", unreadDto);
	}
	
	public void setZero(UnreadDto unreadDto) {
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

	public int countAll(String memberId) {
		Integer result = sqlSession.selectOne("unread.countAll", memberId);
		if(result == null)return 0;
		else return result;
	}

//	public void setZero(String memberId, int roomNo) {
//		UnreadDto unreadDto = new UnreadDto();
//		System.out.println("setZero실행");
//		unreadDto.setMemberId(memberId);
//		unreadDto.setRoomNo(roomNo);
//		sqlSession.update("unread.setZero", unreadDto);
//	}
}
