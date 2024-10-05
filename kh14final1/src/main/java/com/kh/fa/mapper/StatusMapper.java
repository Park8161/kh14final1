package com.kh.fa.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.kh.fa.vo.StatusVO;

@Service
public class StatusMapper implements RowMapper<StatusVO> {

	@Override
	public StatusVO mapRow(ResultSet rs, int rowNum) throws SQLException {
		StatusVO statusVO = new StatusVO();
		statusVO.setTitle(rs.getString("title"));
		statusVO.setCnt(rs.getInt("cnt"));		
		// 효율적이고 적은 개수로 VO를 활용하기 위해서 별칭(cnt, title)이 필수
		return statusVO;
	}

}
