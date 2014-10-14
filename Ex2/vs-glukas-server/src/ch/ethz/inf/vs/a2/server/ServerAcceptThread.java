package ch.ethz.inf.vs.a2.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

public class ServerAcceptThread<T> extends Thread {


	public final int port;
	private final RequestParser<T> parser;
	private final ParsedRequestConsumer<T> consumer;
	private volatile boolean alive = true;
	
	public void terminateGracefully() {
		alive = false;
		//TODO maybe have to stop the socket
	}
	
	/**
	 * THREADING:
	 * PARSER MUST BE THREAD SAFE
	 * CONSUMER IS ALWAYS CALLED ON THE UI THREAD
	 * @param port
	 * @param consumer
	 * @param parser
	 */
	public ServerAcceptThread(int port, ParsedRequestConsumer<T> consumer, RequestParser<T> parser) {
		this.port = port;
		this.consumer = consumer;
		this.parser = parser;
	}
	
	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			
			//Accept clients and dispatch workers to deal with requests
			//Note that the port sequentializes the client requests anyway so it doesn't make sense
			//to have multiple threads waiting for clients
			while (alive) {
				Socket clientSocket = serverSocket.accept();
				ClientRequestTask<T> worker = new ClientRequestTask<T>(consumer, parser);
				worker.execute(clientSocket);
			}
			serverSocket.close();
			
		} catch (IOException e) {
			
			Log.e(this.getClass().toString(),e.getLocalizedMessage());
		}
	}
}
