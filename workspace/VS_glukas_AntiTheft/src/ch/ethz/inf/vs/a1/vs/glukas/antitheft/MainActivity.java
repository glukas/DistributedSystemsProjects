package ch.ethz.inf.vs.a1.vs.glukas.antitheft;

import ch.ethz.inf.vs.a1.vs_glukas_antitheft.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements OnSeekBarChangeListener {
	
	Intent iService;
	SharedPreferences preferences;
	TextView timeoutValueView;
	Boolean needed = true;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Instantiate the service
        iService = new Intent(this, AntiTheftServiceImpl.class);
        
        //Views
        ((Button)findViewById(R.id.toggleButton)).setText(R.string.button_off);
        
        //get a reference to the settings
        preferences = this.getSharedPreferences(Settings.SETTINGS_FILENAME, MODE_PRIVATE);
        
        //Set connection to seek bar
        SeekBar seekBar = (SeekBar) findViewById(R.id.timeoutBar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(timeoutToSeekBarProgress(Settings.MAX_TIMEOUT));
        seekBar.setProgress(timeoutToSeekBarProgress(getTimeout()));
        
        //set text view to show current timeout value
        timeoutValueView = (TextView) findViewById(R.id.timeoutValue);
        timeoutValueView.setText(textForTimeout(getTimeout()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    //The unique button of the activity starts/ends the service
    public void onClickActive(View v){
    	ToggleButton tb = (ToggleButton) v; 
    	if (tb.isChecked()) {
    		this.startService(iService);
    		needed = false;
    		((Button)v).setText(R.string.button_on);
    	}
    	else {
    		this.stopService(iService);
    		needed = true;
    		((Button)v).setText(R.string.button_off);
    	}
    }
    
    private int timeoutToSeekBarProgress(int timeout) {
    	return timeout-Settings.MIN_TIMEOUT;
    }
    
    private int getTimeout() {
    	assert(preferences != null);
    	return preferences.getInt(Settings.TIMEOUT_STR, Settings.TIMEOUT_DEFAULT);
    }
    
	private int progressToTimeout(int progress) {
		return progress+Settings.MIN_TIMEOUT;
	}
	
	private String textForTimeout(int timeout) {
		return String.format("%s : %d", this.getString(R.string.timeout), timeout);
	}
    
    ////
    //OnSeekBarChange interface
    ////

	@Override
	protected void onResume() {
		super.onResume();
		if (iService != null){
			if (!needed)
			finish();
	        this.stopService(iService);
	        
			//finish();
		}
		
	}	
	
	@Override
	protected void onPause() {
		super.onPause();
		
		
	}	
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (timeoutValueView != null) {
			timeoutValueView.setText(textForTimeout(progressToTimeout(progress)));
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		//calculate new timeout
		int current = seekBar.getProgress();
		int newTimeout = Settings.MIN_TIMEOUT + current;
		//store in preferences
		Editor preferencesEditor = preferences.edit();
		preferencesEditor.putInt(Settings.TIMEOUT_STR, newTimeout);
		preferencesEditor.apply();
	}
}
