package com.kh.fa.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.BlockDto;
import com.kh.fa.vo.MemberBlockRequestVO;
import com.kh.fa.vo.MemberBlockVO;

@Repository
public class BlockDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	// 차단 목록 + 페이징 
	public List<MemberBlockVO> selectListByPaging(MemberBlockRequestVO vo){
		return sqlSession.selectList("block.list", vo);
	}
	
	// 차단 목록 검색 카운트
	public int countWithPaging(MemberBlockRequestVO vo) {
		return sqlSession.selectOne("block.count", vo);
	}
	
	// 차단 등록
	public void insertBlock(BlockDto blockDto) {
		sqlSession.insert("block.insert", blockDto);
	}
	
	// 차단 해제
	public void cancelBlock(BlockDto blockDto) {
		sqlSession.insert("block.cancel", blockDto);
	}

	// 상대방의 마지막 차단/해제 상태 확인
	public BlockDto selectLastOne(BlockDto blockDto) {
		return sqlSession.selectOne("block.selectLastOne", blockDto);
	}
	
	
	
}
