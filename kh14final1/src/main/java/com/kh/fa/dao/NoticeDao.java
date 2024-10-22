package com.kh.fa.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.NoticeDto;
import com.kh.fa.vo.NoticeListRequestVO;
import com.kh.fa.vo.PageVO;

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
	//상세
	public NoticeDto selectOne(int noticeNo) {
		return sqlSession.selectOne("notice.detail", noticeNo);
	}
	//수정
	public boolean edit(NoticeDto noticeDto) {
		return sqlSession.update("notice.edit", noticeDto) > 0;
	}
	//삭제
	public boolean delete(int noticeNo) {
		return sqlSession.delete("notice.delete", noticeNo) > 0;
	}
	public List<NoticeDto> selectListByPaging(NoticeListRequestVO vo){
		return sqlSession.selectList("notice.list", vo);
	}
	public int countWithPaging(NoticeListRequestVO vo) {
		return sqlSession.selectOne("notice.count", vo);
	}
}