package com.kh.fa.dto;

import lombok.Data;

@Data
public class UnreadDto {
	private int roomNo;
	private String memberId;
	private int unread;
}
