package de.szalkowski.adamsbatterysaver;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void onToggleClicked(View view) {
    	ToggleButton button = (ToggleButton)view;
    		Intent service = new Intent(this,MainService.class);
    		this.startService(service);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
