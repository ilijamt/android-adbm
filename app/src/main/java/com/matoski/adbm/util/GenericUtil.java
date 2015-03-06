package com.matoski.adbm.util;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.matoski.adbm.Constants;

/**
 * Generic utilities used in the ADBM application
 * 
 * @author ilijamt
 * 
 */
public class GenericUtil {

	/**
	 * Send an intent to open the translation page
	 * 
	 * @param activity
	 */
	public static void openTranslationPage(Activity activity) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(Constants.TRANSLATION_URL));
		activity.startActivity(i);
	}

	/**
	 * Update the application locale based on the language
	 * 
	 * @param activity
	 */
	public static void updateApplicationLocale(Activity activity) {

		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(activity);

		final String language = prefs.getString(Constants.KEY_LANGUAGE,
				Constants.KEY_LANGUAGE_DEFAULT);

		GenericUtil.updateApplicationLocale(activity, language);
	}

	/**
	 * Update the application locale based on the language
	 * 
	 * @param activity
	 * @param language
	 */
	public static void updateApplicationLocale(Activity activity,
			String language) {

		Locale locale = new Locale(language.toString());
		Locale.setDefault(locale);
		Configuration configuration = new Configuration();
		configuration.locale = locale;
		activity.getBaseContext()
				.getResources()
				.updateConfiguration(
						configuration,
						activity.getBaseContext().getResources()
								.getDisplayMetrics());

	}
}
