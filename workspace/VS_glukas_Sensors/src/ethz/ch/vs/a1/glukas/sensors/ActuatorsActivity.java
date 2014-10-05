package ethz.ch.vs.a1.glukas.sensors;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ActuatorsActivity extends Activity implements OnSeekBarChangeListener{
	
	//vibrate members
	private Vibrator vib = null; 
	private int duration = 50;
	
	//sound members
	private MediaPlayer mp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actuators);
		
		//Vibrate set up
		vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		SeekBar seekBarVibrate = (SeekBar) findViewById(R.id.seekBarVibrate);
		seekBarVibrate.setOnSeekBarChangeListener(this);
		
		//Sound set up
		initPlayer(true);
	}
	
	////
	//Vibrate methods
	////
	
	public void onClickVibrate(View v){
		vib.vibrate(duration*10);
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		duration = progress;
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {}
	
	////
	//Play methods
	////
	
	public void onClickPlay(View v){
		if (!mp.isPlaying()) {
			mp.start();
			if (mp.isLooping()) {
				((Button)v).setText(R.string.buttonRunning);
			}
		} else {
			mp.stop(); 
			try {
				mp.prepareAsync();
			} catch (IllegalStateException e) {
				// This is a demo. See Android policy on try/catch!
			} 
			((Button)v).setText(R.string.buttonPlay);
		}
	}
	
	private void initPlayer(boolean loop) {
		mp = MediaPlayer.create(this, loop ? R.raw.loop : R.raw.sound);
		mp.setVolume(1.0f, 1.0f);
		mp.setLooping(loop);
	}
	
	////
	//Menu methods
	////
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) { 
		getMenuInflater().inflate(R.menu.actuators, menu); 
		super.onCreateOptionsMenu(menu);
		if (mp.isPlaying()) 
			return false; 
		else 
			return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) { 
		switch (item.getItemId()) {
			case R.id.menu_looping: initPlayer(true);
				return true; 
			case R.id.menu_once:
				initPlayer(false);
				return true; 
			case R.id.menu_back:
				finish();
				return true; 
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
