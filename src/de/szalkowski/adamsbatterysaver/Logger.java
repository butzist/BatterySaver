package de.szalkowski.adamsbatterysaver;

import android.util.Log;

public class Logger {
	static final private String LOG = "de.szalkowski.adamsbatterysaver";
	
	static public void debug(String message) {
		Log.d(LOG, message);
	}

	static public void verbose(String message) {
		Log.v(LOG, message);
	}
	
	static public void warning(String message) {
		Log.w(LOG, message);
	}

	static public void error(String message) {
		Log.e(LOG, message);
	}
}
