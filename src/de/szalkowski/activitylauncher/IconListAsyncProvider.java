package de.szalkowski.activitylauncher;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class IconListAsyncProvider extends
		AsyncTask<Void, Integer, IconListAdapter> {
	
	public interface Listener {
		public void onIconListProviderFinished(IconListAsyncProvider task, IconListAdapter adapter);
	}
	
	public class Updater {
		private IconListAsyncProvider provider;
		
		public Updater(IconListAsyncProvider provider) {
			this.provider = provider;
		}
		
		public void update(int value) {
			this.provider.publishProgress(value);
		}

		public void updateMax(int value) {
			this.provider.max = value;
		}
	}
	
	protected Context context;
	protected Listener listener;
	protected int max;
	protected ProgressDialog progress;

	public IconListAsyncProvider(Context context, Listener listener) {
		this.context = context;
		this.listener = listener;
		this.progress = new ProgressDialog(context);
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		if(values.length > 0) {
			int value = values[0];
			
			if(value == 0) {
				this.progress.setIndeterminate(false);
				this.progress.setMax(this.max);
			}
			
			this.progress.setProgress(value);
		}
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		this.progress.setCancelable(false);
		this.progress.setMessage(context.getText(R.string.dialog_progress_loading));
		this.progress.setIndeterminate(true);
		this.progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		this.progress.show();
	}
	
	@Override
	protected void onPostExecute(IconListAdapter result) {
		super.onPostExecute(result);
		if(this.listener != null) {
			this.listener.onIconListProviderFinished(this, result);
		}
		this.progress.dismiss();
	}

	@Override
	protected IconListAdapter doInBackground(Void... params) {
		IconListAdapter adapter = new IconListAdapter(this.context, new Updater(this));
		
		return adapter;
	}
}
