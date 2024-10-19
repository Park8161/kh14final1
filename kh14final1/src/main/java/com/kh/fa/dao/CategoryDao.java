package com.kh.fa.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.CategoryDto;
import com.kh.fa.vo.CategoryListRequestVO;
import com.kh.fa.vo.CategoryNameVO;

@Repository
public class CategoryDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	// 상품 번호를 받아 대중소분류 이름 다 나오게 조회
	public CategoryNameVO selectNameList(int productNo) {
		return sqlSession.selectOne("category.nameList", productNo);
	}
	
	// 등록
	public void insert(CategoryDto categoryDto) {
		sqlSession.insert("category.insert", categoryDto);
	}
	
	// 목록 + 페이징 + 검색
	public List<CategoryDto> selectList(CategoryListRequestVO requestVO) {
		return sqlSession.selectList("category.list", requestVO);
	}
	
	// 목록카운트
	public int countWithPaging(CategoryListRequestVO requestVO) {
		return sqlSession.selectOne("category.count", requestVO);
	}
	
	// 상세
	public CategoryDto selectOne(int categoryNo) {
		return sqlSession.selectOne("category.detail", categoryNo);
	}
	
	// 수정
	public boolean update(CategoryDto categoryDto) {
		return sqlSession.update("category.update", categoryDto) > 0;
	}
	
	// 삭제
	public boolean delete(int categoryNo) {
		return sqlSession.delete("category.delete", categoryNo) > 0;
	}
	
	
}
