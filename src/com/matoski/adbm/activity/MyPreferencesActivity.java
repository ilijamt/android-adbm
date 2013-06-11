package com.matoski.adbm.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.matoski.adbm.Constants;
import com.matoski.adbm.R;
import com.matoski.adbm.service.ManagerService;
import com.matoski.adbm.util.ServiceUtil;

public class MyPreferencesActivity extends PreferenceActivity {

	protected void restartActivity() {
		finish();
		startActivity(getIntent());
	}

	/**
	 * The {@link ManagerService} is the service used to control the application
	 * for ADB.
	 */
	private ManagerService service;

	/** Interface connection to the {@link ManagerService} service */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			service = ((ManagerService.ServiceBinder) binder).getService();
		}

	};

	/**
	 * Do bind service.
	 */
	private void doBindService() {
		ServiceUtil.bind(this, mConnection);
	}

	@Override
	protected void onDestroy() {
		this.doUnBindService();
		super.onDestroy();
	}

	private void doUnBindService() {
		ServiceUtil.unbind(this, mConnection);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		final Context context = this;

		findPreference(Constants.KEY_NOTIFICATIONS)
				.setOnPreferenceChangeListener(
						new OnPreferenceChangeListener() {

							@Override
							public boolean onPreferenceChange(
									Preference preference, Object newValue) {
								if (service != null) {
									service.notificationUpdate((Boolean) newValue);
								}
								return true;
							}

						});

		findPreference(Constants.KEY_HIDEABLE_NOTIFICATION_BAR)
				.setOnPreferenceChangeListener(
						new OnPreferenceChangeListener() {

							@Override
							public boolean onPreferenceChange(
									Preference preference, Object newValue) {
								if (service != null) {
									service.notificationUpdate();
								}
								return true;
							}

						});

		findPreference("wifi_button").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						startActivity(new Intent(getBaseContext(),
								ListViewCheckboxesActivity.class));
						return false;
					}
				});

		findPreference("reset_button").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {

						AlertDialog.Builder builder = new AlertDialog.Builder(
								context);
						builder.setCancelable(true);
						builder.setTitle(R.string.settings_reset_button_dialog_title);
						builder.setInverseBackgroundForced(true);
						builder.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										prefs.edit().clear().commit();
										restartActivity();
										dialog.dismiss();
									}
								});
						builder.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});

						builder.setMessage(R.string.settings_reset_button_dialog_message);

						AlertDialog dialog = builder.create();
						dialog.show();

						return false;
					}
				});

		findPreference(Constants.KEY_START_DELAY)
				.setOnPreferenceChangeListener(
						new OnPreferenceChangeListener() {

							@Override
							public boolean onPreferenceChange(
									Preference preference, Object newValue) {
								preference.setSummary(String
										.format(getResources()
												.getString(
														R.string.service_general_start_delay_summary),
												newValue.toString().concat(
														" seconds")));
								return true;
							}

						});

		findPreference(Constants.KEY_ALARM_TIMEOUT_INTERVAL)
				.setOnPreferenceChangeListener(
						new OnPreferenceChangeListener() {

							@Override
							public boolean onPreferenceChange(
									Preference preference, Object newValue) {
								preference.setSummary(String
										.format(getResources()
												.getString(
														R.string.service_general_alarm_timeout_summary),
												newValue.toString().concat(
														" seconds")));
								return true;
							}

						});

		String currentPort = prefs.getString(Constants.KEY_ADB_PORT,
				Long.toString(Constants.ADB_PORT));

		findPreference(Constants.KEY_ADB_PORT).setSummary(
				String.format(
						getResources().getString(
								R.string.service_settings_adb_port_summary),
						Long.parseLong(currentPort)));

		findPreference(Constants.KEY_ADB_PORT).setOnPreferenceChangeListener(
				new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						preference.setSummary(String
								.format(getResources()
										.getString(
												R.string.service_settings_adb_port_summary),
										Integer.parseInt(newValue.toString())));
						return true;
					}

				});

		doBindService();

	}
}
