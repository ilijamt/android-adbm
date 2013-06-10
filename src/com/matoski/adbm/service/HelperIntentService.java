package com.matoski.adbm.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.matoski.adbm.Constants;
import com.matoski.adbm.activity.HelperServiceActivity;

public class HelperIntentService extends IntentService {

	private static String LOG_TAG = HelperServiceActivity.class.getName();

	public HelperIntentService() {
		super(HelperIntentService.class.getName());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	protected boolean runAction(String action) {

		Intent mServiceIntent = new Intent(getBaseContext(), ManagerService.class);
		mServiceIntent.putExtra(Constants.EXTRA_ACTION, action);
		Log.d(LOG_TAG, String.format("Running action: %s", action));
		startService(mServiceIntent);
		return true;

	}

	@Override
	protected void onHandleIntent(Intent intent) {

		final String action = intent.getStringExtra(Constants.EXTRA_ACTION);

		Log.i(LOG_TAG, String.format(
				"Request for executing action \"%s\" received.", action));

	}
}
