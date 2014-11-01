package ch.ethz.inf.vs.android.glukas.protocol;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.HashMap;
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
		this.clock = toCompare.clock;
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
			if (toCompare.clock.get(element) == null)
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
		Integer difference = 0;
		if (lastDeliveredMessage.clock.get(this.ownIndex) != null)
			difference = this.clock.get(this.ownIndex)
					- lastDeliveredMessage.clock.get(this.ownIndex);
		if (difference == 1) {
			return true;
		} else if (difference > 1) {
			return false;
		} else {
			// If its an earlier message just display directly
			return true;
		}

	}

	@Override
	public VectorClock getClock() {
		return this;
	}
}
