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

import com.kh.fa.dao.QnaDao;
import com.kh.fa.dto.QnaDto;
import com.kh.fa.error.TargetNotFoundException;
import com.kh.fa.vo.QnaListRequestVO;
import com.kh.fa.vo.QnaListResponseVO;

import io.swagger.v3.oas.annotations.Parameter;

@CrossOrigin
@RestController
@RequestMapping("/qna")
public class QnaRestController {

	@Autowired
	private QnaDao qnaDao;
	
	@PostMapping("/insert")
	public void insert(@RequestBody QnaDto qnaDto) {
		int qnaNo = qnaDao.sequence();
		System.out.println(qnaDto);
		System.out.println(qnaNo);
		qnaDto.setQnaNo(qnaNo);
		qnaDao.insert(qnaDto);
	}
	
	@GetMapping("/detail/{qnaNo}")//상세
	public QnaDto detail(
			@Parameter(required = true, description = "글 번호(PK)")
			@PathVariable int qnaNo) {
		
		QnaDto qnaDto = qnaDao.selectOne(qnaNo);
		if(qnaDto == null) throw new TargetNotFoundException();
		return qnaDto;
	}
	
	@PostMapping("/list")//조회
	public QnaListResponseVO list(@RequestBody QnaListRequestVO vo){
		int count = qnaDao.countWithPaging(vo);
		boolean last = vo.getEndRow() == null  || count <= vo.getEndRow();
		QnaListResponseVO response = new QnaListResponseVO();
		response.setQnaList(qnaDao.selectListByPaging(vo));
		response.setCount(count);
		response.setLast(last);
		return response;
	}	
	
	@DeleteMapping("/delete/{qnaNo}")//삭제
	public void delete(@PathVariable int qnaNo) {
		qnaDao.delete(qnaNo);
	}
	
}