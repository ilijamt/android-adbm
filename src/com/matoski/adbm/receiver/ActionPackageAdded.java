package com.matoski.adbm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.matoski.adbm.Constants;
import com.matoski.adbm.util.ServiceUtil;

public class ActionPackageAdded extends BroadcastReceiver {

	private static final String LOG_TAG = ActionPackageAdded.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {

		final String action = intent.getAction();

		Log.i(LOG_TAG, String.format("Running action: %s", action));

		if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
			ServiceUtil.runServiceAction(context, Constants.KEY_PACKAGE_ADD);
		}

	}
}
