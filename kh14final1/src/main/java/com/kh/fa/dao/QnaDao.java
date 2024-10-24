package com.kh.fa.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.QnaDto;
import com.kh.fa.vo.QnaListRequestVO;

@Repository
public class QnaDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public int sequence() {
		return sqlSession.selectOne("qna.sequence");
	}
	//등록
	public void insert(QnaDto qnaDto) {
		sqlSession.insert("qna.insert", qnaDto);
	}
	//목록
	public List<QnaDto> selectList(){
		return sqlSession.selectList("qna.list");
	}
	// 목록 임시
	public List<QnaDto> selectList(String column, String keyword){
		Map<String, Object> params = new HashMap<>();
		params.put("column", column);
		params.put("keyword", keyword);
		return sqlSession.selectList("qna.listSearch", params);
	}
	//상세
	public QnaDto selectOne(int qnaNo) {
		return sqlSession.selectOne("qna.detail", qnaNo);
	}
	//수정
	public boolean update(QnaDto qnaDto) {
		return sqlSession.update("qna.edit", qnaDto) > 0;
	}
	//삭제
	public boolean delete(int qnaNo) {
		return sqlSession.delete("qna.delete", qnaNo) > 0;
	}
	//목록 + 페이징 + 검색
	public List<QnaDto> selectListByPaging(QnaListRequestVO vo){
		return sqlSession.selectList("qna.listByPaging", vo);
	}
	public int countWithPaging(QnaListRequestVO vo) {
		return sqlSession.selectOne("qna.count", vo);
	}
}