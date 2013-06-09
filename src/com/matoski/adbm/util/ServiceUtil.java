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
import android.util.Log;

import com.matoski.adbm.receiver.MyStartServiceReceiver;
import com.matoski.adbm.service.ManagerService;

public class ServiceUtil {

	private static String LOG_TAG = ServiceUtil.class.getName();

	public static boolean bind(Context context, ServiceConnection mConnection) {
		return context.bindService(new Intent(context, ManagerService.class),
				mConnection, Context.BIND_AUTO_CREATE);
	}

	public static boolean unbind(Context context, ServiceConnection mConnection) {

		if (mConnection == null) {
			return false;
		}

		context.unbindService(mConnection);
		return true;

	}

	public static void stop(Context context) {
		context.stopService(new Intent(context, ManagerService.class));
	}

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

	public static boolean isServiceRunning(Context context) {
		return ServiceUtil
				.isServiceRunning(context, ManagerService.class.getName());
	}

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

}
