package com.kh.fa.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.fa.dao.RoomDao;
import com.kh.fa.dto.RoomDto;
import com.kh.fa.dto.RoomMemberDto;
import com.kh.fa.error.TargetNotFoundException;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberClaimVO;
import com.kh.fa.vo.RoomVO;

@CrossOrigin
@RestController
@RequestMapping("/room")
public class RoomRestController {
	
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private TokenService tokenService;
	
	@PostMapping("/")
	public RoomDto insert(@RequestBody RoomDto roomDto){
		int roomNo = roomDao.sequence();
		roomDto.setRoomNo(roomNo);
		roomDao.insert(roomDto);
		// return roomDto; // 시간이 안들어있음 >> DB에서 만든 정보가 들어있지 않음
		return roomDao.selectOne(roomNo); // DB에서 만든 정보까지 포함해서 반환
	}
	
	@GetMapping("/")
	public List<RoomVO> list(@RequestHeader("Authorization") String token) {
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		return roomDao.selectList(claimVO.getMemberId());
	}
	
	@DeleteMapping("/{roomNo}")
	public void delete(@PathVariable int roomNo) {
		roomDao.delete(roomNo);
	}
	
	@PostMapping("/enter")
	public void enter(@RequestHeader("Authorization") String token, @RequestBody RoomMemberDto roomMemberDto) {
		// 방이 없는 경우를 사전 차단
		RoomDto roomDto = roomDao.selectOne(roomMemberDto.getRoomNo());
		if(roomDto == null) throw new TargetNotFoundException("존재하지 않는 방");
		
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		roomMemberDto.setMemberId(claimVO.getMemberId()); // 아이디 설정
		roomDao.enter(roomMemberDto); // 등록
	}
	
	@PostMapping("/leave")
	public void leave(@RequestHeader("Authorization") String token, @RequestBody RoomMemberDto roomMemberDto) {
		// 방이 없는 경우를 사전 차단
		RoomDto roomDto = roomDao.selectOne(roomMemberDto.getRoomNo());
		if(roomDto == null) throw new TargetNotFoundException("존재하지 않는 방");
		
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		roomMemberDto.setMemberId(claimVO.getMemberId()); // 아이디 설정
		roomDao.leave(roomMemberDto); // 삭제
	}
	
	@GetMapping("/check/{roomNo}")
	public boolean check(@RequestHeader("Authorization") String token, @PathVariable int roomNo) {
		// 토큰 해석
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));

		// DB 검사
		RoomMemberDto roomMemberDto = new RoomMemberDto();
		roomMemberDto.setMemberId(claimVO.getMemberId());
		roomMemberDto.setRoomNo(roomNo);
		boolean canEnter = roomDao.check(roomMemberDto);
		
		return canEnter;
	}
	
}
