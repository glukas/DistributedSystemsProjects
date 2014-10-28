package ch.ethz.inf.vs.android.glukas.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogFactory {
	
	/**
	 * Create a dialog
	 * @param text of the dialog
	 * @param title of the dialog
	 * @param context where to display the dialog
	 * @return AlertDialog to display
	 */
	public static AlertDialog createDialogMessage(String text, String title, Context context){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(text)
		       .setTitle(title);
		builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
	        }
		});
		AlertDialog dialog = builder.create();
		return dialog;
	}
	
	/**
	 * Create a dialog non erasables
	 * @param text of the dialog
	 * @param title of the dialog
	 * @param context where to display the dialog
	 * @return AlertDialog to display
	 */
	public static AlertDialog createDialogNonErasable(String text, String title, Context context){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(text)
		       .setTitle(title);
		AlertDialog dialog = builder.create();
		return dialog;
	}
}
