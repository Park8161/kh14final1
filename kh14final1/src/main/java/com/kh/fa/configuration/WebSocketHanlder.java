package com.kh.fa.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.kh.fa.error.TargetNotFoundException;
import com.kh.fa.service.FileUploadService;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberClaimVO;

//파일 핸들러, STOMP로 전송
@Service
public class WebSocketHanlder extends TextWebSocketHandler{//텍스트 메세지 처리 핸들러
	
//	List<HashMap<String, Object>> sessions = new ArrayList<>(); //세션에 저장(특정방에 연결된 사용자들)
//	static int roomIndex = -1;
//	
//	//파일 저장경로
//	@Autowired
//	private CustomFileuploadProperties properties;
//	//토큰 서비스
//	@Autowired
//	private TokenService tokenService;
//	//파일 저장 서비스
//	@Autowired
//	private FileUploadService fileUploadService;
//	//채팅방 이미지 저장소
////	private static final String FILE_UPLOAD_PATH = "D:/upload/chatRoom/";
//	
//	@Override
//	public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
//		// 바이너리 메시지 발송
//	    ByteBuffer byteBuffer = message.getPayload(); // 메시지 데이터를 가져와서 바이너리 데이터로 가공
//	    String fileName = "file.jpg";
//	    
////	    File dir = new File(FILE_UPLOAD_PATH, fileName);
////	    if(!dir.exists()) {
////			dir.mkdirs();
////		}
//
//	    // 삭제할 파일을 찾아준다
//	    File oldFile = new File(properties.getPath() + fileName);
//	    try {
//	        FileOutputStream fileOutputStream = new FileOutputStream(oldFile); // 출력 열기
//	        fileOutputStream.close(); // 파일 삭제 시 FileOutputStream을 닫기
//	        oldFile.delete(); // 삭제
//	    } catch (Exception e) {
//	        throw new TargetNotFoundException("파일이 열려있거나 파일이 존재하지 않습니다");
//	    }
//
//	    File file = new File(properties.getPath(), fileName); // 파일을 새로 생성
//
//	    // 출력 = 쓰기 = 저장
//	    FileOutputStream fileOutputStream = null;
//	    FileChannel fileChannel = null; // 대량 데이터 전송
//	    try {
//	        byteBuffer.flip(); // byteBuffer를 읽기 위한 세팅(쓰기 -> 읽기)
//	        fileOutputStream = new FileOutputStream(file, true); // 생성을 위해 OutputStream을 연다.
//	        fileChannel = fileOutputStream.getChannel(); // 채널을 열고
//	        fileChannel.write(byteBuffer); // 파일을 쓰기
//	        byteBuffer.compact(); // 쓴 후 버퍼 준비 새로운 데이터 받을
//	    } catch (Exception e) {
//	        throw new TargetNotFoundException("예상치 못한 오류");
//	    } finally {
//	        try {
//	            if (fileOutputStream != null) {
//	                fileOutputStream.close();
//	            }
//	            if (fileChannel != null) {
//	                fileChannel.close();
//	            }
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	    }
//
//	    // 업로드한 파일을 로컬 저장소에 저장
//	    String imageurl = ""; // 여기에 파일의 경로를 설정해야 함
//	    // File(업로드한 파일)을 MultiPartFile로 변환하는 과정 우선 file을 DiskFileItem에 넣어준다
//	    try { // 업로드한 파일의 MIME 타입을 확인하고 디스크에 임시로 저장됨
//	        FileItem fileItem = new DiskFileItem("marinFile", Files.probeContentType(file.toPath()),
//	                false, file.getName(), (int) file.length(), file.getParentFile());
//	        try (InputStream input = new FileInputStream(file); // 파일에서 바이트 파일로 읽을 수 있게 함
//	             OutputStream os = fileItem.getOutputStream()) { // 출력 스트림 생성
//	            IOUtils.copy(input, os); // 입력 스트림을 출력 스트림으로 복사
//	        } catch (IOException e) {
//	            throw new TargetNotFoundException("IO 실패");
//	        }
//
//	        //file을 multipartFile로 변환
//	        fileUploadService.uploadFile(fileItem);
//
//	        byteBuffer.position(0); // 메시지 데이터가 저장된 버퍼를 다시 시작 위치로 돌림
//
//	        // 토큰 검사 및 해석 
////	        String accessToken = (String) session.getAttributes().get("accessToken");
////	        if(accessToken == null) return;
//	        
////	        MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(accessToken));
//	        
//	        // 메시지 발송
//	        HashMap<String, Object> sessionMap = sessions.get(roomIndex); // 해당 방의 정보를 가져온다
//	        for (String sessionMapKey : sessionMap.keySet()) {
//	            if (sessionMapKey.equals("room	No")) {
//	                continue; //방번호인 경우 건너뜀
//	            }
//	            //현재 반복 중인 세션ID에 해당하는 웹소켓 세션을 가져온다
//	            WebSocketSession webSocketSession = (WebSocketSession) sessionMap.get(sessionMapKey);
//	            try {
//	                JSONObject obj = new JSONObject(); //JSON 객체 생성
//	                obj.put("type", "imgurl");
//	                obj.put("sessionId", session.getId()); // 토큰 검사로 아이디를 갖고온다
//	                obj.put("imageurl", imageurl); // 여기에서 imageurl에 값을 넣어줘야 함
//	                webSocketSession.sendMessage(new TextMessage(obj.toString())); // 초기화 버퍼 전송
//	            } catch (IOException e) {
//	                e.printStackTrace();
//	            }
//	        }
//	    } catch (Exception e) {
//	        e.printStackTrace(); // 다른 예외 처리
//	    }
//	}
//	
//	//웹소캣 연결 후
//	//-세션ID를 포함해 메세지를 전송함
//	//-클라이언트 const socket = new SockJS 여기서 설정한 session 값을 갖고옴
//	//-stomp로 되어있는 방에 세션을 연결해서 파일을 주고받는 구조 
//	 @Override
//	 public void afterConnectionEstablished(WebSocketSession session) throws Exception{
//		 //소캣연결
//		 boolean sessionExist = false;
//		 
//		 String sessionUrl = session.getUri().toString(); 
//		
//		 String roomNo = sessionUrl.split("/room/")[1];//url 자르는데 /이거 기준으로 배열 0,1
//		 int roomIndex = -1; //유효하지않는 방번호
//		if(sessions.size() > 0) { //사용자가 있어서 세션이 있으면 세션을 생성하지않고 사용자가 없으면 세션을 새로 생성한다
//			for(int i=0; i<sessions.size(); i++) {
//				String tempRoomNo = (String) sessions.get(i).get("roomNo");
//				if(roomNo.equals(tempRoomNo)) {
//					sessionExist = true;
//					roomIndex = i;
//					break;
//				}
//			}		
//		}
//		if(sessionExist) {//만약 사용자가 있어서 세션이 존재한다면
//			HashMap<String, Object> sessionMap =  sessions.get(roomIndex);
//			sessionMap.put(session.getId(), session); //핸들바이너리 메세지에서 저장한 아이디 넣음
//		}
//		else {
//			HashMap<String, Object> sessionMap = new HashMap<String, Object>();
//			sessionMap.put("roomNo", roomNo);
//			sessionMap.put(session.getId(), session);
//			sessions.add(sessionMap);
//		}
//		 
//		//세션등록이 끝나면 발급받은 세션id값의 메세지를 발송
//		JSONObject obj = new JSONObject();
//		obj.put("type", "getId");
//		obj.put("sessionId", session.getId());
//		session.sendMessage(new TextMessage(obj.toString()));
//		
//		super.afterConnectionEstablished(session);//TextWebSocketHandler 호출, 세션 초기화 작업등을 해줌
//	}
//	 
//	 @Override
//	 public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//	  //소켓 종료
//	if(sessions.size() > 0) {//소캣 종료되면 해당 세션 값들을 찾아서 지운다
//		for(HashMap<String, Object> stringObjectHashMap : sessions) {
//			stringObjectHashMap.remove(session.getId());
//			}
//		}
//		super.afterConnectionClosed(session, status); //status는 웹소캣 종료될때의 상태정보
//	}
	
}

