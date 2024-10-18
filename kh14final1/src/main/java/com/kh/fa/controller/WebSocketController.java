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

import com.kh.fa.dao.WebsocketMessageDao;
import com.kh.fa.dto.WebsocketMessageDto;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberClaimVO;
import com.kh.fa.vo.WebSocketDMResponseVO;
import com.kh.fa.vo.WebSocketRequestVO;
import com.kh.fa.vo.WebSocketResponseVO;

import lombok.extern.slf4j.Slf4j;

/**
	웹소켓 요청을 처리하기 위한 컨트롤러
	- SockJS와 STOMP를 사용하면 웹소켓을 HTTP처럼 관리할 수 있다
	- @Controller 를 이용하여 메세지를 매핑하고 채널에 전송할 수 있다
	- 이 컨트롤러에서는 stomp client가 publish한 정보만 받을 수 있다
 */
@Slf4j
@Controller
public class WebSocketController {
	
//	[1] /app/chat 이라는 채널로 메세지가 들어오면 /public/chat 으로 전송한다
//	@MessageMapping("/chat")//사용자가 /app/chat으로 메세지를 보내면~
//	@SendTo("/public/chat")//그 메세지를 /public/chat으로 보내세요!
//	public WebSocketResponseVO chat(WebSocketRequestVO request) {
//		WebSocketResponseVO response = new WebSocketResponseVO();
//		response.setContent(request.getContent());//사용자가 보낸 내용 그대로
//		response.setTime(LocalDateTime.now());//시간은 현재 시각으로 추가
//		return response;
//	}
	
//	전송 도구 생성
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
//	토큰 해석 도구
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private WebsocketMessageDao websocketMessageDao;
	
//	사용자가 보내는 내용뿐 아니라 헤더 등 모든 정보를 얻고 싶다면 수신 형태를 바꿔야 한다
//	Message<수신데이터자료형>
//	헤더와 바디를 각각 읽을 수 있게 되어서 여러 가지 추가 작업이 가능하다
	@MessageMapping("/chat")//사용자가 /app/chat으로 메세지를 보내면~
	public void chat(Message<WebSocketRequestVO> message) {
		//헤더 추출
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);//도구 생성
		String accessToken = accessor.getFirstNativeHeader("accessToken");
		//String refreshToken = accessor.getFirstNativeHeader("refreshToken");
//		if(accessToken == null) {//비회원이 채팅을 보냈으면
//			return;//그만둬!
//		}
		
		//토큰 해석
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(accessToken));
		
		//본문 추출
		WebSocketRequestVO request = message.getPayload();//메세지 수신
		
		WebSocketResponseVO response = new WebSocketResponseVO();
		response.setContent(request.getContent());//사용자가 보낸 내용 그대로
		response.setTime(LocalDateTime.now());//시간은 현재 시각으로 추가
		response.setSenderMemberId(claimVO.getMemberId());//발신자 회원아이디
		response.setSenderMemberLevel(claimVO.getMemberLevel());//발신자의 회원등급
		
		//수동으로 직접 전송
		//messagingTemplate.convertAndSend("채널", 데이터);
		messagingTemplate.convertAndSend("/public/chat", response);
		
		//DB에 등록
		int websocketMessageNo = websocketMessageDao.sequence();
		WebsocketMessageDto websocketMessageDto = new WebsocketMessageDto();
		websocketMessageDto.setWebsocketMessageType("chat");
		websocketMessageDto.setWebsocketMessageNo(websocketMessageNo);
		websocketMessageDto.setWebsocketMessageSender(claimVO.getMemberId());
		websocketMessageDto.setWebsocketMessageReceiver(null);//전체채팅이므로 수신자 없음
		websocketMessageDto.setWebsocketMessageContent(request.getContent());
		websocketMessageDto.setWebsocketMessageTime(Timestamp.valueOf(response.getTime()));//시간 동기화
		websocketMessageDao.insert(websocketMessageDto);
	}
	
//	DM과 관련된 처리
	@MessageMapping("/dm/{receiverId}")
//	@SendTo("/public/dm/{receiverId}")
	public void dm(@DestinationVariable String receiverId,
								Message<WebSocketRequestVO> message) {
		log.info("[DM] receiverId = {}", receiverId);
		//헤더 추출
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);//도구 생성
		String accessToken = accessor.getFirstNativeHeader("accessToken");
		//String refreshToken = accessor.getFirstNativeHeader("refreshToken");
//		if(accessToken == null) {//비회원이 채팅을 보냈으면
//			return;//그만둬!
//		}
		
		//토큰 해석
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(accessToken));
		
		//(+추가) 자신에게 보내는 메세지면 차단(허용도 가능)
		if(claimVO.getMemberId().equals(receiverId)) {
			return;
		}
		
		//본문 추출
		WebSocketRequestVO request = message.getPayload();//메세지 수신
		
		WebSocketDMResponseVO response = new WebSocketDMResponseVO();
		response.setContent(request.getContent());//사용자가 보낸 내용 그대로
		response.setTime(LocalDateTime.now());//시간은 현재 시각으로 추가
		response.setSenderMemberId(claimVO.getMemberId());//발신자 회원아이디
		response.setSenderMemberLevel(claimVO.getMemberLevel());//발신자의 회원등급
		response.setReceiverMemberId(receiverId);//수신자 회원아이디
		
		messagingTemplate.convertAndSend("/public/dm/"+response.getSenderMemberId(), response);//발신자
		messagingTemplate.convertAndSend("/public/dm/"+response.getReceiverMemberId(), response);//수신자
		
		//DB에 등록
		int websocketMessageNo = websocketMessageDao.sequence();
		WebsocketMessageDto websocketMessageDto = new WebsocketMessageDto();
		websocketMessageDto.setWebsocketMessageNo(websocketMessageNo);
		websocketMessageDto.setWebsocketMessageType("dm");
		websocketMessageDto.setWebsocketMessageSender(response.getSenderMemberId());
		websocketMessageDto.setWebsocketMessageReceiver(response.getReceiverMemberId());
		websocketMessageDto.setWebsocketMessageContent(response.getContent());
		websocketMessageDto.setWebsocketMessageTime(Timestamp.valueOf(response.getTime()));
		websocketMessageDao.insert(websocketMessageDto);
	}
	
}