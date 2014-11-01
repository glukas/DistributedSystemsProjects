package ch.ethz.inf.vs.android.glukas.protocol;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class should be used to handle vector clocks
 * 
 * @author hong-an
 * 
 */
@SuppressLint("UseSparseArrays")
public class VectorClock implements SyntheticClock<VectorClock> {
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

	public VectorClock(HashMap<Integer, Integer> initClock) {
		this.clock = initClock;
		this.ownIndex = -1;
	}

	/**
	 * Constructor
	 * 
	 * @param initClock
	 * @param initIndex
	 */
	public VectorClock(HashMap<Integer, Integer> initClock, int initIndex) {
		this.clock = initClock;
		this.ownIndex = initIndex;
	}

	/**
	 * This converts the VectorClock to the appropriate JSON format
	 * 
	 * @return
	 * @throws JSONException
	 */
	public JSONObject convertToJSON() throws JSONException {
		// TODO Fill me
		JSONObject returnJSON = new JSONObject();
		for (Integer key : clock.keySet()) {
			returnJSON.put(key.toString(), clock.get(key));
		}

		return returnJSON;
	}

	/**
	 * Use this to update the current vector clock to the newly received one
	 * 
	 * @param toCompare
	 *            The VectorClock that ours should be compared to
	 */
	public void update(VectorClock toCompare) {
		// TODO Fill me			
		for (Integer element : toCompare.clock.keySet())
		{
			if (this.clock.containsKey(element)){
				this.replaceWithMaximum(element, toCompare);
			}
			else {
				this.clock.put(element, toCompare.clock.get(element));
			}
			
			
		}
		
		//Not sure if the below part is needed
		// Remove all keys + values which the newMessage does not have
		
		Iterator<Map.Entry<Integer,Integer>> iter = this.clock.entrySet().iterator();
		while (iter.hasNext()) {
		    Map.Entry<Integer,Integer> entry = iter.next();
		    if(!toCompare.clock.containsKey(entry.getKey())){
		        iter.remove();
		    }
		}
		
		Log.e("Updated VectorClock if usDeliverable: ",  this.toString());
		
		
		

		
		
	}
	
	public void replaceWithMaximum(Integer element, VectorClock toCompare){
		Integer maximum = this.getMaximum(this.clock.get(element), toCompare.clock.get(element));
		this.clock.put(element, maximum);
		
		
	}
	public Integer getMaximum(Integer thisValue, Integer otherValue){
		if (thisValue < otherValue){
			return otherValue;
		}
		else {
			return thisValue;
		}
	}

	// TODO does this belong in the parser?
	@Override
	/**
	 * This function should return a String representation of the
	 * VectorClock
	 * @return String that represents tha VectorClock
	 */
	public String toString() {
		// TODO Fill me

		String result = "{";
		for (Map.Entry<Integer, Integer> entry : clock.entrySet()) {
			result += "\"" + String.valueOf(entry.getKey()) + "\" : "
					+ String.valueOf(entry.getValue()) + ",";
		}
		result += "}";
		result = result.replace(",}", "}");
		return result;
	}

	// //
	// SYNTHETIC CLOCK
	// //

	/**
	 * This function allows to compare VectorClocks
	 * 
	 * @param another
	 *            The VectorClock that ours should be compared to
	 */
	public int compareTo(VectorClock toCompare) {
		// VectorClock Comparison by element
		Integer isSmaller = 0;
		
		for (Integer element : this.clock.keySet()) {
			if (!toCompare.clock.containsKey(element))//TODO maybe set to 0 by default instead of ignoring?
				continue;
			if (this.clock.get(element) < toCompare.clock.get(element)) {
				if (isSmaller == 1) {
					return this.IndexcompareTo(toCompare);
				}
				isSmaller = -1;
			} else if (this.clock.get(element) > toCompare.clock.get(element)) {
				if (isSmaller == -1) {
					return this.IndexcompareTo(toCompare);
				}
				isSmaller = 1;
			} else if (this.clock.get(element) == toCompare.clock.get(element)) {
				// Do nothing
			}
		}

		// TODO : Is this implementation right?
		return isSmaller;

	}

	public int IndexcompareTo(VectorClock toCompare) {
		// Just compare the the two values located at the senders own indexes
		if (this.clock.get(this.ownIndex) < toCompare.clock.get(this.ownIndex)
				&& this.clock.get(toCompare.ownIndex) < toCompare.clock
						.get(toCompare.ownIndex)) {
			Log.v("IndexcompareTo: ", "previous after this");
			return -1;
		} else if (this.clock.get(this.ownIndex) > toCompare.clock
				.get(this.ownIndex)
				&& this.clock.get(toCompare.ownIndex) > toCompare.clock
						.get(toCompare.ownIndex)) {

			Log.v("IndexcompareTo: ", "this after previous");
			return 1;
		} else {
			Log.v("IndexcompareTo:", "couldnt decide!");
			return 0;
		}

	}

	@Override
	public boolean isDeliverable(VectorClock lastDeliveredMessage) {
		
		for (Integer element : this.clock.keySet())	
		{	 
			
			// Calculating differences for each index
			Integer difference = 0;
			if (lastDeliveredMessage.clock.containsKey(element)){
			difference = this.clock.get(element) - lastDeliveredMessage.clock.get(element);
			}
			else {
				difference = this.clock.get(element);
			}
			
			
			
			// If its the index of the sender the difference is allowed to be 1
			if (element == this.ownIndex)
			{
				if (difference <= 1){
					continue;
				}
				else {
					return false;
				}
				
			}
			// If its an index different from the sender there should be difference <= 0
			else{
				if (difference <= 0){
					continue;
				}
				else {
					return false;
				}
					
			}
			
		}
		
			
		return true;	
	}	
			
			
			
	
	@Override
	public VectorClock getClock() {
		return this;
	}

	@Override
	public void tick() {
		Integer newvalue = this.clock.get(this.ownIndex) +1;
		this.clock.remove(this.ownIndex);
		this.clock.put(this.ownIndex, newvalue);
		// TODO (YOUNG) Auto-generated method stub
		
	}
}
