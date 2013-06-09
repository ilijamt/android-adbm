package com.matoski.adbm.receiver;

import com.matoski.adbm.Constants;
import com.matoski.adbm.util.ServiceUtil;

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

		try {
			this.bRunOnBoot = this.preferences.getBoolean(
					Constants.KEY_START_ON_BOOT, Constants.START_ON_BOOT);
		} catch (Exception e) {
			this.bRunOnBoot = Constants.START_ON_BOOT;
		}

		try {
			this.iDelayStart = this.preferences
					.getInt(Constants.KEY_START_DELAY,
							Constants.DELAY_START_AFTER_BOOT);
		} catch (Exception e) {
			this.iDelayStart = Constants.DELAY_START_AFTER_BOOT;
		}

		this.iDelayStart = this.preferences.getInt(Constants.KEY_START_DELAY,
				Constants.DELAY_START_AFTER_BOOT);

		try {
			this.iRepeatTimeout = this.preferences.getLong(
					Constants.KEY_ALARM_TIMEOUT_INTERVAL,
					Constants.ALARM_TIMEOUT_INTERVAL * 1000);
		} catch (Exception e) {
			this.iRepeatTimeout = Constants.ALARM_TIMEOUT_INTERVAL * 1000;
		}

		if (this.bRunOnBoot) {
			ServiceUtil.start(context, this.iDelayStart, this.iRepeatTimeout);
		}

	}

}
