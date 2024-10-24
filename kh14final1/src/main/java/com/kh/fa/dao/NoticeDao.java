package com.kh.fa.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.NoticeDto;
import com.kh.fa.vo.NoticeListRequestVO;

@Repository
public class NoticeDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public int sequence() {
		return sqlSession.selectOne("notice.sequence");
	}
	//등록
	public void insert(NoticeDto noticeDto) {
		sqlSession.insert("notice.insert", noticeDto);
	}
	//목록
	public List<NoticeDto> selectList(){
		return sqlSession.selectList("notice.list");
	}
	// 목록 임시
	public List<NoticeDto> selectList(String column, String keyword){
		Map<String, Object> params = new HashMap<>();
		params.put("column", column);
		params.put("keyword", keyword);
		return sqlSession.selectList("notice.listSearch", params);
	}
	//상세
	public NoticeDto selectOne(int noticeNo) {
		return sqlSession.selectOne("notice.detail", noticeNo);
	}
	//수정
	public boolean update(NoticeDto noticeDto) {
		return sqlSession.update("notice.edit", noticeDto) > 0;
	}
	//삭제
	public boolean delete(int noticeNo) {
		return sqlSession.delete("notice.delete", noticeNo) > 0;
	}
	//목록 + 페이징 + 검색
	public List<NoticeDto> selectListByPaging(NoticeListRequestVO vo){
		return sqlSession.selectList("notice.listByPaging", vo);
	}
	public int countWithPaging(NoticeListRequestVO vo) {
		return sqlSession.selectOne("notice.count", vo);
	}
}