package ch.ethz.inf.vs.android.glukas.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
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
	private volatile boolean  socketBounded;
	private volatile boolean channelOpen;
	private int timeout;
	private int receiveBufSize;

	/**
	 * Constructor
	 * @throws IOException 
	 */
	public UDPCommunicator(String address, int port) {
		this.port = port;
		this.address = address;
		this.receiveBufSize = Utils.RECEIVE_BUFFER_SIZE;
		setupConnection();
	}
	
	/**
	 * Constructor
	 * @throws IOException 
	 */
	public UDPCommunicator(String address, int port, int receiveBufSize) {
		this.port = port;
		this.address = address;
		this.receiveBufSize = receiveBufSize;
		Log.v("", "just set up");
		setupConnection();
		Log.v("", "finish to create connection");
	}
	
	/**
	 * Close the connection properly
	 * @throws IOException
	 */
	public void finishConnection() throws IOException{
		if (socket != null)
			socket.close();
		if (channel != null) 
			channel.close();
	}
	
	public boolean retryOpen(){
		return openConnection();
	}
	
	public boolean retryBound(){
		if (socket == null){
			return false;
		}
		return boundSocket();
	}

	////
	//Connection set up
	////
	
	private void setupConnection() {
		channelOpen = openConnection();
		if (channelOpen) {
			socketBounded = boundSocket();
		}
		else {
			socketBounded = false;
		}
	}
	
	private boolean boundSocket() {
		try{
			SocketAddress localAddr = new InetSocketAddress(0);
			socket.bind(localAddr);
			socket.connect(InetAddress.getByName(address), port);
			return true;
		} catch (IOException ex){
			Log.e("UDPCommunicator", ex.getLocalizedMessage());
			return false;
		}
	}
	
	private boolean openConnection() {
		try{
			channel = DatagramChannel.open();
			socket = channel.socket();
			return true;
		} catch (IOException ex){
			Log.e("UDPCommunicator", ex.getLocalizedMessage());
			return false;
		}
	}
	
	////
	//Send and receive requests, responses
	////

	/**
	 * This function should be used to send a request to the server
	 * @param request The request in JSON format
	 * @throws IOException 
	 */
	public void sendRequest(JSONObject request) throws IOException {
		sendRequestString(request.toString());
	}
	
	/**
	 * This function can be used to send a request in string format to host and by port defined at construction
	 * @param request String representing the request to send
	 * @throws IOException
	 */
	public void sendRequestString(String request) throws IOException{
		DatagramPacket packet = new DatagramPacket(request.getBytes(), request.length());
		socket.send(packet);
	}
	
	/**
	 * Blocking method that waits for the first packet to arrive
	 * @return String representing message arrived at local port where the socket is bounded to
	 * @throws IOException
	 */
	public String receiveReply() throws IOException {
		byte[] buf = new byte[receiveBufSize];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		//TODO : fix the issue of empty packets
		do {
			socket.receive(packet);
			Log.v("","Received something");
		} while (packet.getLength() == 0);//ignore empty packets. (Why do we get those?)
		
		
		return new String(packet.getData(), packet.getOffset(), packet.getLength());
	}
	
	////
	//Getters and setters
	////
	
	/** 
	 * @return whether the socket is bounded to a port or not
	 */
	public boolean isBounded(){
		return socketBounded;
	}
	
	/** 
	 * @return whether the socket is bounded to a port or not
	 */
	public boolean isOpen(){
		return channelOpen;
	}
	
	/**
	 * @return port of the remote host
	 */
	public int getRemotePort(){
		return port;
	}
	
	/**
	 * Gets the local port which the socket is bound to
	 * @return local port, or -2 if socket is null, -1 if socket is closed, 0 if socket is unbound
	 */
	public int getLocalPort(){
		if (socket != null) {
			return socket.getLocalPort();
		} else{
			return -2;
		}
	}
	
	public String getAddress(){
		return address;
	}
	
	/**
	 * @return the maximum size of a arriving packet
	 */
	public int getReceiveBufSize(){
		return receiveBufSize;
	}
	
	/**
	 * Set a new size for maximum size of arriving packets
	 * @param receiveBufSize
	 */
	public void SetReceiveBufSize(int receiveBufSize){
		this.receiveBufSize = receiveBufSize;
	}
	
	public void setTimeout(int timeout) throws SocketException{
		this.timeout = timeout;
		socket.setSoTimeout(this.timeout);
	}
	
	public int getTimeout(){
		return timeout;
	}
}
