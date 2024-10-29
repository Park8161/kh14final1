package com.kh.fa.vo;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class WebsocketFileRequestVO {
	private List<MultipartFile> attachList;
}
