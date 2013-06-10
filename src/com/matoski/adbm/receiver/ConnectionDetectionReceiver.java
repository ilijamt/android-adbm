package com.matoski.adbm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.matoski.adbm.Constants;
import com.matoski.adbm.service.HelperIntentService;

public class ConnectionDetectionReceiver extends BroadcastReceiver {

	private static String LOG_TAG = ConnectionDetectionReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {

		final String action = intent.getAction();
		final Boolean bAutoWiFiConnect;

		if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

			bAutoWiFiConnect = PreferenceManager.getDefaultSharedPreferences(
					context).getBoolean(Constants.KEY_ADB_START_ON_KNOWN_WIFI,
					Constants.ADB_START_ON_KNOWN_WIFI);

			NetworkInfo networkInfo = intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

				Log.d(LOG_TAG, String.format("Going through network state: %s",
						networkInfo.getDetailedState().toString()));

				Intent mHelperIntent = null;

				switch (networkInfo.getDetailedState()) {
				case CONNECTED:

					Log.d(LOG_TAG, String.format("Auto connecting to WiFi: %s",
							Boolean.toString(bAutoWiFiConnect)));

					if (bAutoWiFiConnect) {

						mHelperIntent = new Intent(context,
								HelperIntentService.class);

						mHelperIntent.putExtra(Constants.EXTRA_ACTION,
								Constants.KEY_ACTION_AUTO_WIFI);

						context.startService(mHelperIntent);
						
					} else {

						mHelperIntent = new Intent(context,
								HelperIntentService.class);

						mHelperIntent.putExtra(Constants.EXTRA_ACTION,
								Constants.KEY_ACTION_UPDATE_NOTIFICATION);

						context.startService(mHelperIntent);

					}

					break;

				case DISCONNECTED:

					mHelperIntent = new Intent(context,
							HelperIntentService.class);

					mHelperIntent.putExtra(Constants.EXTRA_ACTION,
							Constants.KEY_ACTION_ADB_STOP);

					context.startService(mHelperIntent);

					break;

				case AUTHENTICATING:
				case BLOCKED:
				case CAPTIVE_PORTAL_CHECK:
				case CONNECTING:
				case DISCONNECTING:
				case FAILED:
				case IDLE:
				case OBTAINING_IPADDR:
				case SCANNING:
				case SUSPENDED:
				case VERIFYING_POOR_LINK:
				default:
					break;

				}

			}

		}

	}
}
