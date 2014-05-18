package com.matoski.adbm.util;

import com.matoski.adbm.Constants;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

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
}
