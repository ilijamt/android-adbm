package com.matoski.adbm;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BootCompleteReceiver extends BroadcastReceiver {

	private SharedPreferences preferences;

	private boolean bRunOnBoot = Constants.START_ON_BOOT;
	private int iDelayStart = Constants.DELAY_START_AFTER_BOOT;
	private long iRepeatTimeout = Constants.ALARM_TIMEOUT_INTERVAL;

	@Override
	public void onReceive(Context context, Intent intent) {

		this.preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		this.bRunOnBoot = this.preferences.getBoolean(
				Constants.KEY_START_ON_BOOT, Constants.START_ON_BOOT);

		this.iDelayStart = this.preferences.getInt(Constants.KEY_START_DELAY,
				Constants.DELAY_START_AFTER_BOOT);

		this.iRepeatTimeout = this.preferences.getLong(
				Constants.KEY_ALARM_TIMEOUT_INTERVAL,
				Constants.ALARM_TIMEOUT_INTERVAL * 1000);

		if (this.bRunOnBoot) {
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
					0, new Intent(context, MyStartServiceReceiver.class),
					PendingIntent.FLAG_CANCEL_CURRENT);

			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.SECOND, this.iDelayStart);

			alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), this.iRepeatTimeout,
					pendingIntent);
		}

	}

}
