package com.kh.fa.vo;

import java.sql.Date;

import lombok.Data;

@Data
public class ReviewVO {
	private int reviewNo;
	private String reviewTarget;
	private String reviewWriter;
	private int reviewProduct;
	private String reviewContent;
	private Date reviewWtime;
	private int reviewScore;
	
	private String ProductName;
}
