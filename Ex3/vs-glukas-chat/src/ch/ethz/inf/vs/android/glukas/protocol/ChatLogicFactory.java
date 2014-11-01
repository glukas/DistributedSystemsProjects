package ch.ethz.inf.vs.android.glukas.protocol;

import android.content.Context;
import ch.ethz.inf.vs.android.glukas.protocol.SyncType;


public class ChatLogicFactory {
	
	private static ChatLogic lamportInstance;
	private static ChatLogic vectorInstance;
	private static SyncType syncType = SyncType.LAMPORT_SYNC;
	
	public static ChatLogic getInstance(Context context){

		if (syncType == SyncType.LAMPORT_SYNC) {
			return getLamportInstance(context);
		} else {
			return getVectorClockInstance(context);
		}
	}
	
	public static void setSyncType(SyncType sync) {
		syncType = sync;
	}
	
	private static ChatLogic getLamportInstance(Context context) {
		if (lamportInstance == null) {
			lamportInstance = new ChatLogic(SyncType.LAMPORT_SYNC , context);
		}
		return lamportInstance;
	}
	
	private static ChatLogic getVectorClockInstance(Context context) {
		if (vectorInstance == null) {
			vectorInstance = new ChatLogic(SyncType.VECTOR_CLOCK_SYNC, context);
		}
		return vectorInstance;
	}
	

}
