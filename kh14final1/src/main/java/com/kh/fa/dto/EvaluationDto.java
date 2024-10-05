package com.kh.fa.dto;

import lombok.Data;

@Data
public class EvaluationDto {
	private int evaluationNo;
	private String evaluationMember;
	private String evaluationContent;
	private int evaluationScore;
}
