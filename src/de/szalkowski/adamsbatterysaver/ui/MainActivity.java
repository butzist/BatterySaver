package de.szalkowski.adamsbatterysaver.ui;

import org.thirdparty.AdvancedSettingsActivity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import de.szalkowski.adamsbatterysaver.R;
import de.szalkowski.adamsbatterysaver.SettingsManager;
import de.szalkowski.adamsbatterysaver.service.MainService;

public class MainActivity extends FragmentActivity {
    private SettingsManager settings;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        loadSettings();
        checkAndStartServiceIfNecessary();
		setupViewPager();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			setupActionBar();
		}
    }

	private void setupViewPager() {
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(new ActionBar.TabListener() {
						
						@Override
						public void onTabUnselected(Tab tab, FragmentTransaction ft) {
						}
						
						@Override
						public void onTabSelected(Tab tab, FragmentTransaction ft) {
							mViewPager.setCurrentItem(tab.getPosition());
						}
						
						@Override
						public void onTabReselected(Tab tab, FragmentTransaction ft) {
						}
					}));
		}		
	}

	private void checkAndStartServiceIfNecessary() {
        boolean start_service = settings.getStartService();

		if(start_service != MainService.is_running) {
        	String text;
        	if(start_service) {
        		text = this.getString(R.string.service_not_running);
        	} else {
        		text = this.getString(R.string.service_running);
        	}
        	Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_LONG).show();
        }
	}

	private void loadSettings() {
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        settings = SettingsManager.getSettingsManager(this.getApplicationContext());
	}
    
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent i1 = new Intent(this, AdvancedSettingsActivity.class);
			this.startActivity(i1);
			return true;

		case R.id.action_view_source:
			Intent i2 = new Intent(Intent.ACTION_VIEW);
			i2.setData(Uri.parse(this.getString(R.string.url_source)));
			this.startActivity(i2);
			return true;

		case R.id.action_view_translation:
			Intent i3 = new Intent(Intent.ACTION_VIEW);
			i3.setData(Uri.parse(this.getString(R.string.url_translation)));
			this.startActivity(i3);
			return true;
			
		case R.id.action_view_bugs:
			Intent i4 = new Intent(Intent.ACTION_VIEW);
			i4.setData(Uri.parse(this.getString(R.string.url_bugs)));
			this.startActivity(i4);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
