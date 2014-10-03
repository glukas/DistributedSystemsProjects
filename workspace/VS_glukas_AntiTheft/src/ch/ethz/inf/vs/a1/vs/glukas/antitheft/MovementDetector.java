package ch.ethz.inf.vs.a1.vs.glukas.antitheft;



import android.hardware.SensorEventListener;
import android.os.Handler;


public class MovementDetector extends AbstractMovementDetector implements SensorEventListener {
	Handler mHandler;
	Runnable detected;
	private final long UNSIG_MOVE_DURATION = 5000;
	boolean return_value = false;
	boolean is_changing = false;
	
	@Override
	protected boolean doAlarmLogic(float[] values) {
		// TODO Auto-generated method stub
		double average = Math.sqrt(Math.pow(values[0] , 2) + Math.pow(values[0] , 2) + Math.pow(values[0] , 2));
		if (average > 1 && !is_changing){
			is_changing = true;
			mHandler.postDelayed(detected = new Runnable() {
				@Override
				public void run() {
					return_value = true;
				}
			}, UNSIG_MOVE_DURATION);

			
		} 
		if (average <= 1 ){
			
			mHandler.removeCallbacks(detected);
			return_value = false;
			is_changing = false;
			
		}
		
		return return_value;
	}

}
