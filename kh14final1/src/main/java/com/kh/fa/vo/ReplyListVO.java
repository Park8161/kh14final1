package com.kh.fa.vo;

import java.util.List;

import com.kh.fa.dto.ReplyDto;

import lombok.Data;

@Data
public class ReplyListVO {
	private List<ReplyDto> list;
	private int totalPage;
	private int currentPage;
}