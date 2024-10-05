package com.kh.fa.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.MemberDto;
import com.kh.fa.vo.MemberComplexRequestVO;

@Repository
public class MemberDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	// 복합 검색 메소드
	public List<MemberDto> complexSearch(MemberComplexRequestVO vo){
		return sqlSession.selectList("member.complexSearch", vo);
	}
	
	// 복합 검색 카운트 메소드
	public int complexSearchCount(MemberComplexRequestVO vo) {
		return sqlSession.selectOne("member.complexSearchCount", vo);
	}

	public MemberDto selectOne(String memberId) {
		return sqlSession.selectOne("member.find", memberId);
	}
	
}
