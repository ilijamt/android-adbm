package com.matoski.adbm.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import com.matoski.adbm.R;
import com.matoski.adbm.util.FileUtil;

public abstract class BaseHelpActivity extends Activity {

	private static final String LOG_TAG = BaseHelpActivity.class.getName();

	protected abstract int getResourceId();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_help);

		TextView versionView = (TextView) findViewById(R.id.help_about_version);
		versionView.setText(getVersion());

		String html = FileUtil.readRawTextFile(getApplicationContext(),
				getResourceId());

		if (html == null) {
			html = getResources().getString(R.string.no_data);
		}

		final WebView aboutText = (WebView) findViewById(R.id.help_about_text);

		aboutText.setBackgroundColor(Color.argb(1, 0, 0, 0));
		aboutText.loadData(html, "text/html", "UTF-8");

	}

	/**
	 * Get the current package version.
	 * 
	 * @return The current version.
	 */
	private String getVersion() {
		String result = "";
		try {
			PackageManager manager = getPackageManager();
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);

			result = String.format("%s (%s)", info.versionName,
					info.versionCode);
		} catch (NameNotFoundException e) {
			Log.w(LOG_TAG,
					"Unable to get application version: " + e.getMessage());
			result = "Unable to get application version.";
		}

		return result;
	}

}
