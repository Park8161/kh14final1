package com.kh.fa.vo;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class RoomListVO {
	private String productName;
	private int productNo;
	private int roomNo;
	private String memberId;
	private String roomMessageContent;
	private Timestamp roomMessageTime;
}