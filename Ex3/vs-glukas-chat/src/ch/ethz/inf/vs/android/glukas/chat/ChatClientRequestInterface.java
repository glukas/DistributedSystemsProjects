package ch.ethz.inf.vs.android.glukas.chat;

public interface ChatClientRequestInterface {

	public void register(String username);
	
	public void deregister();
	
	public void sendMessage(String message, int messageId);
	
	public void getClients();
	
}
