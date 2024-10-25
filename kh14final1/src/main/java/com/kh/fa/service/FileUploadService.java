package com.kh.fa.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kh.fa.configuration.CustomFileuploadProperties;
import com.kh.fa.error.TargetNotFoundException;

@Service
public class FileUploadService {
	
//	//파일 저장경로
//	@Autowired
//	private CustomFileuploadProperties properties;
//	
//	public void uploadFile(FileItem fileItem) {
//		MultipartFile multipartFile =  convertToMultipartFile(fileItem);//멀티파트 파일로 파일 변환
//		
//		if(multipartFile.isEmpty()) {
//			throw new TargetNotFoundException("파일이 비어있습니다.");
//		}
//		
//		//파일 저장
//		try {
//				File destinationFile = new File(properties.getPath(), multipartFile.getOriginalFilename());
//			try(FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
//				outputStream.write(multipartFile.getBytes());
//			}
//		} catch(IOException e) {
//			e.printStackTrace();
//			throw new TargetNotFoundException("파일 저장 중 오류가 발생했습니다.");
//		}
//	}	
//	
//	//멀티파트파일로 변환
//	private MultipartFile convertToMultipartFile(FileItem fileItem) {
//	    return new MultipartFile() {
//	        @Override
//	        public String getName() {
//	            return fileItem.getName();
//	        }
//
//	        @Override
//	        public String getOriginalFilename() {
//	            return fileItem.getName();
//	        }
//
//	        @Override
//	        public String getContentType() {
//	            return fileItem.getContentType();
//	        }
//
//	        @Override
//	        public boolean isEmpty() {
//	            return fileItem.getSize() == 0;
//	        }
//
//	        @Override
//	        public long getSize() {
//	            return fileItem.getSize();
//	        }
//
//	        @Override
//	        public byte[] getBytes() throws IOException {
//	            return fileItem.get();
//	        }
//
//	        @Override
//	        public InputStream getInputStream() throws IOException {
//	            return fileItem.getInputStream();
//	        }
//
//	        @Override
//	        public void transferTo(File dest) throws IOException, IllegalStateException {
//	            try {
//					fileItem.write(dest);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//	        }
//	    };
//	}
}
