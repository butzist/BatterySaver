package de.szalkowski.adamsbatterysaver;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TimePicker;
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
        
        TimePicker from_time = (TimePicker)this.findViewById(R.id.timePickerFrom);
        from_time.setIs24HourView(true);
        from_time.setCurrentHour(settings.getInt("from_hour", 0));
        from_time.setCurrentMinute(settings.getInt("from_minute", 0));
        
        TimePicker to_time = (TimePicker)this.findViewById(R.id.timePickerTo);
        to_time.setIs24HourView(true);
        to_time.setCurrentHour(settings.getInt("to_hour", 8));
        to_time.setCurrentMinute(settings.getInt("to_minute", 0));

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
        
        TimePicker from_time = (TimePicker)this.findViewById(R.id.timePickerFrom);
        editor.putInt("from_hour", from_time.getCurrentHour());
        editor.putInt("from_minute", from_time.getCurrentMinute());

        TimePicker to_time = (TimePicker)this.findViewById(R.id.timePickerTo);
        editor.putInt("to_hour", to_time.getCurrentHour());
        editor.putInt("to_minute", to_time.getCurrentMinute());
        
        editor.commit();

        super.onDestroy();
	}
    
}
