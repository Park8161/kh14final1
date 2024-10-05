package com.kh.fa.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class FaqDto {
	private int faqNo;
	private String faqWriter;
	private String faqType;
	private String faqTitle;
	private String faqContent;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date faqWtime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date faqUtime;
	private int faqViews;
	private int faqReplies;
}
