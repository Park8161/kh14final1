package com.kh.fa.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class ReviewDto {
	private int reviewNo;
	private String reviewTarget;
	private String reviewWriter;
	private String reviewProduct;
	private String reviewContent;
	private Date reviewWtime;
	private int reviewScore;
}
