package com.kh.fa.vo;

import java.util.List;

import lombok.Data;

@Data
public class WebSocketMessageMoreVO {
	private boolean last;
	private List<WebsocketMessageVO> messageList;
}
