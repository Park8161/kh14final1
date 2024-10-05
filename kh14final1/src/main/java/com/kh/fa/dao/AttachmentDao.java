package com.kh.fa.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.AttachmentDto;

@Repository
public class AttachmentDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private SqlSession sqlSession;
	
	public int sequence() {
		return sqlSession.selectOne("attachment.sequence");
	}

	public void insert(AttachmentDto attachmentDto) {
		sqlSession.insert("attachment.add", attachmentDto);		
	}
	
	// 상세 조회 (다운로드 기능 구현)
	public AttachmentDto selectOne(int attachmentNo) {
		return sqlSession.selectOne("attachment.find", attachmentNo);
	}
	
	// 실물 파일 및 DB 삭제
	public boolean delete(int attachmentNo) {
		return sqlSession.delete("attachment.remove", attachmentNo) > 0;		
	}
	
	
	
	
	
	
	
	
	
	
}
