package com.matoski.adbm.activity;

import com.matoski.adbm.Constants;
import com.matoski.adbm.service.ManagerService;
import com.matoski.adbm.util.ServiceUtil;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class HelperServiceActivity extends Activity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ServiceUtil.unbind(this, mConnection);
	}

	private static String LOG_TAG = HelperServiceActivity.class.getName();

	/**
	 * The {@link ManagerService} is the service used to control the application
	 * for ADB.
	 */
	private ManagerService service;

	/** Interface connection to the {@link ManagerService} service */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			service = ((ManagerService.ServiceBinder) binder).getService();

			final Intent intent = getIntent();
			final String action = intent.getStringExtra(Constants.EXTRA_ACTION);

			try {

				Log.d(LOG_TAG, String.format("Running action: %s", action));

				if (action.equals(Constants.KEY_ACTION_ADB_STOP)) {
					service.stopNetworkADB();
				} else if (action.equals(Constants.KEY_ACTION_ADB_START)) {
					service.startNetworkADB();
				} else if (action.equals(Constants.KEY_ACTION_AUTO_WIFI)) {
					service.AutoConnectionAdb();
				} else if ( action.equals(Constants.KEY_ACTION_UPDATE_NOTIFICATION)) {
					service.notificationUpdate();
				} else {
					Log.e(LOG_TAG, String.format("Invalid action: %", action));
				}

			} catch (Exception e) {
				Log.e(LOG_TAG, e.getMessage());
			}

			finish();
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		Log.d(LOG_TAG, "Helper utils activity started.");
		ServiceUtil.bind(this, mConnection);
	}
}
