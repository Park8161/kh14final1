package com.kh.fa.vo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class WebSocketFileResponseVO {
	private final String type = "file";
	private String senderMemberId; // 발신자
	private String senderMemberLevel; // 발신자의 등급
	private LocalDateTime time; // 발신 시각
	private int image;// 보낸 파일 데이터
}
