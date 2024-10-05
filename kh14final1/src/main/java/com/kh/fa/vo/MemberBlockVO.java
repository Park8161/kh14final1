package com.kh.fa.vo;

import java.sql.Date;

import lombok.Data;

@Data
public class MemberBlockVO {
	private String memberId;
	private String memberPw;
	private String memberName;
	private String memberLevel;
	private String memberEmail;
	private String memberPost;
	private String memberAddress1;
	private String memberAddress2;
	private String membercontact;
	private String memberBirth;
	private Date memberJoin;
	private Date memberLogin;
	private int memberPoint;
	
	private int blockNo;
	private String blockOwner;
	private String blockTarget;
	private String blockType;
	private Date blockTime;
	private String blockMemo;
	
}
