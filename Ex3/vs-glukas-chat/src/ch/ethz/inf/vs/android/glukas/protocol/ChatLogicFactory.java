package ch.ethz.inf.vs.android.glukas.protocol;

import ch.ethz.inf.vs.android.glukas.protocol.SyncType;


public class ChatLogicFactory {
	
	private static ChatLogic lamportInstance;
	private static ChatLogic vectorInstance;
	private static SyncType syncType = SyncType.LAMPORT_SYNC;
	
	public static ChatLogic getInstance(){
		if (syncType == SyncType.LAMPORT_SYNC) {
			return getLamportInstance();
		} else {
			return getVectorClockInstance();
		}
	}
	
	public static void setSyncType(SyncType sync) {
		syncType = sync;
	}
	
	private static ChatLogic getLamportInstance() {
		if (lamportInstance == null) {
			lamportInstance = new ChatLogic(SyncType.LAMPORT_SYNC);
		}
		return lamportInstance;
	}
	
	private static ChatLogic getVectorClockInstance() {
		if (vectorInstance == null) {
			vectorInstance = new ChatLogic(SyncType.VECTOR_CLOCK_SYNC);
		}
		return vectorInstance;
	}
	

}
