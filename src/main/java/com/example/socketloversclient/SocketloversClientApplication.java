package com.example.socketloversclient;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class SocketloversClientApplication {

	private static Logger logger = LogManager.getLogger("SocketloversClientApplication");
	private final static WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

	public ListenableFuture<StompSession> connect(){
		Transport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient());
		List<Transport> transports = Collections.singletonList(webSocketTransport);

		SockJsClient sockJsClient = new SockJsClient(transports);
		sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());

		WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);

		String url = "ws://localhost:8080/chats";
		return stompClient.connect(url, headers, new MyHandler(), "localhost", 8080);
	}

	private class MyHandler extends StompSessionHandlerAdapter{
		@Override
		public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
			logger.info("Now Connected");
			//super.afterConnected(session, connectedHeaders);
		}
	}


	public static void main(String[] args) throws Exception {


		SocketloversClientApplication socketClient = new SocketloversClientApplication();

		ListenableFuture<StompSession> f = socketClient.connect();
		StompSession stompSession = f.get();

		Thread.sleep(60000);


		//SpringApplication.run(SocketloversClientApplication.class, args);
	}

}
