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
import com.matoski.adbm.service.ManagerService;
import com.matoski.adbm.util.ServiceUtil;

/**
 * A {@link BroadcastReceiver} that triggers when we get {@link WifiManager#NETWORK_STATE_CHANGED_ACTION}. Used to
 * trigger actions in {@link ManagerService} to start or stop the ADB service.
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public class ConnectionDetectionReceiver extends BroadcastReceiver {

	/**
	 * The tag used when logging with {@link Log}
	 */
	private static String LOG_TAG = ConnectionDetectionReceiver.class.getName();

	/*
	 * (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
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

				switch (networkInfo.getDetailedState()) {
					case CONNECTED:

						Log.d(LOG_TAG, String.format(
								"Auto connecting to WiFi: %s",
								Boolean.toString(bAutoWiFiConnect)));

						if (bAutoWiFiConnect) {
							ServiceUtil.runServiceAction(context,
									Constants.ACTION_SERVICE_AUTO_WIFI);
						} else {
							ServiceUtil
									.runServiceAction(
											context,
											Constants.ACTION_SERVICE_UPDATE_NOTIFICATION_NETWORK_ADB);
						}

						break;

					case DISCONNECTED:
						ServiceUtil.runServiceAction(context,
								Constants.ACTION_SERVICE_ADB_STOP);

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
