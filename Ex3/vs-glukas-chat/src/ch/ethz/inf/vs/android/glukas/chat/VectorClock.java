package ch.ethz.inf.vs.android.glukas.chat;

import android.annotation.SuppressLint;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 * This class should be used to handle vector clocks
 * 
 * @author hong-an
 *
 */
@SuppressLint("UseSparseArrays")
public class VectorClock implements Comparable<VectorClock>{
	/**
	 * This should contain the vector clock
	 */
	private HashMap<Integer, Integer> clock;
	
	/**
	 * This keeps track of the owner's index
	 */
	private int ownIndex;
	
	/**
	 * Constructor
	 */
	public VectorClock() {
		this.clock = new HashMap<Integer, Integer>();
		this.ownIndex = -1;
	}
	
	/**
	 * Constructor
	 * @param initClock
	 * @param initIndex
	 */
	public VectorClock(HashMap<Integer, Integer> initClock, int initIndex) {
		this.clock = initClock;
		this.ownIndex = initIndex;
	}

	/**
	 * This converts the VectorClock to the appropriate JSON format
	 * @return
	 */
	public JSONObject convertToJSON() {

		// TODO Fill me
		return null;
	}
	
	/**
	 * Use this to update the current vector clock to the newly received
	 * one
	 * @param toCompare The VectorClock that ours should be compared to
	 */
	public void update(VectorClock toCompare) {

		// TODO Fill me
	}

	@Override
	/**
	 * This function allows to compare VectorClocks
	 * @param another The VectorClock that ours should be compared to
	 */
	public int compareTo(VectorClock toCompare) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	/**
	 * This function should return a String representation of the
	 * VectorClock
	 * @return String that represents tha VectorClock
	 */
	public String toString(){
		// TODO Fill me
		
		String result = "{";
		for(Map.Entry<Integer, Integer> entry : clock.entrySet()){
			result += "\""+ String.valueOf(entry.getKey()) + "\" : "  + String.valueOf(entry.getValue())+  ","; 
		}
		result += "}";
		result = result.replace (",}", "}");
		return result;
	}
}
