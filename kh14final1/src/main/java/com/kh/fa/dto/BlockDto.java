package com.kh.fa.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class BlockDto {
	private int blockNo;
	private String blockOwner;
	private String blockTarget;
	private String blockType;
	private Date blockTime;
	private String blockMemo;
}
