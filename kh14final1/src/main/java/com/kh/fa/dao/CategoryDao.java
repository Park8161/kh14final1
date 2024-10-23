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
	
	// 단순 목록 조회
	public List<CategoryDto> selectList() {
		return sqlSession.selectList("category.list");
	}
	
	// 목록 + 페이징 + 검색
	public List<CategoryDto> selectListByPaging(CategoryListRequestVO requestVO) {
		return sqlSession.selectList("category.listByPaging", requestVO);
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
	
	// 대분류에 해당하는 중분류 목록 조회
	public List<CategoryDto> selectUpperCategory(int categoryGroup) {
	    return sqlSession.selectList("category.selectUpperCategory", categoryGroup);
	}
	
	public int sequence() {
		return sqlSession.selectOne("category.sequence");
	}

	
	
}
