package com.matoski.adbm.receiver;

import com.matoski.adbm.Constants;
import com.matoski.adbm.service.ManagerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * A {@link BroadcastReceiver} that triggers when we start the service through {@link BootCompleteReceiver}
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public class MyStartServiceReceiver extends BroadcastReceiver {

	/**
	 * The tag used when logging with {@link Log}
	 */
	private static final String LOG_TAG = MyStartServiceReceiver.class
			.getName();

	/*
	 * (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "Starting the service through the broadcast receiver.");
		Intent service = new Intent(context, ManagerService.class);
		service.setAction(Constants.ACTION_SERVICE_START);
		context.startService(service);
	}
}
