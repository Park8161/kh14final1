package com.kh.fa.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.fa.dao.BlockDao;
import com.kh.fa.dao.MemberDao;
import com.kh.fa.dto.BanDto;
import com.kh.fa.dto.MemberDto;
import com.kh.fa.error.TargetNotFoundException;
import com.kh.fa.service.TokenService;

import io.swagger.v3.oas.annotations.Parameter;


@RestController
@RequestMapping("/admin/member")
@CrossOrigin
public class AdminMemberRestController {
	
	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private TokenService tokenService;	
	
	@Autowired
	private BlockDao blockDao;
	
	//회원 목록 출력
		@GetMapping("/")
		public List<MemberDto> list(){
			return memberDao.selectList();
		}
	
	//검색 기능
		@GetMapping("/column/{column}/keyword/{keyword}")
		public List<MemberDto> search(
							@PathVariable String column,
							@PathVariable String keyword){
			List<MemberDto> list = memberDao.selectList(column, keyword);
			return list;
		}
		
	//회원 상세 조회
		@GetMapping("/detail/{memberId}")
		public MemberDto detail(
					@Parameter(required = true, description = "회원아이디")
					@PathVariable String memberId) {
			MemberDto memberDto = memberDao.selectOne(memberId);
			if(memberDto == null)
				throw new TargetNotFoundException();
			return memberDto;
		}
	
	//회원 수정
		@PutMapping("/edit")
		public void update(@RequestBody MemberDto memberDto) {
	// 수정 후 적용여부를 이용하여 404 처리
			boolean result = memberDao.updateMemberByAdmin(memberDto);
			if(result == false) {
				throw new TargetNotFoundException();
			}
		}
		
	//회원 삭제
		@DeleteMapping("/{memberId}")
		public void delete(	@PathVariable String memberId) {
			boolean result = memberDao.delete(memberId);
			if(result == false) {
				throw new TargetNotFoundException();
			}
		}
		
	//회원 차단 등록
	@PostMapping("/bann")
	public ResponseEntity<String> banMember(@RequestBody BanDto banDto) {
		  memberDao.banMember(banDto);
		  
	return ResponseEntity.ok("회원이 차단되었습니다.");		
	}
	
	//회원 차단 해제
	@PostMapping("/free")
	public ResponseEntity<String> freeMember(@RequestBody BanDto banDto) {
	       memberDao.freeMember(banDto);
	       
	   return ResponseEntity.ok("회원이 차단 해제되었습니다.");	     
	}
	
	
}
