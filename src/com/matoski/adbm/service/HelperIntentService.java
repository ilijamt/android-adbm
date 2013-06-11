package com.matoski.adbm.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.matoski.adbm.Constants;
import com.matoski.adbm.util.ServiceUtil;

public class HelperIntentService extends IntentService {

	private static final String LOG_TAG = HelperIntentService.class.getName();
	
	public static final String EXECUTE_NETWORK_ADB_INFO = "network_adb_info";
	public static final String EXECUTE_START_ADB = "start_adb";
	public static final String EXECUTE_STOP_ADB = "stop_adb";
	

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

	@Override
	protected void onHandleIntent(Intent intent) {

		try {

			final String action = intent.getStringExtra(Constants.EXTRA_ACTION);
			final Bundle bundle = new Bundle();
			
			if ( action.equals(EXECUTE_NETWORK_ADB_INFO) ) {
				
			} else if ( action.equals(EXECUTE_START_ADB) ) {
				
			} else if ( action.equals(EXECUTE_STOP_ADB) ) {
				
			}
			
			ServiceUtil.runServiceAction(getBaseContext(), Constants.KEY_ACTION_UPDATE_NOTIFICATION, bundle);
			

		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}

	}
}
