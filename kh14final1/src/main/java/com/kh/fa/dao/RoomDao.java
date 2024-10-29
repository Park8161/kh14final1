package com.kh.fa.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.ProductDto;
import com.kh.fa.dto.RoomDto;
import com.kh.fa.dto.RoomMemberDto;
import com.kh.fa.vo.RoomListVO;

@Repository
public class RoomDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private UnreadDao unreadDao;
	
	
	public int sequence() {
		return sqlSession.selectOne("room.sequence");
	}
	
	public void insert(RoomDto roomDto) {		
		sqlSession.insert("room.insert", roomDto);
	}
	
	// 특정 회원이 참가 중인 방 목록 조회
	public List<RoomListVO> selectList(String memberId){
//		아이디를 통해서 참여중인 방 번호의 목록 추출
		List<Integer> roomNoList = sqlSession.selectList("room.noList", memberId);
		List<RoomListVO> list = new ArrayList<>();
//		RoomListVO 에서 상품 정보, 마지막 메시지, 방 멤버 추출
		for (int roomNo : roomNoList) {
		    RoomMemberDto dto = new RoomMemberDto();
		    dto.setMemberId(memberId);
		    dto.setRoomNo(roomNo);
		    boolean check = checkRemainMember(dto);
		    if(check) {
//		    	나 이외의 참여자가 있다면 
		    	RoomListVO vo = sqlSession.selectOne("room.selectRoomListVO2", dto);   
		    	int unreadCnt = unreadDao.count(dto);
		    	vo.setUnreadCnt(unreadCnt);
		    	if (vo != null) list.add(vo);
		    }
		    else {
//		    	없다면
//		    	vo null exeption 발생 상대방이 나갔을 때 
		    	RoomListVO vo = new RoomListVO();
		    	vo.setMemberId("상대방이 퇴장했습니다");
		    	vo = sqlSession.selectOne("room.selectRoomListVO", dto);	
		    	if (vo != null) list.add(vo);
		    }
		}

		return list;
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
	
//	채팅방 퇴장 
	public void leave(RoomMemberDto roomMemberDto) {
		sqlSession.delete("roomMember.leave", roomMemberDto);
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
	
	public boolean checkRemainMember(RoomMemberDto roomMemberDto) {
//		채팅방 멤버 중 나를 제외한 참가자의 수
		int result =  sqlSession.selectOne("roomMember.checkRemainMember", roomMemberDto);
		return result > 0;
	}
	
	public String selectAnother(RoomMemberDto roomMemberDto) {
		return sqlSession.selectOne("roomMember.selectAnother", roomMemberDto);
	}
	
	// 채팅방 테이블과 첨부테이블을 연결
	public void connect(int roomNo, int attachmentNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("roomNo", roomNo);
		params.put("attachmentNo", attachmentNo);
		sqlSession.insert("room.connect", params);
	}
	
	// 채팅방 이미지 찾기
	public Integer findImage(int roomNo) {
		return sqlSession.selectOne("room.findImage", roomNo);
	}
	
	// 채팅방 이미지 찾기 - 여러 이미지를 가져오기
	public List<Integer> findImages(int roomNo) {
		return sqlSession.selectList("room.findImages", roomNo);
	}
	
}