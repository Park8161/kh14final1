package com.kh.fa.restcontroller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/dummy/file")
public class TestFileRestController {

	@PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void upload(@RequestPart MultipartFile attach) {
		log.info("attach = {}, {}, {}", attach.getOriginalFilename(), attach.getContentType(), attach.getSize());
	}
	@PostMapping(value="/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void uploads(@RequestParam List<MultipartFile> attachList) {
		log.info("attach count = {}", attachList.size());
	}
	@PostMapping(value="/uploadWithDto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void uploadWithDto(@ModelAttribute TestVO testVO) {
		log.info("testVO = {}", testVO);
	}
	
}
