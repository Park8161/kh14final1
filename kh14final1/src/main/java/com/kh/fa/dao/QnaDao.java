package com.kh.fa.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.QnaDto;
import com.kh.fa.vo.QnaListRequestVO;
import com.kh.fa.vo.PageVO;

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
	//상세
	public QnaDto selectOne(int qnaNo) {
		return sqlSession.selectOne("qna.detail", qnaNo);
	}
	//수정
	public boolean edit(QnaDto qnaDto) {
		return sqlSession.update("qna.edit", qnaDto) > 0;
	}
	//삭제
	public boolean delete(int qnaNo) {
		return sqlSession.delete("qna.delete", qnaNo) > 0;
	}
	public List<QnaDto> selectListByPaging(QnaListRequestVO vo){
		return sqlSession.selectList("qna.list", vo);
	}
	public int countWithPaging(QnaListRequestVO vo) {
		return sqlSession.selectOne("qna.count", vo);
	}
}