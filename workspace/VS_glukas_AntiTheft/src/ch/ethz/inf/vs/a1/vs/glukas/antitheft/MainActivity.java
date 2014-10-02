package ch.ethz.inf.vs.a1.vs.glukas.antitheft;

import ch.ethz.inf.vs.a1.vs_glukas_antitheft.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	
	Intent iService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Instantiate the service
        iService = new Intent(this, AntiTheftServiceImpl.class);
        
        //Views
        ((Button)findViewById(R.id.toggleButton)).setText(R.string.button_off);
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
    		((Button)v).setText(R.string.button_on);
    	}
    	else {
    		this.stopService(iService);
    		((Button)v).setText(R.string.button_off);
    	}
    }
}
