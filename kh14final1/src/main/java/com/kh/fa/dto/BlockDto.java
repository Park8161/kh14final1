package com.kh.fa.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class BlockDto {
	private int blockNo;
	private String blockOwner;
	private String blockTarget;
	private String blockType;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date blockTime;
	private String blockMemo;
}
