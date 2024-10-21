package com.kh.fa.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.ProductDto;
import com.kh.fa.dto.RoomDto;
import com.kh.fa.dto.RoomMemberDto;
import com.kh.fa.vo.RoomVO;

@Repository
public class RoomDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	@Autowired
	private ProductDao productDao;
	
	public int sequence() {
		return sqlSession.selectOne("room.sequence");
	}
	
	public void insert(RoomDto roomDto) {		
		sqlSession.insert("room.insert", roomDto);
	}
	
	// 전체 방 목록 조회
	public List<RoomDto> selectList(){
		return sqlSession.selectList("room.list");
	}
	
	// 특정 회원이 참가 중인 방 목록 조회
	public List<RoomVO> selectList(String memberId){
		return sqlSession.selectList("room.listByMember", memberId);
	}
	
	public RoomDto selectOne(int roomNo) {
		return sqlSession.selectOne("room.find", roomNo);
	}
	
	public boolean update(RoomDto roomDto) {
		return sqlSession.update("room.edit", roomDto) > 0;
	}
	
	public boolean delete(int roomNo) {
		return sqlSession.delete("room.delete", roomNo) > 0;
	}
	
	// 채팅방 입장
	public void enter(RoomMemberDto roomMemberDto) {
		sqlSession.insert("roomMember.enter", roomMemberDto);
	}
	
	// 채팅방 퇴장
	public boolean leave(RoomMemberDto roomMemberDto) {
		return sqlSession.delete("roomMember.leave", roomMemberDto) > 0;
	}
	
	// 채팅방 자격 검사
	public boolean check(RoomMemberDto roomMemberDto) {
		int result = sqlSession.selectOne("roomMember.check", roomMemberDto);
		return result > 0; // myBatis가 자동으로 자료형을 파악하는데 가끔 못찾는 경우 콕 집어서 설명해주어야 함
	}
	
//	해당 상품과의 채팅 기록이 있는지 (방이 존재하는지 검사)
	public boolean isRoomExist(RoomMemberDto roomMemberDto) {
		int result = sqlSession.selectOne("roomMember.isRoomExist", roomMemberDto);
		return result > 0;
	}
	
	public int findRoomNo(RoomMemberDto roomMemberDto){
		int result = sqlSession.selectOne("roomMember.findRoomNo", roomMemberDto);
		return result;
	}

	public ProductDto getProductInfo(int roomNo) {
		int productNo = sqlSession.selectOne("roomMember.findProductNo", roomNo);
		return productDao.selectOne(productNo);
	}
	
	
}
