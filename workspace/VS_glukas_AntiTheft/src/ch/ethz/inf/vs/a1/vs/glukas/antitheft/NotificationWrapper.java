package ch.ethz.inf.vs.a1.vs.glukas.antitheft;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import ch.ethz.inf.vs.a1.vs_glukas_antitheft.R;

public class NotificationWrapper {
	
	////
	//Manage Notification
	////
	
	protected int notifId = 001;
	protected NotificationManager notifManager;
	protected NotificationCompat.Builder notifBuilder;
	protected Notification notif;
	protected int stateProgressBar = 0;
	protected final String titleStr = "Anti Theft";
	protected final String noMoveStr = "No movement detected";
	protected final String alarmArmedStr = "Alarm is armed. Tap to disable";
	protected final String ringingStr = "THIEEEF!!!";
	protected Service service;
	
	public void destroyNotification(){
		notifManager.cancel(notifId);
	}
	
	public NotificationWrapper(Service service){
		this.service = service;
		
		//Create the builder for the notification with all features
		notifBuilder =
			    new NotificationCompat.Builder(service)
			    .setSmallIcon(R.drawable.ic_launcher)
			    .setContentTitle(titleStr)
			    .setContentText(noMoveStr);
			
		//create the notification with the flag "ongoing" (can't be deleted by user)
		notifManager = (NotificationManager) service.getSystemService(Service.NOTIFICATION_SERVICE);
		notif = notifBuilder.build();
		notif.flags = Notification.FLAG_ONGOING_EVENT;

		//set action onClick
		Intent onClickIntent = new Intent();  
		onClickIntent.setAction(AntiTheftServiceImpl.broadcastMessage);
		PendingIntent pendingIntentOnClick = PendingIntent.getBroadcast(service, 1234, onClickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		notifBuilder.setContentIntent(pendingIntentOnClick);

		//notify notification
		notifManager.notify(notifId, notif);
	}
	
	public void setNotificationInProgress(int toSet){
		setTextNotification(alarmArmedStr);
		setProgressBar(toSet);
	}
	
	public void incrNotificationInProgress(int toIncr){
		setTextNotification(alarmArmedStr);
		incrProgressBar(toIncr);
	}
	
	public void setNotificationRinging(){
		destroyProgressBar();
		setTextNotification(ringingStr);
	}
	
	public void setNotificationNoMove(){
		destroyProgressBar();
		setTextNotification(noMoveStr);
	}
	
	private void setTextNotification(String s){
		notifBuilder.setContentText(s);
		notif = notifBuilder.build();
		notifManager.notify(notifId, notif);
	}

	private void destroyProgressBar(){
		notifBuilder.setProgress(0, 0, false);
		notif = notifBuilder.build();
		notifManager.notify(notifId, notif);
	}
		
	private void incrProgressBar(int toIncr){
		stateProgressBar += toIncr;
		notifBuilder.setProgress(((AntiTheftServiceImpl)service).getTimeout(), stateProgressBar, false);
		notif = notifBuilder.build();
		notif.flags = Notification.FLAG_ONGOING_EVENT;
		notifManager.notify(notifId, notif);
	}
		
	private void setProgressBar(int toSet) {
		stateProgressBar = toSet;
		incrProgressBar(0);	
	}
}
