package com.kh.fa.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.kh.fa.dto.ReplyDto;

@Service
public class ReplyMapper implements RowMapper<ReplyDto>{
	@Override
	public ReplyDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		ReplyDto replyDto = new ReplyDto();
		replyDto.setReplyNo(rs.getInt("reply_no"));
		replyDto.setReplyWriter(rs.getString("reply_writer"));
		replyDto.setReplyQna(rs.getInt("reply_qna"));
		replyDto.setReplyContent(rs.getString("reply_content"));
		replyDto.setReplyWtime(rs.getDate("reply_wtime"));
		replyDto.setReplyUtime(rs.getDate("reply_utime"));
		return replyDto;
	}
}