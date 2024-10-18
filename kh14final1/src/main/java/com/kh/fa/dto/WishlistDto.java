package com.kh.fa.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class WishlistDto {
	private int wishlistNo;
	private String wishlistMember;
	private int wishlistProduct;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date wishlistDate;
}
