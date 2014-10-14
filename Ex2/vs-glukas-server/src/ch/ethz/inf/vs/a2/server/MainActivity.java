package ch.ethz.inf.vs.a2.server;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements OnClickListener {
	
	Intent serverServiceIntent;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Instantiate the service
        serverServiceIntent = new Intent(this, ServerService.class);

        //Views
        ((Button)findViewById(R.id.toggleButton)).setText(R.string.button_off);
        ((Button)findViewById(R.id.toggleButton)).setOnClickListener(this);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	stopService(serverServiceIntent);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	////
	//On Click Listener
	////
	
    
    //The unique button of the activity starts/ends the service
	@Override
	public void onClick(View v) {
    	ToggleButton tb = (ToggleButton) v; 
    	if (tb.isChecked()) {
    		this.startService(serverServiceIntent);
    		((Button)v).setText(R.string.button_on);
    	}
    	else {
    		this.stopService(serverServiceIntent);
    		((Button)v).setText(R.string.button_off);
    	}
	}
}
