package com.kh.fa.vo;

import java.util.List;

import com.kh.fa.dto.NoticeDto;

import lombok.Data;

@Data
public class NoticeDetailResponseVO {
	private NoticeDto noticeDto;
	private List<Integer> images;
}
