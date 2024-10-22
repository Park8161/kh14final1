package com.kh.fa.vo;

import java.util.List;

import com.kh.fa.dto.NoticeDto;

import lombok.Data;

@Data
public class NoticeListResponseVO {
	private List<NoticeDto> noticeList; // 검색 결과
	private boolean isLast;
	private int count;
}
