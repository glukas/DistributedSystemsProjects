package ch.ethz.inf.vs.android.glukas.chat;

import ch.ethz.inf.vs.android.glukas.chat.Utils.SyncType;

public class ChatLogicFactory {
	
	private static ChatLogic instance;
	private static SyncType syncType;
	
	public static ChatLogic getInstance(){
		if (instance == null){
			if (syncType != null){
				instance = new ChatLogic(syncType);
			} else {
				instance = new ChatLogic(Utils.SyncType.LAMPORT_SYNC);
			}
		}
		return instance;
	}
	
	public static void setSyncType(SyncType syncTypeArg){
		syncType = syncTypeArg;
	}
}
