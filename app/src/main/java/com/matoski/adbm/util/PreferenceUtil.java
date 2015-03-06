package com.matoski.adbm.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Helper class for {@link PreferenceManager}
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public class PreferenceUtil {

	/**
	 * The tag used when logging with {@link Log}
	 */
	private static String LOG_TAG = PreferenceUtil.class.getName();

	/**
	 * Gets the data for the defined key
	 * 
	 * @param context
	 *            Application/base context
	 * @param key
	 *            The key to use to get the data
	 * @param dflt
	 *            Default value of the data
	 * 
	 * @return the value of the property, if not found it will return dflt
	 */
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

	/**
	 * Gets the data for the defined key
	 * 
	 * @param context
	 *            Application/base context
	 * @param key
	 *            The key to use to get the data
	 * @param dflt
	 *            Default value of the data
	 * 
	 * @return the value of the property, if not found it will return dflt
	 */
	public static String getString(Context context, String key, boolean dflt) {
		return PreferenceUtil.getString(context, key, Boolean.toString(dflt));
	}

	/**
	 * Gets the data for the defined key
	 * 
	 * @param context
	 *            Application/base context
	 * @param key
	 *            The key to use to get the data
	 * @param dflt
	 *            Default value of the data
	 * 
	 * @return the value of the property, if not found it will return dflt
	 */
	public static String getString(Context context, String key, Long dflt) {
		return PreferenceUtil.getString(context, key, Long.toString(dflt));
	}

	/**
	 * Gets the data for the defined key
	 * 
	 * @param context
	 *            Application/base context
	 * @param key
	 *            The key to use to get the data
	 * @param dflt
	 *            Default value of the data
	 * 
	 * @return the value of the property, if not found it will return dflt
	 */
	public static String getString(Context context, String key, Integer dflt) {
		return PreferenceUtil.getString(context, key, Integer.toString(dflt));
	}
}
