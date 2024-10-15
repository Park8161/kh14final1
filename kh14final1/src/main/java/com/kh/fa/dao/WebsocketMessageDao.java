package com.kh.fa.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.WebsocketMessageDto;

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
	
	public List<WebsocketMessageDto> selectList(int beginRow, int endRow){
		Map<String, Object> map = Map.of("beginRow", beginRow, "endRow", endRow);
		return sqlSession.selectList("websocketMessage.list", map);
	}
	
}
