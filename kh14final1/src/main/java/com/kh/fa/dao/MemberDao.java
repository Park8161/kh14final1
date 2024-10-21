package com.kh.fa.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.BanDto;
import com.kh.fa.dto.MemberDto;
import com.kh.fa.dto.ProductDto;
import com.kh.fa.vo.MemberComplexRequestVO;
import com.kh.fa.vo.MemberDetailVO;

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
	public boolean updateMemberPw(String memberId, String changePw) {
		//비밀번호 암호화
		String rawPw = changePw; // 비밀번호 암호화 안된 것
		String encPw = encoder.encode(rawPw); // 암호화된 비밀번호
		Map<Object, Object> map = new HashMap<>();
		map.put("memberId", memberId);
		map.put("memberPw", encPw);
		
		return sqlSession.update("member.changePw", map) > 0;
	}
	
	// 회원 정보 수정 by 관리자
	public boolean updateMemberByAdmin(MemberDto memberDto) {
		return sqlSession.update("member.editByAdmin", memberDto) > 0;
	}

	// 회원 정보 삭제(관리자)
	public boolean delete(String memberId) {
		return sqlSession.delete("admin.del", memberId) > 0;
	}

	//목록 조회(관리자)
	public List<MemberDto> selectList() {		
		return sqlSession.selectList("admin.list");
	}

	//검색 기능(관리자)
	public List<MemberDto> selectList(String column, String keyword) {
		Map<String, Object> params = new HashMap<>();
		params.put("column", column);
		params.put("keyword", keyword);
		return sqlSession.selectList("admin.search", params);
	}

	public MemberDetailVO selectMemberDetail(String memberId) {
		MemberDetailVO memberDetailVO = new MemberDetailVO();
		
		if(sqlSession.selectOne("member.selectMemberDetail", memberId) == null) {
			memberDetailVO.setMemberId(memberId);
			//리뷰받은 기록이 없을 경우 디폴트 신뢰도 50
			memberDetailVO.setReliability(50);
			memberDetailVO.setReviewCnt(0);			
		}
		else {
			memberDetailVO = sqlSession.selectOne("member.selectMemberDetail", memberId);
		}
		
		return memberDetailVO;
	}
	
	//특정 멤버의 상품 리스트를 추출하는 메소드
	public List<ProductDto> selectMemberProduct(String memberId){
		List<ProductDto> list = sqlSession.selectList("member.selectMemberProduct", memberId);
		return list;
	}
	
	//회원 상세 조회(관리자)
	public MemberDto selectOneAdmin(String memberId) {
		return sqlSession.selectOne("admin.detail", memberId);
	}
	
	//회원 정보 수정(관리자)
	public boolean updateAdmin(MemberDto memberDto) {
		return sqlSession.update("admin.edit", memberDto) > 0;
	}
	
	//회원 차단(관리자)
	public void banMember(BanDto banDto) {
		 sqlSession.insert("ban.bann", banDto);
	}
	
	//회원 차단해제(관리자
	public void freeMember(BanDto banDto) {
		 sqlSession.insert("ban.free", banDto);
	}
	//차단 상태 조회
		public BanDto selectBanCheck(String memberId) {
			return sqlSession.selectOne("ban.selectBanCheck", memberId);
		}
	
	
}
