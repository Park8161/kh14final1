package com.kh.fa.configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.kh.fa.dao.RoomDao;
import com.kh.fa.dao.RoomMessageDao;
import com.kh.fa.dao.UnreadDao;
import com.kh.fa.dao.WebsocketMessageDao;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberClaimVO;
import com.kh.fa.vo.WebSocketMessageMoreVO;
import com.kh.fa.vo.WebsocketMessageVO;

import lombok.extern.slf4j.Slf4j;

// 웹소켓에 대한 사용자의 접속 및 종료를 감지하는 이벤트 처리도구
@Slf4j
@Service
public class WebSocketEventHandler {
	
	@Autowired
	private TokenService tokenService;
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private WebsocketMessageDao websocketMessageDao;
	@Autowired
	private RoomMessageDao roomMessageDao;
	@Autowired 
	private RoomDao roomDao;
	@Autowired
	private UnreadDao unreadDao;
	
	// 사용자 세션 정보 저장소 - HashMap은 non thread-safe 여서 사용 불가 >> thread-safe는 ConcurrentHashMap을 사용
	// private Map<String, String> userList = new ConcurrentHashMap<>();
	private Map<String, String> userList = Collections.synchronizedMap(new HashMap<>()); // 이것도 가능
	
	@EventListener // implements ApplicationListener<SessionConnectEvent> 효과
	public void whenUserEnter(SessionConnectEvent event) {
		// 헤더 정보를 추출
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = accessor.getSessionId(); // 웹소켓 세션에 들어있는 아이디 추출
		
		String accessToken = accessor.getFirstNativeHeader("accessToken");
		if(accessToken == null) return;
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(accessToken));		
		
		// 저장소에 사용자 등록
		userList.put(sessionId, claimVO.getMemberId());
		
//		log.info("사용자 접속 완료, 인원수 = {}, 세션 = {}, 아이디 = {}", userList.size(), sessionId, claimVO.getMemberId());
	}
	
	@EventListener // 구독 이벤트
	public void whenUserSubscribe(SessionSubscribeEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		if(accessor.getDestination() == null) return;
		
		if("/public/users".equals(accessor.getDestination())) {
			// 채널 /users에 전파
			Set<String> values = new TreeSet<>(userList.values());
			messagingTemplate.convertAndSend("/public/users", values);
		}
		else if(accessor.getDestination().equals("/public/db")) { // 비회원
//			System.out.println("비회원 DB검색 시작");
			List<WebsocketMessageVO> messageList = websocketMessageDao.selectListMemberComplete(null, 1, 100);
//			System.out.println(messageList);
			if(messageList.isEmpty()) return;
			
			List<WebsocketMessageVO> prevMessageList = websocketMessageDao.selectListMemberComplete(null, 1, messageList.get(0).getNo());
			
			WebSocketMessageMoreVO moreVO = new WebSocketMessageMoreVO();
			moreVO.setMessageList(messageList);
			moreVO.setLast(prevMessageList.isEmpty());
			
			messagingTemplate.convertAndSend("/public/db", moreVO);
			
		}
		else if(accessor.getDestination().startsWith("/public/db")) { // 회원
			// 채널명이 /public/db/xxx일 것이므로 xxx을 원한다면 /public/db/를 제거
			String memberId = accessor.getDestination().substring("/public/db/".length());
			
			List<WebsocketMessageVO> messageList = websocketMessageDao.selectListMemberComplete(memberId, 1, 100);
			if(messageList.isEmpty()) return;
			
			List<WebsocketMessageVO> prevMessageList = websocketMessageDao.selectListMemberComplete(memberId, 1, messageList.get(0).getNo());
			
			WebSocketMessageMoreVO moreVO = new WebSocketMessageMoreVO();
			moreVO.setMessageList(messageList);
			moreVO.setLast(prevMessageList.isEmpty());
			
			messagingTemplate.convertAndSend("/public/db/"+memberId, moreVO);
		}
		else if(accessor.getDestination().startsWith("/private/db")) {
			// 주소의 형태는 /private/db/방번호/아이디
			String removeStr = accessor.getDestination().substring("/private/db/".length());
			int slash = removeStr.indexOf("/");
			int roomNo = Integer.parseInt(removeStr.substring(0, slash)); // 슬래시 앞부분
			String memberId = removeStr.substring(slash+1); // 슬래시 뒷부분
//			roomNo와 memberId를 통해서 안읽은 메시지(unread)를 0으로 업데이트
//			unreadDao.setZero(memberId, roomNo);
			List<WebsocketMessageVO> messageList = roomMessageDao.selectList(memberId, roomNo);
//			WebSocketMessageMoreVO moreVO = new WebSocketMessageMoreVO();
//			moreVO.setMessageList(messageList);
//			moreVO.setLast(true);
//			if(messageList.size() > 0) { // 메세지가 존재한다면
//				List<WebsocketMessageVO> prevMessageList 
//				= roomMessageDao.selectListMemberComplete(memberId, 1, 100, messageList.get(0).getNo(), roomNo);
//				moreVO.setLast(prevMessageList.isEmpty());
//			}
			// 전송
			messagingTemplate.convertAndSend("/private/db/"+roomNo+"/"+memberId, messageList);
		}
		
	}
	
	
	@EventListener // implements ApplicationListener<SessionDisconnectEvent> 효과
	public void whenUserLeave(SessionDisconnectEvent event) {
		// 헤더 정보를 추출
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = accessor.getSessionId(); // 웹소켓 세션에 들어있는 아이디 추출
		
		// 저장소에서 사용자 제거
		userList.remove(sessionId);
		
//		log.info("사용자 접속 종료, 인원수 = {}, 세션 = {}", userList.size(), sessionId);
		
		// 채널 /users에 전파
		Set<String> values = new TreeSet<>(userList.values());
		messagingTemplate.convertAndSend("/public/users", values);
	}
}
