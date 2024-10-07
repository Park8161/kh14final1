package com.kh.fa.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.MemberDto;
import com.kh.fa.vo.MemberBlockRequestVO;
import com.kh.fa.vo.MemberBlockVO;
import com.kh.fa.vo.MemberComplexRequestVO;

@Repository
public class MemberDao {
	
	@Autowired
	private SqlSession sqlSession;
	@Autowired
	private PasswordEncoder encoder;
	
	// 복합 검색 메소드
	public List<MemberDto> complexSearch(MemberComplexRequestVO vo){
		return sqlSession.selectList("member.complexSearch", vo);
	}
	
	// 복합 검색 카운트 메소드
	public int complexSearchCount(MemberComplexRequestVO vo) {
		return sqlSession.selectOne("member.complexSearchCount", vo);
	}
	
	// 상세
	public MemberDto selectOne(String memberId) {
		return sqlSession.selectOne("member.find", memberId);
	}
	
	// 로그인전용 상세조회(+암호화 비밀번호)
	public MemberDto selectOneWithPassword(String memberId, String memberPw) {
		List<MemberDto> list = sqlSession.selectOne("member.find", memberId);
		if(list.isEmpty()) return null;
		
		MemberDto memberDto = list.get(0); // 비밀번호 비교
		boolean isValid = encoder.matches(memberPw, memberDto.getMemberPw());
		return isValid ? memberDto : null;
	}	
	
	// 로그인전용 상세조회(+암호화 비밀번호)
//	public MemberDto selectOneWithPassword(String memberId, String memberPw) {
//		if(memberPw == null) {
//			return sqlSession.selectOne("member.find", memberId);
//		}
//		else {
//			List<MemberDto> list = sqlSession.selectOne("member.find", memberId);
//			if(list.isEmpty()) return null;
//			
//			MemberDto memberDto = list.get(0); // 비밀번호 비교
//			boolean isValid = encoder.matches(memberPw, memberDto.getMemberPw());
//			return isValid ? memberDto : null;
//		}
//	}
	
	// 차단 목록 + 페이징 
	public List<MemberBlockVO> selectListByPaging(MemberBlockRequestVO vo){
		return sqlSession.selectList("member.blockList", vo);
	}
	
	// 차단 목록 검색 카운트
	public int countWithPaging(MemberBlockRequestVO vo) {
		return sqlSession.selectOne("member.blockListCount", vo);
	}

	// 개인정보 변경
	public boolean update(MemberDto memberDto) {
		return sqlSession.update("member.edit", memberDto) > 0;
	}
	
	// 최종 로그인 시각 갱신
	public boolean updateMemberLogin(String memberId) {
		return sqlSession.update("member.login", memberId) > 0;
	}
	
	// 등록(회원가입)
	public void insert(MemberDto memberDto) {
		// 비밀번호 암호화
		String rawPw = memberDto.getMemberPw(); // 비밀번호 암호화 안된 것
		String encPw = encoder.encode(rawPw); // 암호화된 비밀번호
		memberDto.setMemberPw(encPw);
		
		sqlSession.insert("member.add", memberDto);		
	}
	
	// 비밀번호 변경
	public boolean updateMemberPw(MemberDto memberDto) {
		//비밀번호 암호화
		String rawPw = memberDto.getMemberPw(); // 비밀번호 암호화 안된 것
		String encPw = encoder.encode(rawPw); // 암호화된 비밀번호
		memberDto.setMemberPw(encPw);
		
		return sqlSession.update("member.editPw", memberDto) > 0;
	}
	
	// 회원 정보 수정 by 관리자
	public boolean updateMemberByAdmin(MemberDto memberDto) {
		return sqlSession.update("member.editByAdmin", memberDto) > 0;
	}
	
	
	
	
	
	
	
	
}
