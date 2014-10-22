package ch.ethz.inf.vs.a1.vs.glukas.antitheft;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import ch.ethz.inf.vs.a1.vs_glukas_antitheft.R;

/**
 * This class is meant to remove all stuff about notification from the service
 * 
 */
public class NotificationWrapper {

	// //
	// Members
	// //

	// building members, ids, ...
	private int notifId = 001;
	private NotificationManager notifManager;
	private NotificationCompat.Builder notifBuilder;
	private Notification notif;

	// string to display, registered state of progress bar
	private int stateProgressBar = 0;
	private final String titleStr = "Anti Theft";
	private final String noMoveStr = "No movement detected";
	private final String alarmArmedStr = "Alarm is armed. Tap to disable";
	private final String ringingStr = "THIEEEF!!!";

	// the service that uses the notification
	private Service service;

	/**
	 * Destroy the notification
	 */
	public void destroyNotification() {
		notifManager.cancel(notifId);
	}

	/**
	 * Create a new notification which can't be cleared and that sends a
	 * broadcast on tap
	 * 
	 * @param service
	 *            that wants to manage the notification
	 */
	public NotificationWrapper(Service service, String broadcastMessage) {
		this.service = service;
		
		// Create the builder for the notification with all features
		
		notifBuilder = new NotificationCompat.Builder(service)
				.setSmallIcon(R.drawable.ic_launcher).setContentTitle(titleStr)
				.setContentText(noMoveStr);

		// create the notification with the flag "ongoing" (can't be deleted by
		// user)
	
		notifManager = (NotificationManager) service
				.getSystemService(Service.NOTIFICATION_SERVICE);
		notif = notifBuilder.build();
		notif.flags = Notification.FLAG_ONGOING_EVENT;
		
		// set action onClick (broadcast
		Intent onClickIntent = new Intent();
		onClickIntent.setAction(broadcastMessage);
		PendingIntent pendingIntentOnClick = PendingIntent
				.getBroadcast(service, 1234, onClickIntent,
						PendingIntent.FLAG_CANCEL_CURRENT);

		notifBuilder.setContentIntent(pendingIntentOnClick);

		// notify notification
		notifManager.notify(notifId, notif);}
	

	/**
	 * Set (and create if not already) the value to the progress bar
	 * 
	 * @param toSet
	 */
	public void setNotificationInProgress(int toSet) {
		setTextNotification(alarmArmedStr);
		setProgressBar(toSet);
	}

	/**
	 * Increment (and create if not already) the value to the progress bar
	 * 
	 * @param toIncr
	 */
	public void incrNotificationInProgress(int toIncr) {
		if (toIncr == 0)
		setTextNotification(alarmArmedStr);
		incrProgressBar(toIncr);
	}

	/**
	 * Set the notification to display some nice message
	 */
	public void setNotificationRinging() {
		destroyProgressBar();
		setTextNotification(ringingStr);
	}

	/**
	 * Set the notification to display some nice message
	 */
	public void setNotificationNoMove() {
		destroyProgressBar();
		setTextNotification(noMoveStr);
	}

	/**
	 * Display s on the notification as ContentText
	 */
	private void setTextNotification(String s) {
		notifBuilder.setContentText(s);
		notif = notifBuilder.build();
		notifManager.notify(notifId, notif);
	}

	/**
	 * Destroy the progress bar from notification
	 */
	private void destroyProgressBar() {
		notifBuilder.setProgress(0, 0, false);
		notif = notifBuilder.build();
		notifManager.notify(notifId, notif);
	}

	/**
	 * Increment the progress bar by toIncr
	 */
	private void incrProgressBar(int toIncr) {
		stateProgressBar += toIncr;
		notifBuilder.setProgress(((AntiTheftServiceImpl) service).getTimeout(),
				stateProgressBar, false);
		notif = notifBuilder.build();
		notif.flags = Notification.FLAG_ONGOING_EVENT;
		notifManager.notify(notifId, notif);
	}

	/**
	 * Set the progress bar to toSet
	 */
	private void setProgressBar(int toSet) {
		stateProgressBar = toSet;
		incrProgressBar(0);
	}
}
