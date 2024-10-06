package com.kh.fa.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.kh.fa.configuration.CustomCertProperties;
import com.kh.fa.dao.CertDao;

// 인증번호 청소 서비스 (스케쥴러, 스케쥴링 서비스)
// - 정해진 시간마다 "쓸모없어진" 인증번호를 삭제하는 서비스
// - 쓸모없다는 것은 인증시간이 만료된 데이터를 의미(현재는 10분)

@Service
public class CertCleanService {
	
	@Autowired
	private CertDao certDao;
	@Autowired
	private CustomCertProperties customCertProperties;
	
//	정기적으로 실행할 작업에 @Scheduled를 추가하여 실행 지시
//	@Scheduled(fixedRate = 1000L) // 1000ms마다 반복
//	@Scheduled(cron = "* * * * * *") // 크론 표현식(6자리 입력 필수), 매초 매분 매시 매일 매월 모든요일
//	@Scheduled(cron = "*/2 * * * * *") // 크론 표현식, 매2초마다 매분 매시 매일 매월 모든요일
//	@Scheduled(cron = "0-20 * * * * *") // 0~20초 ~~
//	@Scheduled(cron = "0,30 * * * * *") // 0,30초 ~~
	
//	(Q) 출근시간(0930)과 퇴근시각(1830)에 한 번씩 실행
//	@Scheduled(cron = "0 30 9,18 * * *")
//	(Q) 업무시간(0930-1830) 사이의 매 정각마다 한번씩 실행
//	@Scheduled(cron = "0 0 10-18 * * *")
//	(Q) 영업일 업무시각 정각마다 실행
//	@Scheduled(cron = "0 0 10-18 * * 1-5") // 월(1), 금(5), 토(6), 일(7)
//	@Scheduled(cron = "0 0 10-18 * * mon-fri") // 월(mon)~금(fri)
	
//	(Q) 매월 셋째주 목요일 오후 2시에 실행
//	@Scheduled(cron = "0 0 14 ? * 4#3") // 셋째주(#3) 목요일(4), ?는 무관을 의미
//	@Scheduled(cron = "0 0 14 ? * thu#3") // 셋째주(#3) 목요일(thu), ?는 무관을 의미
	
//	(Q) 매월 마지막주 목요일 오후 2시에 실행
//	@Scheduled(cron = "0 0 14 ? * 4L") // 마지막(L) 목요일(4)
//	@Scheduled(cron = "0 0 14 ? * thuL") // 마지막(L) 목요일(thu)
	
//	(Q) 급여일(매월 25일) 오후 3시에 실행
//	@Scheduled(cron = "0 0 15 25 * ?")
	
//	(Q) 매 시 정각마다 인증번호 청소 작업을 수행
	@Scheduled(cron = "0 0 * * * *")
	public void clean() {
		System.out.println("청소시작! "+LocalDateTime.now());
		certDao.clean(customCertProperties.getExpire()); // 만료 시간이 지난 데이터 모두 삭제
	}
	
	
}
