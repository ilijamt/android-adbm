package com.matoski.adbm.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.matoski.adbm.Constants;
import com.matoski.adbm.util.ArrayUtils;
import com.matoski.adbm.util.ServiceUtil;

public class ControlWidgetProvider extends AppWidgetProvider {

	private static final String LOG_TAG = ControlWidgetProvider.class.getName();

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		Log.i(LOG_TAG, "Widget has been deleted");
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.i(LOG_TAG, "Widget has been enabled");
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Log.i(LOG_TAG, "Widget has been disabled");
	}

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

		ServiceUtil.runServiceAction(context, Constants.KEY_UPDATE_WIDGETS,
				bundle);

	}
}
