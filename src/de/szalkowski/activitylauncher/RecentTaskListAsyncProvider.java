package de.szalkowski.activitylauncher;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

public class RecentTaskListAsyncProvider extends AsyncTask<Void, Integer, MyActivityInfo[]> {
	public interface Listener {
		public void onRecentTaskProviderFinished(RecentTaskListAsyncProvider taskProvider, MyActivityInfo[] result);
	}
	
	protected Context context;
	protected Listener listener; 
	protected int size;
	protected ProgressDialog progress;

	public RecentTaskListAsyncProvider(Context context, Listener listener) {
		this.context = context;
		this.size = 1;
		this.progress = new ProgressDialog(context);
		this.listener = listener;
	}

	@Override
	protected MyActivityInfo[] doInBackground(Void... params) {
		PackageManager pm = context.getPackageManager();
		ArrayList<MyActivityInfo> activities = new ArrayList<MyActivityInfo>();
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(200);
		this.size = tasks.size();
		this.publishProgress(0);
		
		for (int i=0; i < tasks.size(); ++i) {
			this.publishProgress(i+1);

			ActivityManager.RunningTaskInfo task = tasks.get(i);
			MyActivityInfo info = new MyActivityInfo(task.topActivity, pm);			
			activities.add(info);
		}
		
		MyActivityInfo[] activities2 = new MyActivityInfo[activities.size()];
		for(int i=0; i < activities.size(); ++i) {
			activities2[i] = activities.get(i);
		}
		
		return activities2;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		this.progress.setCancelable(false);
		this.progress.setMessage(context.getText(R.string.dialog_progress_loading));
		this.progress.setIndeterminate(true);
		this.progress.show();
	}
	
	@Override
	protected void onPostExecute(MyActivityInfo[] result) {
		super.onPostExecute(result);
		if(this.listener != null) {
			this.listener.onRecentTaskProviderFinished(this, result);
		}
		this.progress.dismiss();
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		if(values.length > 0) {
			int value = values[0]; 
			if(value == 0) {
				this.progress.setIndeterminate(false);
				this.progress.setMax(this.size);				
			}
				
			this.progress.setProgress(value);
		}
	}
}
