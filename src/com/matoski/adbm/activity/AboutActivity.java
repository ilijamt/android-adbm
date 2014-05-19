package com.matoski.adbm.activity;

import android.util.Log;

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
	@SuppressWarnings("unused")
	private static final String LOG_TAG = AboutActivity.class.getName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.matoski.adbm.activity.BaseHelpActivity#getResourceId()
	 */
	@Override
	protected int getResourceId() {
		return R.raw.about;
	}

}
