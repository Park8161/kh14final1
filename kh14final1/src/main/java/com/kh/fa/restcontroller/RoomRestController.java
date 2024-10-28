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

import com.kh.fa.dao.ProductDao;
import com.kh.fa.dao.RoomDao;
import com.kh.fa.dto.ProductDto;
import com.kh.fa.dto.RoomDto;
import com.kh.fa.dto.RoomMemberDto;
import com.kh.fa.error.TargetNotFoundException;
import com.kh.fa.service.TokenService;
import com.kh.fa.vo.MemberClaimVO;
import com.kh.fa.vo.RoomListVO;

@CrossOrigin
@RestController
@RequestMapping("/room")
public class RoomRestController {
	
	@Autowired
	private RoomDao roomDao;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired 
	private ProductDao productDao;
	
	
//	채팅방 생성 or 입장 
	@PostMapping("/{productNo}")
	public int insert(@PathVariable int productNo,
			@RequestHeader("Authorization") String token
			){
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		RoomMemberDto roomBuyerDto = new RoomMemberDto();
		ProductDto productDto = productDao.selectOne(productNo);
		
//		판매자 아이디 추출
		String sellerId = productDto.getProductMember();
		
		if(sellerId == null) throw new TargetNotFoundException("판매자가 존재하지 않습니다.");
		
//		확인을 위해 필요한 데이터: 접속자(구매자) id, 상품 번호
		roomBuyerDto.setMemberId(claimVO.getMemberId()); // 아이디 설정
		roomBuyerDto.setProductNo(productNo);
		
//		해당 상품에 대한 채팅 기록이 존재하는지 확인 
		boolean isRoomExist = roomDao.isRoomExist(roomBuyerDto);		
		if(!isRoomExist) { //방이 존재하지 않을 경우 방 생성
			int roomNo = roomDao.sequence();
			RoomDto roomDto = new RoomDto();
			RoomMemberDto roomSellerDto = new RoomMemberDto();
			String roomName = sellerId +", "+ claimVO.getMemberId();
			
			roomDto.setRoomNo(roomNo);
			roomDto.setRoomName(roomName); //방이름은 판매자 아이디로 자동 설정
			
			roomBuyerDto.setRoomNo(roomNo);
			
			roomSellerDto.setRoomNo(roomNo);
			roomSellerDto.setMemberId(sellerId);
			roomSellerDto.setProductNo(productNo);
			
			roomDao.insert(roomDto);
			roomDao.enter(roomBuyerDto);
			roomDao.enter(roomSellerDto);
			
			return roomNo;
			
		}
		 //방이 존재할 경우 방에 입장
		return roomDao.findRoomNo(roomBuyerDto);
		
	}
	
	@GetMapping("/")
	public List<RoomListVO> list(
			@RequestHeader("Authorization") String token
			) {
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		return roomDao.selectList(claimVO.getMemberId());
	}
	
	@DeleteMapping("/{roomNo}")
	public void delete(@PathVariable int roomNo) {
		roomDao.delete(roomNo);
	}
	
	@PostMapping("/leave")
	public void leave(@RequestHeader("Authorization") String token, @RequestBody RoomMemberDto roomMemberDto) {
		// 방이 없는 경우를 사전 차단
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		roomMemberDto.setMemberId(claimVO.getMemberId());
		
		RoomDto roomDto = roomDao.selectOne(roomMemberDto.getRoomNo());
		if(roomDto == null) throw new TargetNotFoundException("존재하지 않는 방");
		
//		방에 남은 회원이 없다면 방을 삭제 
		if(!roomDao.checkRemainMember(roomMemberDto)) {
			roomDao.delete(roomMemberDto.getRoomNo());
		}
		else
		roomDao.leave(roomMemberDto); // 퇴장
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
	
	@GetMapping("/productInfo/{roomNo}")
	public ProductDto getProductInfo(@PathVariable int roomNo) {
//		roomNo -> productNo -> productInfo
		ProductDto productDto = roomDao.getProductInfo(roomNo);
		return productDto;
	}
	
}