package com.kh.fa.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ChatroomDto {
	private int chatroomNo;
	private int chatroomProduct;
	private String chatroomSeller;
	private String chatroomBuyer;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date chatroomDate;
}
