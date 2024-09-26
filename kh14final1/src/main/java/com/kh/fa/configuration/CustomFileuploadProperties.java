package com.kh.fa.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import lombok.Data;

// custom.fileupload로 만들어진 속성을 불러오기 위한 파일
@Data
@Service
@ConfigurationProperties(prefix="custom.fileupload")
public class CustomFileuploadProperties {
	private String path;
	
	
}
