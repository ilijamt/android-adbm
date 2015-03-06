package com.matoski.adbm.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.matoski.adbm.Constants;
import com.matoski.adbm.service.ManagerService;
import com.matoski.adbm.util.ArrayUtils;
import com.matoski.adbm.util.ServiceUtil;

/**
 * Used to update the widget of it's status, it notifies {@link ManagerService} if there is an update and updates the
 * widget with the new data.
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public class ControlWidgetProvider extends AppWidgetProvider {

	/**
	 * The tag used when logging with {@link Log}
	 */
	private static final String LOG_TAG = ControlWidgetProvider.class.getName();

	/*
	 * (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onDeleted(android.content.Context, int[])
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		Log.i(LOG_TAG, "Widget has been deleted");
	}

	/*
	 * (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onEnabled(android.content.Context)
	 */
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.i(LOG_TAG, "Widget has been enabled");
	}

	/*
	 * (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onDisabled(android.content.Context)
	 */
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Log.i(LOG_TAG, "Widget has been disabled");
	}

	/*
	 * (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onUpdate(android.content.Context, android.appwidget.AppWidgetManager,
	 * int[])
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		ComponentName widget = new ComponentName(context,
				ControlWidgetProvider.class);

		int[] allWidgetsIds = appWidgetManager.getAppWidgetIds(widget);

		Log.d(LOG_TAG,
				String.format("Widget has been updated: %s",
						ArrayUtils.join(allWidgetsIds, ",")));

		Bundle bundle = new Bundle();
		bundle.putIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetsIds);

		ServiceUtil.runServiceAction(context,
				Constants.ACTION_SERVICE_UPDATE_WIDGETS, bundle);

	}
}
