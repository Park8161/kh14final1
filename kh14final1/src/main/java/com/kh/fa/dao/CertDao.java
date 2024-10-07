package com.kh.fa.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.CertDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class CertDao {

	@Autowired
	private SqlSession sqlSession;
	
	public void insert(CertDto certDto) {
		sqlSession.insert("cert.add", certDto);
	}
	
	public boolean delete(String certEmail) {
		return sqlSession.delete("cert.remove", certEmail) > 0;
	}
	
	//이메일과 인증번호가 유효한지 검사하는 기능
	public boolean check(CertDto certDto, int duration) {
		Map<Object, Object> map = new HashMap<>();
		map.put("certEmail", certDto.getCertEmail());
		map.put("certNumber", certDto.getCertNumber());
		map.put("duration", duration);
		List<CertDto> list = sqlSession.selectList("cert.check", map);
//		log.debug("결과 수 : {}", list.size());
		return list.size() > 0;
	}	
	
	//유효시간이 지난 인증번호를 삭제하도록 구현
	// minute = duration
	public boolean clean(int minute) {
		return sqlSession.delete("cert.clean", minute) > 0;
	}
	
}
