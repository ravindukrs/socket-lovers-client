package com.example.socketloversclient;

import com.example.socketloversclient.cipher.Cipher;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class SocketloversClientApplication {
    private static String loggedInUser = null;
    private static String message = null;
	private static String incomingMessage = null;


	static Scanner input = new Scanner(System.in);




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

	public void subscribeChat(StompSession stompSession){
		stompSession.subscribe("/topic/public", new StompSessionHandler() {
			@Override
			public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {

			}

			@Override
			public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {

			}

			@Override
			public void handleTransportError(StompSession stompSession, Throwable throwable) {

			}

			@Override
			public Type getPayloadType(StompHeaders stompHeaders) {
				return byte[].class;
			}

			@Override
			public void handleFrame(StompHeaders stompHeaders, Object o) {
				incomingMessage = new String((byte[]) o);
				JSONObject jsonObject = new JSONObject(incomingMessage);
				String type = (String) jsonObject.get("type");
				if("CHAT".equals(type)){
					System.out.println((String) jsonObject.get("sender")+": "+Cipher.decrypt((String) jsonObject.get("content"),26-1));
				}else if("JOIN".equals(type)){
					System.out.println((String) jsonObject.get("sender")+" joined the Chat");
				}
				//logger.info("Received Message " + new String((byte[]) o));
			}
		});
	}

	public void sendJoinMessage(StompSession stompSession, String sender) {
		String jsonHello = "{ \"sender\" : \""+sender+"\", \"type\" : \"JOIN\"}";
		stompSession.send("/app/chat.register", jsonHello.getBytes());
	}


	public void sendMessage(StompSession stompSession, String content, String sender) {
		String jsonHello = "{ \"content\" : \""+content+"\", \"sender\" : \""+sender+"\", \"type\" : \"CHAT\"}";
		stompSession.send("/app/chat.send", jsonHello.getBytes());
	}


	public static void main(String[] args) throws Exception {



		SocketloversClientApplication socketClient = new SocketloversClientApplication();

		ListenableFuture<StompSession> f = socketClient.connect();
		StompSession stompSession = f.get();

		logger.info("Subscribing to Chat topic using session " + stompSession);
		socketClient.subscribeChat(stompSession);

		System.out.println("Welcome to Socket Lovers!");
		System.out.println("Enter your Username: ");
		loggedInUser = input.nextLine();
		socketClient.sendJoinMessage(stompSession, loggedInUser);
		System.out.println("Thank you, "+loggedInUser+" you may chat now");

		while(true){

			//Get input from user and Encrypt
            message = Cipher.encrypt(input.nextLine(), 1);
            //logger.info("Sending hello message" + stompSession);
            socketClient.sendMessage(stompSession, message, loggedInUser);
            Thread.sleep(1000);

        }

//Thread.sleep(60000);
	}

}
