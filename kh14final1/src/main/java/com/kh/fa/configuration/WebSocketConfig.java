package com.kh.fa.configuration;

import java.util.logging.SocketHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

//바이너리 메세지 최대크기 지정
@Configuration
@EnableWebSocket
public class WebSocketConfig {
//	@Autowired
//	SocketHandler socketHandler; //웹소캣 연결의 전반적인 관리
//	
//	@Bean
//	public ServletServerContainerFactoryBean createWebSocketContainer() {
//		ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
//		container.setMaxBinaryMessageBufferSize(5242880); //5MB
//		return container;
//	}
}
