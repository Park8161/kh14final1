package com.kh.fa.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.ReplyDto;


@Repository
public class ReplyDao {

    @Autowired
    private SqlSession sqlSession;

    public int sequence() {
        return sqlSession.selectOne("reply.sequence");
    }

    // 등록
    public void insert(ReplyDto replyDto) {
        sqlSession.insert("reply.insert", replyDto);
    }

    // 목록
    public List<ReplyDto> selectList(int replyQna) {
        return sqlSession.selectList("reply.list", replyQna);
    }
    
    // 수정
    public boolean update(ReplyDto replyDto) {
        return sqlSession.update("reply.edit", replyDto) > 0;
    }

    // 삭제
    public boolean delete(int replyNo) {
        return sqlSession.delete("reply.delete", replyNo) > 0;
    }
    // 상세
    public ReplyDto selectOne(int replyNo) {
    	return sqlSession.selectOne("reply.detail", replyNo);
    }
}