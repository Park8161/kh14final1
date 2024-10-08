package com.kh.fa.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.MemberTokenDto;

@Repository
public class MemberTokenDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public void insert(MemberTokenDto memberTokenDto) {
		sqlSession.insert("memberToken.insert", memberTokenDto);
	}

	public MemberTokenDto selectOne(MemberTokenDto memberTokenDto) {
		return sqlSession.selectOne("memberToken.check", memberTokenDto);
	}
	
	// 회원 탈퇴용
	public boolean remove(String memberId) {
		return sqlSession.delete("memberToken.remove", memberId) > 0;
	}
	
	public boolean delete(MemberTokenDto memberTokenDto) {
		return sqlSession.delete("memberToken.delete", memberTokenDto) > 0;
	}
	
	public boolean clean(int minute) {
		return sqlSession.delete("memberToken.clean", minute) > 0;
	}	
	
}
