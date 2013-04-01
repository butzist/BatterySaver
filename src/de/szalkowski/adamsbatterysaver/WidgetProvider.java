package de.szalkowski.adamsbatterysaver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		SharedPreferences settings = context.getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
		boolean serviceRunning = settings.getBoolean(MainActivity.SETTINGS_START_SERVICE, MainService.is_running);
		
		for (int id : appWidgetIds) {
			updateWidget(context, appWidgetManager, id, serviceRunning);
		}
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	protected static void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId, boolean serviceRunning) {
		int widget_layout;
		
		if(serviceRunning) {
			widget_layout = R.layout.widget_layout;
		} else {
			widget_layout = R.layout.widget_layout_bw;
		}
		
		RemoteViews views = new RemoteViews(context.getPackageName(), widget_layout);
		//Intent intent = new Intent(context,MainService.class);
		//if(serviceRunning) {
		//	intent.setAction(MainService.ACTION_DISABLE);
		//}
		//PendingIntent pending = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		Intent intent = new Intent(context,MainActivity.class);
		PendingIntent pending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.widgetImage, pending);
		appWidgetManager.updateAppWidget(widgetId, views);
	}
	
	public static void updateAllWidgets(Context context) {
		SharedPreferences settings = context.getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
	    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context.getApplicationContext(), WidgetProvider.class));
		boolean serviceRunning = settings.getBoolean(MainActivity.SETTINGS_START_SERVICE, MainService.is_running);
		
		for (int id : appWidgetIds) {
			updateWidget(context, appWidgetManager, id, serviceRunning);
		}
	}
	
	public static boolean hasWidgets(Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
	    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context.getApplicationContext(), WidgetProvider.class));
		
	    return appWidgetIds.length > 0; 
	}

}
