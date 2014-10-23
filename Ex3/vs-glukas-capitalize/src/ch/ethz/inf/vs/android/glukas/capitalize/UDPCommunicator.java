package ch.ethz.inf.vs.android.glukas.capitalize;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.DatagramSocketImpl;
import java.net.DatagramSocketImplFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import org.json.JSONObject;

import android.util.Log;

/**
 * This class should be used to interface with the server
 * using UDP
 * @author hong-an
 *
 */
public class UDPCommunicator {
	
	private final int port;
	private final String address;
	private DatagramSocket socket;
	private DatagramChannel channel;

	/**
	 * Constructor
	 * @throws IOException 
	 */
	public UDPCommunicator(String address, int port) {
		this.port = port;
		this.address = address;
		setupConnection();
	}

	/**
	 * This function should be used to setup the "connection" to the server
	 * Not crucial in task 1, but in task 2, the port should be bound.
	 * @return
	 */
	public boolean setupConnection() {
		
		try {
			channel = DatagramChannel.open();
			socket = channel.socket();
			SocketAddress localAddr = new InetSocketAddress(0);
			socket.bind(localAddr);
			socket.connect(InetAddress.getByName(address), port);
		} catch (Exception ex){
			Log.e("UDPCommunicator", "RESPONSE : failed to bind "+ex.getLocalizedMessage());
		} 
		return false;
	}

	/**
	 * This function should be used to send a request to the server
	 * @param request The request in JSON format
	 */
	public void sendRequest(JSONObject request) {

	}
	
	public void sendRequestString(String request) throws IOException{
		
		DatagramPacket packet = new DatagramPacket(request.getBytes(), request.length());
		Log.e("", "RESPONSE : call send");
		socket.send(packet);
		Log.e("", "RESPONSE : send");
	}
	
	public String receiveReply() throws IOException {

		
		byte[] buf = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		Log.e("", "RESPONSE : call receive");
		socket.receive(packet);
		Log.e("", "RESPONSE : received");
		return new String(packet.getData(), 0, packet.getLength());
	}
}
