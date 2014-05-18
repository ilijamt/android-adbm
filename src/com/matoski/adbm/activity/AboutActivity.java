package com.matoski.adbm.activity;

import java.util.Locale;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.matoski.adbm.Constants;
import com.matoski.adbm.R;

/**
 * Shows the About data for the application
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public class AboutActivity extends BaseHelpActivity {

	/**
	 * The tag used when logging with {@link Log}
	 */
	private static final String LOG_TAG = AboutActivity.class.getName();

	/*
	 * (non-Javadoc)
	 * @see com.matoski.adbm.activity.BaseHelpActivity#getResourceId()
	 */
	@Override
	protected int getResourceId() {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		String languageValue = new Locale(prefs.getString(
				Constants.KEY_LANGUAGE, Constants.KEY_LANGUAGE_DEFAULT))
				.getDisplayLanguage();
		
		int file = R.raw.about;
		
		if (languageValue.equalsIgnoreCase("mk")) {
			file = R.raw.aboutmk;
		} 
		
		Log.d(LOG_TAG, String.format("Loading resource: %d", file));
	
		return file;

	}

}
