package ch.ethz.inf.vs.a2.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class HttpSocketImpl implements HttpSocket{
	
	private String host = null;
	private int port = -1;
	private volatile boolean changed = true;
	Socket socket;
	PrintWriter printWriter;

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public void setHost(String hostArg) {
		changed = true;
		this.host = hostArg;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void setPort(int portArg) {
		changed = true;
		this.port = portArg;
	}

	@Override
	public String execute(String request) {
		if (changed){
			try {
				socket = new Socket(host, port);
				printWriter = new PrintWriter(socket.getOutputStream());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			changed = false;
		}
		printWriter.println(request);
		printWriter.flush();
		String reply = "";
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line;

			while((line = br.readLine()) != null) reply += line;
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reply;
	}

}
