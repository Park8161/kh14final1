package com.kh.fa.dao;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.RoomMessageDto;
import com.kh.fa.vo.WebsocketMessageVO;

@Repository
public class RoomMessageDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public int sequence() {
		return sqlSession.selectOne("roomMessage.sequence");
	}
	
	public void insert(RoomMessageDto roomMessageDto) {
		sqlSession.insert("roomMessage.add", roomMessageDto);
	}
	
	// 최초 입장 시 전달할 메세지 조회
	public List<WebsocketMessageVO> selectList(String memberId, int roomNo){
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("roomNo", roomNo);
		return sqlSession.selectList("roomMessage.list", params);
	}
	
	// 최초 입장 시 전달할 메세지 조회
	public List<WebsocketMessageVO> selectListMemberComplete(String memberId, int beginRow, int endRow, int roomNo){
		// Map<String, Object> params = Map.of("memberId", memberId, "beginRow", beginRow, "endRow", endRow);
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("beginRow", beginRow);
		params.put("endRow", endRow);
		params.put("roomNo", roomNo);
		return sqlSession.selectList("roomMessage.listMemberComplete", params);
	}
	
	// 더보기 누를 경우 전달할 메세지 조회
	public List<WebsocketMessageVO> selectListMemberComplete(String memberId, int beginRow, int endRow, int firstMessageNo, int roomNo){
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("beginRow", beginRow);
		params.put("endRow", endRow);
		params.put("firstMessageNo", firstMessageNo);
		params.put("roomNo", roomNo);
		return sqlSession.selectList("roomMessage.listMemberComplete", params);
	}
	
	public void setIsRead(int roomNo, String memberId) {
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("roomNo", roomNo);
		sqlSession.update("roomMessage.setIsRead", params);
//		System.out.println("세션 다녀왔니/roomMessageDao");
	}
	
	//발신자 가져오는 메소드
    public String findSender(int messageNo) {
        return sqlSession.selectOne("roomMessage.findSender", messageNo);
    }
    //시간 가져오는 메소드
    public Timestamp findTime(int messageNo) {
        return sqlSession.selectOne("roomMessage.findTime", messageNo);
    }
    //파일 등록하는 메소드
    public void addFile(RoomMessageDto roomMessageDto) {
        sqlSession.insert("roomMessage.insertFile", roomMessageDto);
    }
    //메세지 번호 가져오는 메소드
    public List<Integer> findRoomMessageNo (int roomNo) {
//        System.out.println("방번호 "+roomNo);
        return sqlSession.selectList("roomMessage.findRoomMessageNo", roomNo);
    }
}
