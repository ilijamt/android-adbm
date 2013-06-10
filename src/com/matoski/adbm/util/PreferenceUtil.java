package com.matoski.adbm.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class PreferenceUtil {

	private static String LOG_TAG = PreferenceUtil.class.getName();

	public static String getString(Context context, String key, String dflt) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		String value = dflt;

		try {
			value = prefs.getString(key, dflt);
			Log.i(LOG_TAG, String.format("%s = %s", key, dflt));
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
		}

		return value;

	}

	public static String getString(Context context, String key, boolean dflt) {
		return PreferenceUtil.getString(context, key, Boolean.toString(dflt));
	}

	public static String getString(Context context, String key, Long dflt) {
		return PreferenceUtil.getString(context, key, Long.toString(dflt));
	}

	public static String getString(Context context, String key, Integer dflt) {
		return PreferenceUtil.getString(context, key, Integer.toString(dflt));
	}
}
