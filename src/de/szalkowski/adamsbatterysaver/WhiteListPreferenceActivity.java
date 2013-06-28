package de.szalkowski.adamsbatterysaver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

public class WhiteListPreferenceActivity extends PreferenceActivity {
	protected PreferenceScreen screen;
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(android.os.Build.VERSION.SDK_INT >= 11) {
			this.getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment() {
		        @Override
		        public void onCreate(final Bundle savedInstanceState)
		        {
		            super.onCreate(savedInstanceState);
		            
		            PreferenceScreen screen = this.getPreferenceManager().createPreferenceScreen(WhiteListPreferenceActivity.this);
		            fillWhiteList(screen,WhiteListPreferenceActivity.this);
		            this.setPreferenceScreen(screen);
		        }
			}).commit();
		} else {
			PreferenceScreen screen = this.getPreferenceManager().createPreferenceScreen(this);
			fillWhiteList(screen,this);
            this.setPreferenceScreen(screen);
            this.screen = screen;
		}
	}
	
	protected Set<String> getRunningTasks(Context context) {
		Set<String> packages = new HashSet<String>();
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(100);
		for (ActivityManager.RunningTaskInfo task : tasks) {
			String currentTaskPackage = task.topActivity.getPackageName();
			packages.add(currentTaskPackage);			
		}
		
		return packages;
	}
	
	protected void fillWhiteList(PreferenceScreen screen, Context context) {
		Set<String> currentWhiteList = screen.getSharedPreferences().getStringSet("wifi_whitelist", new HashSet<String>());
		PackageManager pm = this.getPackageManager();
		Set<String> runningTasks = this.getRunningTasks(context);
		
		List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES | PackageManager.GET_PERMISSIONS);
		for(PackageInfo pack : packages) {
			if(pack.applicationInfo.enabled == false) continue;
			if(pack.activities == null || pack.activities.length == 0) continue;
			
			boolean usesInternet = false;
			if(pack.requestedPermissions == null) continue;
			for(String perm : pack.requestedPermissions) {
				if (perm.equals(Manifest.permission.INTERNET)) {
					usesInternet = true;
					break;
				}
			}
			if(!usesInternet) continue;
			
			CheckBoxPreference preference = new CheckBoxPreference(context);
			preference.setPersistent(false);
			preference.setTitle(pm.getApplicationLabel(pack.applicationInfo));
			preference.setKey(pack.packageName);
			if(currentWhiteList.contains(pack.packageName)) {
				preference.setChecked(true);
			}
			
			Drawable default_icon = pm.getDefaultActivityIcon();
			Drawable icon = pm.getApplicationIcon(pack.applicationInfo);
			if(icon.getIntrinsicHeight() != default_icon.getIntrinsicHeight() || icon.getIntrinsicWidth() != default_icon.getIntrinsicWidth()) {
				icon = default_icon;
			}
			preference.setIcon(icon);
			
			if(runningTasks.contains(pack.packageName)) {
				preference.setSummary(context.getText(R.string.currently_running));
			}
			
			screen.addPreference(preference);
            this.screen = screen;
		}
	}
	
	@Override
	protected void onDestroy() {
		Set<String> newWhiteList = new HashSet<String>();
		for(int i=0; i < this.screen.getPreferenceCount(); ++i) {
			CheckBoxPreference preference = (CheckBoxPreference)this.screen.getPreference(i);
			if(preference.isChecked()) {
				newWhiteList.add(preference.getKey());
			}
		}
		SharedPreferences.Editor edit = this.screen.getSharedPreferences().edit();
		edit.putStringSet("wifi_whitelist", newWhiteList);
		edit.commit();
		
		super.onDestroy();
	}
}
