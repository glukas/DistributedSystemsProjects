package ch.ethz.inf.vs.android.glukas.chat;

import java.io.Serializable;

public class MessageRequest implements Serializable {

	private static final long serialVersionUID = -2035223644194480230L;
	
	public enum MessageRequestType {
		sendMessage,
		register,
		deregister,
		getClients;
	};
	
	MessageRequest(int id, String message, MessageRequestType type) {
		this.id = id;
		this.message = message;
		this.type = type;
	}
	
	public final MessageRequestType type;
	public final int id;
	public final String message;
	
}
