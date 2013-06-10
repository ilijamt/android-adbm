package com.matoski.adbm.service;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.matoski.adbm.Constants;
import com.matoski.adbm.activity.HelperServiceActivity;
import com.matoski.adbm.util.ServiceUtil;

public class HelperIntentService extends IntentService {

	private static String LOG_TAG = HelperServiceActivity.class.getName();

	protected Object bLock = new Object();

	public HelperIntentService() {
		super(HelperIntentService.class.getName());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ServiceUtil.unbind(getBaseContext(), mConnection);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		ServiceUtil.bind(getBaseContext(), mConnection);
	}

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

			// String[] queue = HelperIntentService.Queue
			// .toArray(new String[HelperIntentService.Queue.size()]);
			// HelperIntentService.Queue.clear();
			//
			// for (String action : queue) {
			// runAction(service, action);
			// }
			//
			// ServiceUtil.unbind(getBaseContext(), mConnection);

		}

	};

	protected boolean runAction(ManagerService service, String action) {

		if (service == null) {
			return false;
		}

		Log.d(LOG_TAG, String.format("Running action: %s", action));

		if (action.equals(Constants.KEY_ACTION_ADB_STOP)) {
			service.stopNetworkADB();
		} else if (action.equals(Constants.KEY_ACTION_ADB_START)) {
			service.startNetworkADB();
		} else if (action.equals(Constants.KEY_ACTION_AUTO_WIFI)) {
			service.AutoConnectionAdb();
		} else if (action.equals(Constants.KEY_ACTION_UPDATE_NOTIFICATION)) {
			service.notificationUpdate();
		} else if (action.equals(Constants.KEY_ACTION_ADB_TOGGLE)) {
			service.toggleADB();
		} else {
			Log.e(LOG_TAG, String.format("Invalid action: %", action));
			return false;
		}

		return true;

	}

	@Override
	protected void onHandleIntent(Intent intent) {

		final String action = intent.getStringExtra(Constants.EXTRA_ACTION);

		Log.i(LOG_TAG, String.format(
				"Request for executing action \"%s\" received.", action));

		try {
			while (service == null) {
				bLock.wait(100);
			}
			runAction(service, action);
		} catch (InterruptedException e) {
			Log.e(LOG_TAG, e.getMessage());
		}

		// HelperIntentService.Queue.add(action);
		// ServiceUtil.bind(getBaseContext(), mConnection);

		// synchronized (lock) {
		//
		// try {
		// lock.wait();

		// try {
		// Log.d(LOG_TAG, String.format("Running action: %s", action));
		//
		// if (service == null) {
		// Log.e(LOG_TAG, "Service is not up and running");
		// } else {
		//
		// if (action.equals(Constants.KEY_ACTION_ADB_STOP)) {
		// service.stopNetworkADB();
		// } else if (action.equals(Constants.KEY_ACTION_ADB_START)) {
		// service.startNetworkADB();
		// } else if (action.equals(Constants.KEY_ACTION_AUTO_WIFI)) {
		// service.AutoConnectionAdb();
		// } else if (action
		// .equals(Constants.KEY_ACTION_UPDATE_NOTIFICATION)) {
		// service.notificationUpdate();
		// } else if (action.equals(Constants.KEY_ACTION_ADB_TOGGLE)) {
		// service.toggleADB();
		// } else {
		// Log.e(LOG_TAG, String.format("Invalid action: %", action));
		// }
		// }
		// } catch (Exception e) {
		// Log.e(LOG_TAG, e.getMessage());
		// }

		// } catch (InterruptedException ie) {
		// Log.d(LOG_TAG, ie.getMessage());
		// }
		// }

	}
}
