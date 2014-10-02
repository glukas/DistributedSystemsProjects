package ch.ethz.inf.vs.a1.vs.glukas.antitheft;

import ch.ethz.inf.vs.a1.vs_glukas_antitheft.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.support.v4.app.NotificationCompat;

public abstract class AbstractAntiTheftService extends Service {

	protected AbstractMovementDetector listener;
	protected int notifId = 001;
	protected NotificationManager notifManager;
	protected NotificationCompat.Builder notifBuilder;
	protected Notification notif;
	protected int stateProgressBar = 0;
	protected int timeout = Settings.TIMEOUT_DEFAULT;

	@Override
	public void onCreate() {
		
		//Create the builder for the notification with all features
		notifBuilder =
			    new NotificationCompat.Builder(this)
			    .setSmallIcon(R.drawable.ic_launcher)
			    .setContentTitle("Anti Theft")
			    .setContentText("State");
		
		//create the notification with the flag "ongoing" (can't be deleted by user)
		notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		//Initialize movement detector
		listener = new MovementDetector();
		listener.setCallbackService(this);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		//Destroy the notification
		notifManager.cancel(notifId);
	}

	/**
	 * Starts the alarm when a deliberate move is detected.
	 */
	public abstract void startAlarm();
	
	/**
	 * Increment the status of the progress bar in the notification
	 * The maximum (timeout) is the timeout specified in the setting (Settings.TIMEOUT_DEFAULT)
	 * @param toIncr, amount of time (in seconds) to add to the progress bar
	 */
	public abstract void incrProgressBar(int toIncr);
	
	/**
	 * Reset the progress bar to zero
	 */
	public abstract void resetProgressBar();
}
