package ch.ethz.inf.vs.android.glukas.chat;

import ch.ethz.inf.vs.android.glukas.chat.Utils.SyncType;

public class ChatLogicFactory {
	
	private static ChatLogic instance;
	private static SyncType syncType;
	private static String username = "";
	
	public static ChatLogic getInstance(){
		if (instance == null){
			if (syncType != null){
				instance = new ChatLogic(syncType, username);
			} else {
				instance = new ChatLogic(Utils.SyncType.LAMPORT_SYNC, username);
			}
		}
		return instance;
	}
	
	public static void setSyncType(SyncType syncTypeArg){
		syncType = syncTypeArg;
	}
	
	public static void setUsername(String usernameArg){
		username = usernameArg;
	}
}
