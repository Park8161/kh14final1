package com.kh.fa.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.kh.fa.dao.RoomDao;
import com.kh.fa.dao.RoomMessageDao;
import com.kh.fa.dto.RoomMemberDto;
import com.kh.fa.dto.RoomMessageDto;
import com.kh.fa.dto.UnreadDto;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberClaimVO;
import com.kh.fa.vo.WebSocketRequestVO;
import com.kh.fa.vo.WebSocketResponseVO;

@Controller
public class RoomMessageController {
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private RoomMessageDao roomMessageDao;
	@Autowired
	private RoomDao roomDao;
	
	@MessageMapping("/room/{roomNo}")
	public void chat(@DestinationVariable int roomNo, Message<WebSocketRequestVO> message) {
		// 토큰 검사 및 해석
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String accessToken = accessor.getFirstNativeHeader("accessToken");
		if(accessToken == null) return;
				
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(accessToken));
		RoomMemberDto roomMemberDto = new RoomMemberDto();
		roomMemberDto.setMemberId(claimVO.getMemberId());
		roomMemberDto.setRoomNo(roomNo);
//		상대방 아이디
		String anotherMember = roomDao.selectAnother(roomMemberDto);
		System.out.println("anoterMember/roomMemssageController"+anotherMember);
		UnreadDto unreadDto = new UnreadDto();
//		unread Dto에 roomNo, memberId, unread(count) 반환 
		unreadDto.setMemberId(anotherMember);
		unreadDto.setRoomNo(roomNo);
		
//		있으면 업데이트 없으면 생성 (dao에서 처리)
		
		WebSocketRequestVO request = message.getPayload(); // 메세지 추출 (본문)
		
		// 메세지 발송
		WebSocketResponseVO response = new WebSocketResponseVO();
		response.setSenderMemberId(claimVO.getMemberId());
		response.setSenderMemberLevel(claimVO.getMemberLevel());
		response.setTime(LocalDateTime.now());
		response.setContent(request.getContent());
		messagingTemplate.convertAndSend("/private/chat/"+roomNo, response); // @SendTo("/private/chat/{roomNo}")
		
		// DB에 저장
		int roomMessageNo = roomMessageDao.sequence();
		RoomMessageDto roomMessageDto = new RoomMessageDto();
		roomMessageDto.setRoomMessageNo(roomMessageNo);
		roomMessageDto.setRoomMessageType("chat");
		roomMessageDto.setRoomMessageSender(claimVO.getMemberId());
		roomMessageDto.setRoomMessageReceiver(null);
		roomMessageDto.setRoomMessageContent(request.getContent());
		roomMessageDto.setRoomMessageTime(Timestamp.valueOf(response.getTime()));
		roomMessageDto.setRoomNo(roomNo); // 전체 채팅과 다른 점
		roomMessageDao.insert(roomMessageDto);
	}
	
}
