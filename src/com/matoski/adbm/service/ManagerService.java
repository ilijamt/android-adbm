package com.matoski.adbm.service;

import java.lang.reflect.Type;
import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.matoski.adbm.AdbStateEnum;
import com.matoski.adbm.Constants;
import com.matoski.adbm.R;
import com.matoski.adbm.activity.MainActivity;
import com.matoski.adbm.interfaces.IMessageHandler;
import com.matoski.adbm.pojo.IP;
import com.matoski.adbm.pojo.Model;
import com.matoski.adbm.util.NetworkUtil;
import com.matoski.adbm.util.PreferenceUtil;

public class ManagerService extends Service {

	/**
	 * @param handler
	 *            the handler to set
	 */
	public void setHandler(IMessageHandler handler) {
		this.handler = handler;
	}

	private static String LOG_TAG = ManagerService.class.getName();

	private NotificationManager mNM;
	private final IBinder mBinder = new ServiceBinder();
	private int NOTIFICATION = R.string.service_name;

	private boolean bNetworkADBStatus = false;

	private long mADBPort = Constants.ADB_PORT;
	private boolean mShowNotification = Constants.SHOW_NOTIFICATIONS;

	private SharedPreferences preferences;

	private Gson gson;
	private Type gsonType;

	private ConnectivityManager mConnectivityManager;

	private WifiManager mWifiManager;

	private boolean ADB_START_ON_KNOWN_WIFI;

	@Override
	public void onCreate() {
		super.onCreate();

		Log.d(LOG_TAG, "Manager service created");

		this.mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		this.preferences = PreferenceManager.getDefaultSharedPreferences(this);

		this.mADBPort = Long.parseLong(PreferenceUtil.getString(
				getBaseContext(), Constants.KEY_ADB_PORT, Constants.ADB_PORT));

		this.mConnectivityManager = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		this.mWifiManager = (WifiManager) getApplicationContext()
				.getSystemService(Context.WIFI_SERVICE);

		this.ADB_START_ON_KNOWN_WIFI = this.preferences.getBoolean(
				Constants.KEY_ADB_START_ON_KNOWN_WIFI,
				Constants.ADB_START_ON_KNOWN_WIFI);

		this.mShowNotification = this.preferences.getBoolean(
				Constants.KEY_NOTIFICATIONS, Constants.SHOW_NOTIFICATIONS);

		this.gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.serializeNulls().create();

		this.gsonType = new TypeToken<ArrayList<Model>>() {
		}.getType();

		if (this.ADB_START_ON_KNOWN_WIFI && this.isValidConnectionToWiFi()) {
			this.startNetworkADB();
		}

		this.notificationUpdate();

	}

	private void removeNotification() {
		Log.d(LOG_TAG, "Removing notification");
		this.mNM.cancelAll();
	}

	private boolean showNotification() {

		Log.d(LOG_TAG, "Prepearing notification bar");

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				getApplicationContext());

		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent, 0);

		builder.setSmallIcon(R.drawable.ic_launcher);

		NetworkInfo networkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		IP ip = NetworkUtil.getLocalAddress();

		String stringADB;
		String stringIP;
		String empty = "";

		if (networkInfo.isConnected()) {

			if (this.isNetworkADBRunning()) {
				stringADB = "ADB service is running";
				stringIP = String.format(
						getResources().getString(R.string.ip_and_port),
						ip.ipv4, Long.toString(this.mADBPort));
			} else {
				stringADB = "ADB service is not running";
				stringIP = empty;
			}

		} else {
			stringADB = "ADB service is not running";
			stringIP = "No WiFi connection";
		}

		builder.setContentTitle("ADB Manager");
		builder.setContentIntent(pIntent);

		RemoteViews remoteView = new RemoteViews(getPackageName(),
				R.layout.my_notification);

		remoteView.setTextViewText(R.id.notification_title, stringADB);
		remoteView.setTextViewText(R.id.notification_text, stringIP);

		builder.setContent(remoteView);

		Notification notification = builder.build();

		notification.flags |= Notification.FLAG_NO_CLEAR;

		this.mNM.notify(NOTIFICATION, notification);

		return true;
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "Destroying Manager Service");
		super.onDestroy();
		this.mNM.cancelAll();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(LOG_TAG, "Service bound to "
				+ intent.getComponent().getClassName());
		return this.mBinder;
	}

	@Override
	public void onRebind(Intent intent) {
		Log.d(LOG_TAG, "Service rebound to "
				+ intent.getComponent().getClassName());
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(LOG_TAG, "Service unbound from "
				+ intent.getComponent().getClassName());
		return super.onUnbind(intent);
	}

	public boolean isNetworkADBRunning() {
		Log.i(LOG_TAG,
				"Is network ADB running? "
						+ Boolean.toString(this.bNetworkADBStatus));
		return this.bNetworkADBStatus;
	}

	public void notificationUpdate() {
		this.mShowNotification = this.preferences.getBoolean(
				Constants.KEY_NOTIFICATIONS, Constants.SHOW_NOTIFICATIONS);
		this.notificationUpdate(this.mShowNotification);
	}

	public void notificationUpdate(boolean update) {

		Log.i(LOG_TAG,
				"Triggered notification update: "
						+ Boolean.toString(this.mShowNotification));

		this.addItem("Triggered notification update: "
				+ Boolean.toString(this.mShowNotification));

		if (update) {
			this.showNotification();
		} else {
			this.removeNotification();
		}

	}

	public AdbStateEnum startNetworkADB() {
		Log.i(LOG_TAG, "Starting network ADB.");

		NetworkInfo networkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (!networkInfo.isConnected()) {
			this.addItem("No WiFi connection available");
			return AdbStateEnum.NOT_ACTIVE;
		}

		this.bNetworkADBStatus = true;
		this.notificationUpdate();
		return this.bNetworkADBStatus ? AdbStateEnum.ACTIVE
				: AdbStateEnum.NOT_ACTIVE;
	}

	public AdbStateEnum stopNetworkADB() {
		Log.i(LOG_TAG, "Stopping network ADB.");
		this.bNetworkADBStatus = false;
		this.notificationUpdate();
		return this.bNetworkADBStatus ? AdbStateEnum.ACTIVE
				: AdbStateEnum.NOT_ACTIVE;
	}

	public void AutoConnectionAdb() {

		if (this.isValidConnectionToWiFi()) {
			this.startNetworkADB();
		}

	}

	/**
	 * Checks if the connection is valid, to be used for auto ADB initialization
	 * 
	 * @return {@link Boolean}
	 */
	public boolean isValidConnectionToWiFi() {

		this.addItem("Checking if we have a valid connection to the auto connect list for WiFi");
		Log.d(LOG_TAG, "Do we have a walid connection for WiFi?");

		ArrayList<Model> objects = gson.fromJson(
				this.preferences.getString(Constants.KEY_WIFI_LIST, null),
				gsonType);

		if (null == objects) {
			this.addItem("WiFi auto connect list is empty");
			Log.d(LOG_TAG, "The WiFi auto connect list is empty");
			return false;
		}

		String SSID = null;

		NetworkInfo networkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (networkInfo.isConnected()) {
			final WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
			if (connectionInfo != null && connectionInfo.getSSID() != null) {
				SSID = connectionInfo.getSSID();
				Log.d(LOG_TAG, "Connection SSID: " + SSID);
			}
		}

		if (SSID == null) {
			this.addItem("Couldn't retrieve the SSID for the WiFi network.");
			Log.w(LOG_TAG, "Couldn't retrieve the SSID for the WiFi network");
			return false;
		}

		if (objects.contains(new Model(SSID))) {
			this.addItem(SSID + " WiFi network is in the auto connect list.");
			Log.d(LOG_TAG, SSID + " WiFi network is in the auto connect list.");
			return true;
		}

		this.addItem(SSID + " WiFi network is not in the auto connect list.");
		Log.d(LOG_TAG, SSID + " WiFi network is not in the auto connect list.");

		return false;
	}

	/**
	 * Service binder for the Service
	 * 
	 * @author Ilija Matoski (ilijamt@gmail.com)
	 */
	public class ServiceBinder extends Binder {

		/**
		 * Get's the currently instantiated service
		 * 
		 * @return {@link ManagerService}
		 */
		public ManagerService getService() {
			return ManagerService.this;
		}

	}

	/**
	 * The handler for messaging
	 */
	private IMessageHandler handler = null;

	/**
	 * Add a message to the list queue
	 * 
	 * @param message
	 */
	private void addItem(String message) {
		if (handler != null) {
			handler.message(message);
		}
	}

}
