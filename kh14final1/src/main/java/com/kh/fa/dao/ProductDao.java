package com.kh.fa.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.fa.dto.ProductDto;
import com.kh.fa.vo.ProductLikeListRequestVO;
import com.kh.fa.vo.ProductListRequestVO;
import com.kh.fa.vo.ProductListVO;

@Repository
public class ProductDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	// 시퀀스 번호 생성
	public int sequence() {
		return sqlSession.selectOne("product.sequence");
	}
	
	// 등록
	public void insert(ProductDto productDto) {
		sqlSession.insert("product.insert", productDto);
	}

	// 상품 테이블과 첨부 테이블 연결
	public void connect(int productNo, int attachmentNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("productNo", productNo);
		params.put("attachmentNo", attachmentNo);
		sqlSession.insert("product.connect", params);
	}
	
	// 이미지 찾기 - 여러 이미지 중 첫 번째만 가져오기
	public Integer findImage(int productNo) {
		return sqlSession.selectOne("product.findImage", productNo);
	}
	
	// 이미지 찾기 - 여러 이미지를 가져오기
	public List<Integer> findImages(int productNo) {
		return sqlSession.selectList("product.findImages", productNo);
	}
	
	// 목록 + 페이징 + 검색
	public List<ProductListVO> selectListByPaging(ProductListRequestVO requestVO){
		return sqlSession.selectList("product.list", requestVO);
	}

	// 연관 상품 목록 조회
	public List<ProductListVO> selectRelationList(int productNo, int productCategory){
		Map<String, Object> params = new HashMap<>();
		params.put("productNo", productNo);
		params.put("productCategory", productCategory);
		params.put("beginRow", 1);
		params.put("endRow", 6);
		return sqlSession.selectList("product.relation", params);
	}
	
	// 목록 카운트
	public int countWithPaging(ProductListRequestVO requestVO) {
		return sqlSession.selectOne("product.count", requestVO);
	}
	
	// 상세 조회
	public ProductDto selectOne(int productNo) {
		return sqlSession.selectOne("product.detail", productNo);
	}
	
	// 수정
	public boolean update(ProductDto productDto) {
		return sqlSession.update("product.edit", productDto) > 0;		
	}
	
	// 삭제 - 상품 정보
	public boolean delete(int productNo) {
		return sqlSession.delete("product.remove", productNo) > 0;
	}
	
	// 삭제 - 이미지 연결 테이블 정보
	public boolean deleteImage(int productNo) {
		return sqlSession.delete("product.removeImage", productNo) > 0;
	}

	// 상품 좋아요 갱신(최신화) 기능
	public boolean updateLikes(int productNo) {
		return sqlSession.update("product.likes", productNo) > 0;
	}	
	
	// 좋아요 목록 + 페이징 
	public List<ProductListVO> selectLikeList(String memberId) {
		return sqlSession.selectList("product.likeList", memberId);
	}
	
	// 좋아요 목록 카운트
//	public int countWithLike(ProductLikeListRequestVO vo) {
//		return sqlSession.selectOne("product.likeCount", vo);
//	}
	
	// 내 상품 목록 조회 : 페이징X 검색X
	public List<ProductListVO> selectMyList(String memberId) {
		return sqlSession.selectList("product.myList",memberId);
	}
	
	
}
