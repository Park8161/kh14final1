package com.kh.fa.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class RoomMessageDto {
	private int roomMessageNo;
	private String roomMessageType;
	private String roomMessageSender;
	private String roomMessageReceiver;
	private String roomMessageContent;
	private Timestamp roomMessageTime;
	private char roomMessageRead;
	private int roomNo;
}
