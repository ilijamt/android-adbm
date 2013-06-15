package com.matoski.adbm.receiver;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.matoski.adbm.Constants;
import com.matoski.adbm.util.PreferenceUtil;
import com.matoski.adbm.util.ServiceUtil;

/**
 * A {@link BroadcastReceiver} that starts when we get {@link Intent#ACTION_BOOT_COMPLETED}
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	/**
	 * The tag used when logging with {@link Log}
	 */
	private static String LOG_TAG = ConnectionDetectionReceiver.class.getName();

	/**
	 * The preferences for the {@link Application}
	 */
	private SharedPreferences preferences;

	/**
	 * Should we run on boot?
	 */
	private boolean bRunOnBoot = Constants.START_ON_BOOT;

	/**
	 * How long do we delay before starting the service?
	 */
	private int iDelayStart = Constants.DELAY_START_AFTER_BOOT;

	/**
	 * How often should we check if the service is running?
	 */
	private long iRepeatTimeout = Constants.ALARM_TIMEOUT_INTERVAL;

	/*
	 * (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {

		this.preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		try {
			this.bRunOnBoot = this.preferences.getBoolean(
					Constants.KEY_START_ON_BOOT, Constants.START_ON_BOOT);
		} catch (Exception e) {
			this.bRunOnBoot = Constants.START_ON_BOOT;
		}

		Log.d(LOG_TAG, String.format("Should we start on boot: %b", bRunOnBoot));

		try {
			this.iDelayStart = Integer.parseInt(PreferenceUtil.getString(
					context, Constants.KEY_START_DELAY,
					Constants.DELAY_START_AFTER_BOOT));
		} catch (Exception e) {
			this.iDelayStart = Constants.DELAY_START_AFTER_BOOT;
		}

		Log.d(LOG_TAG, String.format(
				"We are gonna delay the startup by %d seconds", iDelayStart));

		try {
			this.iRepeatTimeout = Long.parseLong(PreferenceUtil.getString(
					context, Constants.KEY_ALARM_TIMEOUT_INTERVAL,
					Constants.ALARM_TIMEOUT_INTERVAL * 1000));
		} catch (Exception e) {
			this.iRepeatTimeout = Constants.ALARM_TIMEOUT_INTERVAL * 1000;
		}

		Log.d(LOG_TAG,
				String.format(
						"We are gonna check every %d seconds to check if the service is running",
						iRepeatTimeout));

		if (this.bRunOnBoot) {
			Log.d(LOG_TAG, "Starting the service on boot");
			ServiceUtil.start(context, this.iDelayStart, this.iRepeatTimeout);
		}

	}

}
