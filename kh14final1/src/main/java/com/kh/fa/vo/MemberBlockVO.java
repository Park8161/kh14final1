package com.kh.fa.vo;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class MemberBlockVO {
	private String memberId;
	private String memberName;
	private String memberLevel;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date memberJoin;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date memberLogin;
	
	private String blockTarget;
	private String blockType;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date blockTime;
	private String blockMemo;
	
}
