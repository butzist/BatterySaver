package de.szalkowski.adamsbatterysaver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		for (int id : appWidgetIds) {
			updateWidget(context, appWidgetManager, id, MainService.is_running);
		}
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		if(MainService.is_running) {
			Intent service = new Intent(context,MainService.class);
			service.setAction(MainService.ACTION_UPDATE);
			context.startService(service);
		}
		
		super.onDeleted(context, appWidgetIds);
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
		
		if(MainService.is_running) {
			Intent service = new Intent(context,MainService.class);
			service.setAction(MainService.ACTION_UPDATE);
			context.startService(service);
		}
	}
	
	public static void updateAllWidgets(Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
	    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context.getApplicationContext(), WidgetProvider.class));
		
		for (int id : appWidgetIds) {
			updateWidget(context, appWidgetManager, id, MainService.is_running);
		}
	}
	
	public static boolean hasWidgets(Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
	    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context.getApplicationContext(), WidgetProvider.class));
		
	    return appWidgetIds.length > 0; 
	}

}
