package com.kh.fa.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class QnaDto {
	private int qnaNo;
	private String qnaWriter;
	private String qnaType;
	private String qnaTitle;
	private String qnaContent;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date qnaWtime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date qnaUtime;
	private int qnaViews;
	private int qnaReplies;
}
