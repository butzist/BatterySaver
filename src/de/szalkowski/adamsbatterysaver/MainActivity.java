package de.szalkowski.adamsbatterysaver;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
        
        EditText minutes = (EditText)this.findViewById(R.id.editText1);
        minutes.setText(Integer.toString(settings.getInt("minutes", 15)));
        
        EditText from_time = (EditText)this.findViewById(R.id.editText2);
        from_time.setText(settings.getString("from_time", "24:00"));
        
        EditText to_time = (EditText)this.findViewById(R.id.editText3);
        to_time.setText(settings.getString("to_time", "8:00"));

        ToggleButton toggle = (ToggleButton)this.findViewById(R.id.toggleButton);
        boolean start_service = settings.getBoolean("start_service", true);
        toggle.setChecked(MainService.is_running);
        toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent service = new Intent(MainActivity.this,MainService.class);
				if(isChecked) {
					MainActivity.this.startService(service);
				} else {
					MainActivity.this.stopService(service);
				}
			}
		});
        
        toggle.setChecked(start_service);
        if(start_service != MainService.is_running) {
        	String text = "service was " + (start_service ? "not " : "") + "running";
        	Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	protected void onDestroy() {
        SharedPreferences settings = this.getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        
        ToggleButton toggle = (ToggleButton)this.findViewById(R.id.toggleButton);
        editor.putBoolean("start_service", toggle.isChecked());
        
        EditText minutes = (EditText)this.findViewById(R.id.editText1);
        editor.putInt("minutes", Integer.parseInt(minutes.getText().toString()));
        
        EditText from_time = (EditText)this.findViewById(R.id.editText2);
        editor.putString("from_time", from_time.getText().toString());
        
        EditText to_time = (EditText)this.findViewById(R.id.editText3);
        editor.putString("to_time", to_time.getText().toString());
        
        editor.commit();

        super.onDestroy();
	}
    
}
