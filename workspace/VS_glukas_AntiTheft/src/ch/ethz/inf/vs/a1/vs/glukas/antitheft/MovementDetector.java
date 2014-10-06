package ch.ethz.inf.vs.a1.vs.glukas.antitheft;

import java.util.Date;

import android.hardware.SensorEventListener;

public class MovementDetector extends AbstractMovementDetector implements SensorEventListener {
	
	//time in ms where we respectively first detected movement and we get new values from sensor
	private long firstMove = 0;
	private long actualMove;
	//arbitrary time defined in assignment
	private final int UNSIG_MOVE_DURATION = 5000;
	
	@Override
	protected boolean doAlarmLogic(float[] values) {
		//compute the average of movement. "very scientifically provable"
		double average = Math.sqrt(Math.pow(values[0] , 2) + Math.pow(values[1] , 2) + Math.pow(values[2] , 2));

		if (average > 1){
			actualMove = new Date().getTime();
			if (firstMove == 0){
				firstMove = actualMove;
				return false;
			} else if (actualMove - firstMove >= UNSIG_MOVE_DURATION){
				firstMove = actualMove;
				return true;
			}
		} else {
			//if there's no more consequent move
			firstMove = 0;
		}
		return false;
	}
	
	
	
	
	
	
	/*
	Handler mHandler;
	Runnable detected;
	private final long UNSIG_MOVE_DURATION = 2000;
	private boolean return_value = false;
	private boolean is_changing = false;
	
	@Override
	protected boolean doAlarmLogic(float[] values) {
		
		double average = Math.sqrt(Math.pow(values[0] , 2) + Math.pow(values[1] , 2) + Math.pow(values[2] , 2));
		if (average > 1 && !is_changing){
			mHandler = new Handler();
			is_changing = true;
			mHandler.postDelayed(detected = new Runnable() {
				@Override
				public void run() {
					return_value = true;
				}
			}, UNSIG_MOVE_DURATION);

			
		} 
		if (average < 0.1 && is_changing){
			
			mHandler.removeCallbacks(detected);
			return_value = false;
			is_changing = false;
		
		}
		
		return return_value;
	}
*/
}
