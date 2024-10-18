package com.kh.fa.restcontroller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.fa.dao.AttachmentDao;
import com.kh.fa.service.AttachmentService;

// 사용자에게 파일을 전송하는 컨트롤러
@RestController // 이 컨트롤러는 화면이 아니라 데이터를 사용자에게 보낸다
@RequestMapping("/attach")
public class FileDownloadController {
	
	@Autowired
	private AttachmentDao attachmentDao;
	
//	// 1. 내가 직접 스프링을 거치지 않고 사용자에게 파일을 전송하는 방법
//	// (기존에 써오던 방식, 스프링이 안 좋아하는 방식)
//	// - HttpServletResponse는 사용자에게 보낼 정보를 담는 객체
//	//@RequestMapping("/download")
//	public void download(@RequestParam int attachmentNo,
//							HttpServletResponse response) throws IOException {
//		// (1) attachmentNo에 대한 데이터가 존재하는지 확인해야 한다
//		AttachmentDto attachmentDto = attachmentDao.selectOne(attachmentNo);
//		if(attachmentDto==null) throw new TargetNotFoundException("존재하지 않는 파일 번호");
//		
//		// (2) 정보가 있으므로 실제 파일을 불러온다
//		// - 파일을 한 번에 쉽게 불러주는 라이브러리 사용(apache commons io)
//		File dir = new File(System.getProperty("user.home"), "upload");
//		File target = new File(dir, String.valueOf(attachmentNo));
//		byte[] data = FileUtils.readFileToByteArray(target);
//		
//		// (3) 불러온 정보를 사용자에게 전송 (헤더 + 바디)
//		// 헤더 설정 명령 - response.setHeader("이름", "값");
//		// 바디 설정 명령 - response.getOutputStream().write(데이터);
//		// 양식이 정해져 있다
//		response.setHeader("Content-Encoding", "UTF-8");
//		response.setHeader("Content-Type", "application/octet-stream");
//		// 값의 해당 문구(application/octet-stream)가 나오는 순간 다운로드 바로 시작
//		//response.setHeader("Content-Type", attachmentDto.getAttachmentType());
//		// 처음에 적어놨던 형태(ex. png, gif 등)를 알려주면 서버가 알아서 판단 후 다운로드 시작
//		response.setHeader("Content-Length", // Length : 규격, 크기
//				String.valueOf(attachmentDto.getAttachmentSize())); 
//		response.setHeader("Content-Disposition",
//				"attachment; filename="+attachmentDto.getAttachmentName());
//		// attachment; : 해야하는 목록, 이 파일을 가져가라
//		// ; 는 구분선 역할을 한다
//		// attachmentDto.getAttachmentName() >> 한글이나 띄어쓰기 안되는 이유
//		// DB로 바로 들어가면서 띄어쓰기 인식이 안됨
//		// escape sequence 방식을 사용하면 해결됨 ( \" )
//		
//		response.getOutputStream().write(data); // 데이터 전송		
//	}
//	
//	// 2. 스프링에게 다운로드 가능한 상태로 데이터를 전달하는 방법 (추천)
//	// (스프링이 알아서 다운로드 기능을 구현해줌)
//	//@RequestMapping("/download")
//	public ResponseEntity<ByteArrayResource> download(
//			@RequestParam int attachmentNo) throws IOException{
//		// (1) attachmentNo에 대한 데이터가 존재하는지 확인해야 한다
//		AttachmentDto attachmentDto = attachmentDao.selectOne(attachmentNo);
//		if(attachmentDto==null) throw new TargetNotFoundException("존재하지 않는 파일 번호");
//		
//		// (2) 정보가 있으므로 실제 파일을 불러온다
//		// - 파일을 한 번에 쉽게 불러주는 라이브러리 사용(apache commons io)
//		File dir = new File(System.getProperty("user.home"), "upload");
//		File target = new File(dir, String.valueOf(attachmentNo));
//		byte[] data = FileUtils.readFileToByteArray(target);
//		
//		// (3) 불러온 정보를 사용자에게 전송 (헤더 + 바디)
//		ByteArrayResource resource = new ByteArrayResource(data); // 포장
//		return ResponseEntity.ok()
//			.contentType(MediaType.APPLICATION_OCTET_STREAM)
//			.contentLength(attachmentDto.getAttachmentSize())
//			.header(HttpHeaders.CONTENT_ENCODING, "UTF-8") // StandardCharsets.UTF_8 대체 가능
//			.header(HttpHeaders.CONTENT_DISPOSITION, 
//					ContentDisposition.attachment()
//						.filename(
//							attachmentDto.getAttachmentName(), 
//							StandardCharsets.UTF_8) // "UTF-8"을 못씀
//							.build().toString()
//					)
//			.body(resource);
//		// builder pattern, new 쓰지말고 해결하는 방식?
//	}
	
	@Autowired
	private AttachmentService attachmentService;
	
	@GetMapping("/download/{attachmentNo}")
	public ResponseEntity<ByteArrayResource> download(
			@PathVariable int attachmentNo) throws IOException{
		return attachmentService.find(attachmentNo);
	}
	
}
