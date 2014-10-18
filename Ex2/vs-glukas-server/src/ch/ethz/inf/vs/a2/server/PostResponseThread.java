package ch.ethz.inf.vs.a2.server;

import java.io.IOException;

import ch.ethz.inf.vs.a2.server.ClientHandle.Status;

public class PostResponseThread extends Thread{
	
	private ClientHandle<ParsedRequest> client;
	private String reply;
	private Status status;
	
	public PostResponseThread(ClientHandle<ParsedRequest> client, String s, Status status){
		this.client = client;
		this.reply = s;
		this.status = status;
	}
	
	@Override
	public void run(){
		try {
			client.postResponse(reply, status);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
