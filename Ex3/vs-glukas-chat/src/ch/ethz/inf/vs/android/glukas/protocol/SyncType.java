package ch.ethz.inf.vs.android.glukas.protocol;

/**
 * Useful for differentiating between selecting
 * Lamport timestamps and vector clocks
 * @author hong-an
 *
 */
public enum SyncType {
	LAMPORT_SYNC(1), VECTOR_CLOCK_SYNC(2);
	private int type;
	
	private SyncType(int t){
		type = t;
	}
	
	public int getTypeId(){
		return type;
	}
	
	public static SyncType getSyncTypeById(int id){
		return id == 1 ? SyncType.LAMPORT_SYNC : SyncType.VECTOR_CLOCK_SYNC;
	}
}
