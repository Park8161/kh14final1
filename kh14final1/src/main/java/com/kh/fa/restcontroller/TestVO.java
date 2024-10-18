package com.kh.fa.restcontroller;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class TestVO {
	private String memberId;
	private String memberPw;
	private MultipartFile attach;
}
