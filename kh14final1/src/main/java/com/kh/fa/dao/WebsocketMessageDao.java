package com.kh.fa.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.WebsocketMessageDto;
import com.kh.fa.vo.WebsocketMessageVO;

@Repository
public class WebsocketMessageDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public int sequence() {
		return sqlSession.selectOne("websocketMessage.sequence");
	}
	
	public void insert(WebsocketMessageDto webSocketMessageDto) {
		sqlSession.insert("websocketMessage.add", webSocketMessageDto);
	}
	
	// 비회원용(dm제외)
	public List<WebsocketMessageDto> selectList(int beginRow, int endRow){
		Map<String, Object> params = Map.of("beginRow", beginRow, "endRow", endRow);
		return sqlSession.selectList("websocketMessage.list", params);
	}
	
	// 회원용(dm포함)
	public List<WebsocketMessageDto> selectListMember(String memberId, int beginRow, int endRow){
//		Map<String, Object> params = Map.of("memberId", memberId, "beginRow", beginRow, "endRow", endRow);
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("beginRow", beginRow);
		params.put("endRow", endRow);
		return sqlSession.selectList("websocketMessage.listMember", params);
	}
	
	// VO의 형태 통일 후 목록 조회 >> stream을 피하기 위한 밑작업
	public List<WebsocketMessageVO> selectListMemberComplete(String memberId, int beginRow, int endRow){
//		Map<String, Object> params = Map.of("memberId", memberId, "beginRow", beginRow, "endRow", endRow);
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("beginRow", beginRow);
		params.put("endRow", endRow);
		return sqlSession.selectList("websocketMessage.listMemberComplete", params);
	}
	
	// 더보기를 위한 밑작업(오버로딩)
	public List<WebsocketMessageVO> selectListMemberComplete(String memberId, int beginRow, int endRow, int firstMessageNo){
//		Map<String, Object> params = Map.of("memberId", memberId, "beginRow", beginRow, "endRow", endRow, "firstMessageNo", firstMessageNo);
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("beginRow", beginRow);
		params.put("endRow", endRow);
		params.put("firstMessageNo", firstMessageNo);
		return sqlSession.selectList("websocketMessage.listMemberComplete", params);
	}
	
	
}
