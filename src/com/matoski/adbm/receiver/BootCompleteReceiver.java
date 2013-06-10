package com.matoski.adbm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.matoski.adbm.Constants;
import com.matoski.adbm.util.PreferenceUtil;
import com.matoski.adbm.util.ServiceUtil;

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
			this.iDelayStart = Integer.parseInt(PreferenceUtil.getString(
					context, Constants.KEY_START_DELAY,
					Constants.DELAY_START_AFTER_BOOT));
		} catch (Exception e) {
			this.iDelayStart = Constants.DELAY_START_AFTER_BOOT;
		}

		try {
			this.iRepeatTimeout = Long.parseLong(PreferenceUtil.getString(
					context, Constants.KEY_ALARM_TIMEOUT_INTERVAL,
					Constants.ALARM_TIMEOUT_INTERVAL * 1000));
		} catch (Exception e) {
			this.iRepeatTimeout = Constants.ALARM_TIMEOUT_INTERVAL * 1000;
		}

		if (this.bRunOnBoot) {
			ServiceUtil.start(context, this.iDelayStart, this.iRepeatTimeout);
		}

	}

}
