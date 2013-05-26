package com.matoski.adbm;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver {

	private static final long REPEAT_TIME = 1000 * 30;
	private static final int AFTER_BOOT_TIME = 30;

	@Override
	public void onReceive(Context context, Intent intent) {

		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				new Intent(context, MyStartServiceReceiver.class),
				PendingIntent.FLAG_CANCEL_CURRENT);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND,
				(int) BootCompleteReceiver.AFTER_BOOT_TIME);

		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), BootCompleteReceiver.REPEAT_TIME,
				pendingIntent);

	}

}
