package com.matoski.adbm.util;

import java.util.Calendar;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.util.Log;

import com.matoski.adbm.Constants;
import com.matoski.adbm.receiver.MyStartServiceReceiver;
import com.matoski.adbm.service.ManagerService;

/**
 * Collection of utilities to help with running {@link ManagerService}
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public class ServiceUtil {

	/**
	 * The tag used when logging with {@link Log}
	 */
	private static String LOG_TAG = ServiceUtil.class.getName();

	/**
	 * Bind the service to a {@link ServiceConnection}, which implements the connection to the {@link ManagerService}
	 * 
	 * @param context
	 *            The context to which we bind the service
	 * @param mConnection
	 *            The service connection that is used to communicate with the service
	 * @return true, if successful
	 */
	public static boolean bind(Context context, ServiceConnection mConnection) {
		final Intent intent = new Intent(context, ManagerService.class);
		intent.setAction(Constants.ACTION_SERVICE_BIND);
		return context.bindService(intent, mConnection,
				Context.BIND_AUTO_CREATE);
	}

	/**
	 * Unbind the service to a {@link ServiceConnection}, which implements the connection to the {@link ManagerService}
	 * 
	 * @param context
	 *            The context to which we have binded the service
	 * @param mConnection
	 *            The service connection that was used to communicate with the service
	 * @return true, if successful
	 */
	public static boolean unbind(Context context, ServiceConnection mConnection) {

		if (mConnection == null) {
			return false;
		}

		context.unbindService(mConnection);
		return true;

	}

	/**
	 * Stops the running {@link ManagerService}
	 * 
	 * @param context
	 *            the context
	 */
	public static void stop(Context context) {
		context.stopService(new Intent(context, ManagerService.class));
	}

	/**
	 * Starts {@link ManagerService} if not running
	 * 
	 * @param context
	 *            The context from which we start the service
	 * @param delayStart
	 *            The delay before starting the service
	 * @param iRepeatTimeout
	 *            How often should we check if the service is running
	 */
	public static void start(Context context, int delayStart,
			long iRepeatTimeout) {

		Log.i(LOG_TAG, "Starting the service");

		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				new Intent(context, MyStartServiceReceiver.class),
				PendingIntent.FLAG_CANCEL_CURRENT);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, delayStart);

		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), iRepeatTimeout, pendingIntent);

	}

	/**
	 * Checks if is service {@link ManagerService} running?
	 * 
	 * @param context
	 *            The context which we use to check the service
	 * @return true, if is service running
	 */
	public static boolean isServiceRunning(Context context) {
		return ServiceUtil.isServiceRunning(context,
				ManagerService.class.getName());
	}

	/**
	 * Checks if is service defined by the "serviceClassName" is running?
	 * 
	 * @param context
	 *            The context which we use to check the service
	 * 
	 * @param serviceClassName
	 *            Service class name to check
	 * 
	 * @return true, if is service running
	 */
	public static boolean isServiceRunning(Context context,
			String serviceClassName) {

		final ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		final List<RunningServiceInfo> services = activityManager
				.getRunningServices(Integer.MAX_VALUE);

		for (RunningServiceInfo runningServiceInfo : services) {
			if (runningServiceInfo.service.getClassName().equals(
					serviceClassName)) {
				Log.i(LOG_TAG, "Service is running");
				return true;
			}
		}

		Log.i(LOG_TAG, "Service is not running");
		return false;
	}

	/**
	 * Run service action.
	 * 
	 * @param context
	 *            Application context
	 * 
	 * @param action
	 *            The action to run
	 */
	public static void runServiceAction(Context context, String action) {
		ServiceUtil.runServiceAction(context, action, null);
	}

	/**
	 * Run service action.
	 * 
	 * @param context
	 *            Application context
	 * 
	 * @param action
	 *            The action to run
	 * 
	 * @param bundle
	 *            Bundle with extra data to add to the {@link Intent} before running the service action
	 */
	public static void runServiceAction(Context context, String action,
			Bundle bundle) {
		Intent mServiceIntent = new Intent(context, ManagerService.class);
		mServiceIntent.putExtra(Constants.EXTRA_ACTION, action);
		if (bundle != null) {
			mServiceIntent.putExtras(bundle);
		}
		Log.d(LOG_TAG, String.format("Running action: %s", action));
		context.startService(mServiceIntent);
	}

}
