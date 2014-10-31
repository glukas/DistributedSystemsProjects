package ch.ethz.inf.vs.android.glukas.protocol;

import ch.ethz.inf.vs.android.glukas.protocol.SyncType;


public class ChatLogicFactory {
	
	private static ChatLogic instance;
	private static SyncType syncType;
	
	public static ChatLogic getInstance(){
		if (instance == null){
			if (syncType != null){
				instance = new ChatLogic(syncType);
			} else {
				instance = new ChatLogic(SyncType.LAMPORT_SYNC);
			}
		}
		return instance;
	}
	
	public static void setSyncType(SyncType syncTypeArg){
		syncType = syncTypeArg;
	}
}
