package com.kh.fa.vo;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kh.fa.dto.NoticeDto;

import lombok.Data;

@Data
public class NoticeEditRequestVO {	
	private int noticeNo;
	private String noticeType;
	private String noticeTitle;
	private String noticeContent;
	private List<MultipartFile> attachList;//첨부 파일 정보 리스트
	private List<Integer> originList;// 기존 이미지의 첨부파일 번호
}
