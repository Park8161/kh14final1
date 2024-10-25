package com.kh.fa.vo;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class NoticeInsertImageRequestVO {
	private int noticeNo;
	private String noticeWriter;
	private String noticeType;
	private String noticeTitle;
	private String noticeContent;
	private List<MultipartFile> attachList;
}
