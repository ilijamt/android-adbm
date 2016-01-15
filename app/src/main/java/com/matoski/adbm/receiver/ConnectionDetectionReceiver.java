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
 * A {@link BroadcastReceiver} that triggers when we get {@link WifiManager#NETWORK_STATE_CHANGED_ACTION} or {@link ConnectivityManager#CONNECTIVITY_ACTION}. Used to
 * trigger actions in {@link ManagerService} to start or stop the ADB service.
 *
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public class ConnectionDetectionReceiver extends BroadcastReceiver {

    /**
     * The tag used when logging with {@link Log}
     */
    private static String LOG_TAG = ConnectionDetectionReceiver.class.getName();

    private Context myContext;

    /**
     * Get a boolean value from {@link android.content.SharedPreferences}
     *
     * @param key
     * @param defValue
     * @return
     */
    private Boolean getBoolean(String key, Boolean defValue) {
        return PreferenceManager.getDefaultSharedPreferences(myContext).getBoolean(key, defValue);
    }

    /*
     * (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        this.myContext = context;
        final String action = intent.getAction();

        final Boolean bAutoWiFiConnect = getBoolean(Constants.KEY_ADB_START_ON_KNOWN_WIFI, Constants.ADB_START_ON_KNOWN_WIFI);
        final Boolean bAutoMobileConnect = getBoolean(Constants.KEY_AUTOSTART_MOBILE, Constants.ADB_START_ON_MOBILE);
        final Boolean bAutoEthernetConnect = getBoolean(Constants.KEY_AUTOSTART_ETH, Constants.ADB_START_ON_ETHERNET);

        if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

            NetworkInfo networkInfo = intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                Log.d(LOG_TAG, String.format("Going through network state: %s",
                        networkInfo.getDetailedState().toString()));

                final NetworkInfo.DetailedState state = networkInfo.getDetailedState();

                switch (state) {
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
                        //case BLOCKED:
                        //case CAPTIVE_PORTAL_CHECK:
                    case CONNECTING:
                    case DISCONNECTING:
                    case FAILED:
                    case IDLE:
                    case OBTAINING_IPADDR:
                    case SCANNING:
                    case SUSPENDED:
                        //case VERIFYING_POOR_LINK:
                    default:
                        break;

                }

            }

        } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            final boolean isEthernet = activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET;
            final boolean isMobile = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
            final boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            final boolean noConnection = intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY);

            if ((isEthernet && bAutoEthernetConnect) || (isMobile && bAutoMobileConnect)) {
                if (isConnected) {
                    ServiceUtil.runServiceAction(context, Constants.ACTION_SERVICE_ADB_START);
                } else {
                    ServiceUtil.runServiceAction(context, Constants.ACTION_SERVICE_ADB_STOP);
                }
            } else {
                ServiceUtil.runServiceAction(context, Constants.ACTION_SERVICE_UPDATE_NOTIFICATION_NETWORK_ADB);
            }
        }

    }

}
