package com.kh.fa.dto;

import lombok.Data;

@Data
public class AttachmentDto {
	private int attachmentNo;
	private String attachmentName;
	private String attachmentType;
	private long attachmentSize;
}
