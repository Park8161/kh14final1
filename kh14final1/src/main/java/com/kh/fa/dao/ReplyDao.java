package com.kh.fa.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.ReplyDto;


@Repository
public class ReplyDao {

    @Autowired
    private SqlSession sqlSession; // MyBatis를 위한 SqlSession 사용

    // 다음 시퀀스 값을 가져오는 메서드
    public int replysequence() {
        return sqlSession.selectOne("reply.sequence");
    }

    // 새로운 댓글 삽입
    public void insert(ReplyDto replyDto) {
        sqlSession.insert("cper.ReplyMapper.insertReply", replyDto);
    }

    // 댓글 목록 조회 (기본)
    public List<ReplyDto> selectList(int replyOrigin) {
        return sqlSession.selectList("com.kh.fa.mapper.ReplyMapper.selectList", replyOrigin);
    }

    // 페이지네이션을 고려한 댓글 목록 조회
    public List<ReplyDto> selectList(int replyOrigin, int page, int size) {
        int endRow = page * size;
        int beginRow = endRow - (size - 1);
        return sqlSession.selectList("com.kh.fa.mapper.ReplyMapper.selectListPaginated", 
                                      new PagingParams(replyOrigin, beginRow, endRow));
    }

    // 댓글 삭제
    public boolean delete(int replyNo) {
        return sqlSession.delete("com.kh.fa.mapper.ReplyMapper.deleteReply", replyNo) > 0;
    }

    // 댓글 하나 조회
    public ReplyDto selectOne(int replyNo) {
        return sqlSession.selectOne("com.kh.fa.mapper.ReplyMapper.selectReply", replyNo);
    }

    // 댓글 수정
    public boolean update(ReplyDto replyDto) {
        return sqlSession.update("com.kh.fa.mapper.ReplyMapper.updateReply", replyDto) > 0;
    }

    // 댓글 수 카운트
    public int count(int replyOrigin) {
        return sqlSession.selectOne("com.kh.fa.mapper.ReplyMapper.count", replyOrigin);
    }

    // 페이지네이션 매개변수를 위한 내부 클래스
    public static class PagingParams {
        private int replyOrigin;
        private int beginRow;
        private int endRow;

        public PagingParams(int replyOrigin, int beginRow, int endRow) {
            this.replyOrigin = replyOrigin;
            this.beginRow = beginRow;
            this.endRow = endRow;
        }

        // Getter와 Setter
        public int getReplyOrigin() { return replyOrigin; }
        public void setReplyOrigin(int replyOrigin) { this.replyOrigin = replyOrigin; }
        public int getBeginRow() { return beginRow; }
        public void setBeginRow(int beginRow) { this.beginRow = beginRow; }
        public int getEndRow() { return endRow; }
        public void setEndRow(int endRow) { this.endRow = endRow; }
    }
}
