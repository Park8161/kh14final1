package com.kh.fa.vo;

import java.time.LocalDateTime;

import lombok.Data;

// DB에서 불러온 메세지를 저장하기 위한 VO
@Data
public class WebsocketMessageVO {
	private int no;
	private String type;
	private String senderMemberId; // 발신자
	private String senderMemberLevel; // 발신자의 등급
	private String receiverMemberId; // 수신자
	private String content; // 보낸 내용
	private LocalDateTime time; // 발신 시각
}
