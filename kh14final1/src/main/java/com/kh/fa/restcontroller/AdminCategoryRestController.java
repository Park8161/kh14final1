package com.kh.fa.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.fa.dao.CategoryDao;
import com.kh.fa.dto.CategoryDto;
import com.kh.fa.error.TargetNotFoundException;
import com.kh.fa.vo.CategoryListRequestVO;
import com.kh.fa.vo.CategoryListResponsVO;

@CrossOrigin
@RestController
@RequestMapping("/admin/category")
public class AdminCategoryRestController {
	
	@Autowired
	private CategoryDao categoryDao;
	
	// 등록 - 카테고리 번호도 입력받아와야함  
	@PostMapping("/insert")
	public void insert(@RequestBody CategoryDto categoryDto) {
//		System.out.println(categoryDto);
		int seq = categoryDao.sequence();
		categoryDto.setCategoryNo(seq);
		if(categoryDto.getCategoryDepth() == 1) {
			categoryDto.setCategoryGroup(seq);
			categoryDto.setCategoryUpper(null);
		}
		else {
			CategoryDto upperDto = categoryDao.selectOne(categoryDto.getCategoryUpper());
			categoryDto.setCategoryGroup(upperDto.getCategoryGroup());
		}
		categoryDao.insert(categoryDto);
	}
	
	// 목록
	@GetMapping("/listP")
	public List<CategoryDto> listP() {
		List<CategoryDto> list = categoryDao.selectList();
		return list;
	}
	
	// 목록 + 페이징 + 검색
	@PostMapping("/list")
	public CategoryListResponsVO list(@RequestBody CategoryListRequestVO requestVO) {
		// 페이징 정보 정리
		int count = categoryDao.countWithPaging(requestVO);
		boolean last = requestVO.getEndRow() == null || count <= requestVO.getEndRow();
		// 출력 클래스에 입력
		CategoryListResponsVO responseVO = new CategoryListResponsVO();
		responseVO.setCategoryList(categoryDao.selectListByPaging(requestVO));
		responseVO.setCount(count);
		responseVO.setLast(last);
		
		return responseVO;
	}
	
	// 상세
	@GetMapping("/detail/{categoryNo}")
	public CategoryDto detail(@PathVariable int categoryNo) {
		CategoryDto categoryDto = categoryDao.selectOne(categoryNo);
		if(categoryDto == null) throw new TargetNotFoundException("존재하지 않는 분류 번호");
		return categoryDto;
	}
	
	// 수정
	@PostMapping("/update/{categoryNo}")
	public void update(@PathVariable int categoryNo, @RequestBody CategoryDto categoryDto) {
		categoryDto.setCategoryNo(categoryNo);
		if(categoryDto.getCategoryDepth() == 1) {
			categoryDto.setCategoryGroup(categoryNo);
			categoryDto.setCategoryUpper(null);
		}
		else {
			CategoryDto upperDto = categoryDao.selectOne(categoryDto.getCategoryUpper());
			categoryDto.setCategoryGroup(upperDto.getCategoryGroup());
		}
		categoryDao.update(categoryDto);
	}
	
	// 삭제
	@DeleteMapping("/delete/{categoryNo}")
	public void delete(@PathVariable int categoryNo) {
		categoryDao.delete(categoryNo);
	}
	
	// 중분류 목록 조회
	@GetMapping("/upper/{categoryGroup}")
	public List<CategoryDto> getUpperCategories(@PathVariable int categoryGroup) {
	    List<CategoryDto> upperCategories = categoryDao.selectUpperCategory(categoryGroup);
	    return upperCategories;
	}
	
	//하위 카테고리 조회
	@GetMapping("/contains/{categoryNo}")
	public boolean contains(@PathVariable int categoryNo) {
		int result = categoryDao.checkCotains(categoryNo);
		return result > 0;
	}
}
