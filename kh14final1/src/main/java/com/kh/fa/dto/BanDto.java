package com.kh.fa.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class BanDto {
	private int banNo;
	private String banType;
	private String banMemo;
	private Date banTime;
	private String banTarget;
}
