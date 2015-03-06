package com.matoski.adbm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.matoski.adbm.Constants;
import com.matoski.adbm.service.ManagerService;
import com.matoski.adbm.util.ServiceUtil;

/**
 * A {@link BroadcastReceiver} that triggers when we get {@link Intent#ACTION_PACKAGE_ADDED}. Used to trigger a screen
 * wake up by calling {@link ManagerService#wakeUpPhone()} through the service.
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public class ActionPackageAdded extends BroadcastReceiver {

	/**
	 * The tag used when logging with {@link Log}
	 */
	private static final String LOG_TAG = ActionPackageAdded.class.getName();

	/*
	 * (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {

		final String action = intent.getAction();

		Log.i(LOG_TAG, String.format("Running action: %s", action));

		if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
			ServiceUtil.runServiceAction(context,
					Constants.SERVICE_ACTION_PACKAGE_ADD);
		}

	}
}
