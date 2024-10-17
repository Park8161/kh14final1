package com.kh.fa.vo;

import java.time.LocalDateTime;

import lombok.Data;

// 사용자에게 보낼 내용을 저장하기 위한 VO
@Data
public class WebSocketResponseVO {
	private final String type = "chat";
	private String senderMemberId; // 발신자
	private String senderMemberLevel; // 발신자의 등급
	private String content; // 보낸 내용
	private LocalDateTime time; // 발신 시각
}
