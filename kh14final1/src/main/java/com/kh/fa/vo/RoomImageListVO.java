package com.kh.fa.vo;

import java.sql.Timestamp;

import lombok.Data;
@Data
public class RoomImageListVO {
    private final String type = "file";
    private String senderMemberId; // 발신자
    private String senderMemberLevel; // 발신자의 등급
    private Timestamp time; // 발신 시각
    private int image;// 보낸 파일 데이터
}