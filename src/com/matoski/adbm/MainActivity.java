package com.matoski.adbm;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.matoski.adbm.util.Network;

public class MainActivity extends Activity {

	private TextView tvADB;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.tvADB = (TextView) findViewById(R.id.adb_command);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		String adb_port = this.prefs.getString(Constants.KEY_ADB_PORT,
				Long.toString(Constants.ADB_PORT));
		String ip = Network.getLocalIpAddress();
		String string = getResources().getText(R.string.main_adb_command)
				.toString();
		this.tvADB.setText(String.format(string, ip, adb_port));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, MyPreferencesActivity.class));
			return true;
		}

		return false;
	}

	@Override
	protected void onPause() {

		String adb_port = this.prefs.getString(Constants.KEY_ADB_PORT,
				Long.toString(Constants.ADB_PORT));
		String ip = Network.getLocalIpAddress();
		String string = getResources().getText(R.string.main_adb_command)
				.toString();
		this.tvADB.setText(String.format(string, ip, adb_port));
		super.onPause();
	}
}
