package ch.ethz.inf.vs.android.glukas.capitalize;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

/**
 * This class offers a wrapper for the logging of the 
 * interactions with the server.
 * 
 * @author hong-an
 *
 */
public class Logger implements Serializable {

	/**
	 * Id for the serialization
	 */
	private static final long serialVersionUID = -108568480851154224L;
	/**
	 * File that will contain the logs: i.e. all messages sent and received. 
	 * They should be added to the log file as they are displayed to the view.
	 */
	private File logFile;
	
	private Context appContext;

	/**
	 * Constructor
	 */
	public Logger(Context appContext) {
		this.appContext = appContext;
		ContextWrapper cw = new ContextWrapper(appContext);
		File mediaDir = cw.getDir("logs", Context.MODE_WORLD_READABLE);
		
			{
	        try{
	        	if (!mediaDir.exists()){
				   mediaDir.createNewFile();
				   mediaDir.mkdirs();

				}
	        	System.out.println(mediaDir.getAbsoluteFile() + "/"+ Utils.LOG_PATH);
	        	this.logFile = new File(mediaDir.getAbsoluteFile() + "/"+ Utils.LOG_PATH);
	        	if(this.logFile.exists()){
	        		this.logFile.delete();
	        	}
	        	this.logFile.createNewFile();
	        } catch (IOException e){
	            e.printStackTrace();
	        }
	    }
	}

	/**
	 * This logs the messages that are displayed in the view
	 * in LogCat and in the file
	 * @param msg The message
	 * @param isIncoming Whether the message is an incoming message or not.
	 */
	public void logReadyMsg(DisplayMessage msg, boolean isIncoming) {
		String time = Utils.getTime();
		String line;
		if (isIncoming) {
			line = new String("[" + time + "] [INFO] [INCOMING]: "
					+ msg.getMessage() + " from the server");
		} else {
			line = new String("[" + time + "] [INFO] [OUTGOING]: "
					+ msg.getMessage() + " from me");
		}
		this.logToFile(line);
		this.toLogCat("(DEBUG)", line);
	}

	/**
	 * This function writes the current log line to the log file
	 * @param text The line to be logged
	 */
	private void logToFile(String text) {
		try {
			// BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
					true));
			buf.append(text);
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This function sends a message to logcat
	 * @param tag The message identifier
	 * @param msg The message
	 */
	public void toLogCat(String tag, String msg) {
		Log.d(tag, msg);
	}

}
