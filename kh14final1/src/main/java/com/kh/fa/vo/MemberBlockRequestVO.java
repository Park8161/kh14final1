package com.kh.fa.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kh.fa.advice.JsonEmptyIntegerToNullDeserializer;
import com.kh.fa.advice.JsonEmptyStringToNullDeserializer;

import lombok.Data;

@Data
public class MemberBlockRequestVO {
	private String memberId;
	@JsonDeserialize(using = JsonEmptyStringToNullDeserializer.class)
	private String column, keyword;	
	@JsonDeserialize(using = JsonEmptyIntegerToNullDeserializer.class)
	private Integer beginRow, endRow;
	
}
