package com.matoski.adbm.service;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.matoski.adbm.Constants;
import com.matoski.adbm.R;
import com.matoski.adbm.activity.MainActivity;
import com.matoski.adbm.enums.AdbStateEnum;
import com.matoski.adbm.interfaces.IMessageHandler;
import com.matoski.adbm.pojo.IP;
import com.matoski.adbm.pojo.Model;
import com.matoski.adbm.tasks.NetworkStatusChecker;
import com.matoski.adbm.tasks.RootCommandExecuter;
import com.matoski.adbm.util.ArrayUtils;
import com.matoski.adbm.util.NetworkUtil;
import com.matoski.adbm.util.PreferenceUtil;
import com.matoski.adbm.widgets.ControlWidgetProvider;

public class ManagerService extends Service {

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

	private static String LOG_TAG = ManagerService.class.getName();

	private NotificationManager mNM;
	private final IBinder mBinder = new ServiceBinder();
	private int NOTIFICATION = R.string.service_name;

	private boolean bNotificationHideable = Constants.SHOW_HIDEABLE_NOTIFICATIONS;
	private boolean bNetworkADBStatus = false;
	private AdbStateEnum mAdbState = AdbStateEnum.NOT_ACTIVE;

	private long mADBPort = Constants.ADB_PORT;
	private boolean mShowNotification = Constants.SHOW_NOTIFICATIONS;

	private SharedPreferences preferences;

	private Gson gson;
	private Type gsonType;

	private ConnectivityManager mConnectivityManager;

	private WifiManager mWifiManager;

	private boolean ADB_START_ON_KNOWN_WIFI;

	/**
	 * The handler for messaging
	 */
	private IMessageHandler handler = null;

	// private PowerManager mPowerManager;

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

	private void triggerBoundActivityUpdate() {
		triggerBoundActivityUpdate(mAdbState);
	}

	private void triggerBoundActivityUpdate(AdbStateEnum state) {
		if (handler != null) {
			handler.update(state);
		}
	}

	public void toggleADB() {
		(new MyToggleNetworkAdb()).execute();
	}

	public void autoConnectionAdb() {

		if (this.isValidConnectionToWiFi()) {
			this.startNetworkADB();
		}

	}

	private final class MyRootCommandExecuter extends RootCommandExecuter {

		public MyRootCommandExecuter() {
			this.mUseRoot = preferences.getBoolean(Constants.KEY_USE_ROOT,
					Constants.USE_ROOT);
			this.map = mResourcesStringMap;
		}

		@Override
		protected void onProgressUpdate(String... messages) {
			super.onProgressUpdate(messages);
			for (String message : messages) {
				addItem(message);
			}
		}

		@Override
		protected void onPostExecute(AdbStateEnum result) {
			super.onPostExecute(result);
			bNetworkADBStatus = result == AdbStateEnum.ACTIVE;
			mAdbState = result;
			notificationUpdateRemoteOnly(result == AdbStateEnum.ACTIVE);
			triggerBoundActivityUpdate(result);
		}

	}

	private final class MyToggleNetworkAdb extends NetworkStatusChecker {

		public MyToggleNetworkAdb() {
			this.mUseRoot = preferences.getBoolean(Constants.KEY_USE_ROOT,
					Constants.USE_ROOT);
			this.map = mResourcesStringMap;
		}

		@Override
		protected void onProgressUpdate(String... messages) {
			super.onProgressUpdate(messages);
			for (String message : messages) {
				addItem(message);
			}
		}

		@Override
		protected void onPostExecute(AdbStateEnum result) {
			super.onPostExecute(result);
			Log.i(LOG_TAG, "Toggling the ADB state: " + result.toString());
			bNetworkADBStatus = result == AdbStateEnum.ACTIVE;
			mAdbState = result;
			switch (result) {
			case ACTIVE:
				stopNetworkADB();
				break;

			case NOT_ACTIVE:
				startNetworkADB();
			}
		}

	}

	private final class MyNetworkStatusChecker extends NetworkStatusChecker {

		public MyNetworkStatusChecker() {
			this.mUseRoot = preferences.getBoolean(Constants.KEY_USE_ROOT,
					Constants.USE_ROOT);
			this.map = mResourcesStringMap;
		}

		@Override
		protected void onProgressUpdate(String... messages) {
			super.onProgressUpdate(messages);
			for (String message : messages) {
				addItem(message);
			}
		}

		@Override
		protected void onPostExecute(AdbStateEnum result) {
			super.onPostExecute(result);
			bNetworkADBStatus = result == AdbStateEnum.ACTIVE;
			mAdbState = result;
			notificationUpdate();
		}
	}

	public void isNetworkADBRunning() {
		(new MyNetworkStatusChecker()).execute();
	}

	/**
	 * Checks if the connection is valid, to be used for auto ADB initialization
	 * 
	 * @return {@link Boolean}
	 */
	public boolean isValidConnectionToWiFi() {

		this.addItem(getResources().getString(
				R.string.item_check_if_we_have_valid_connection));
		Log.d(LOG_TAG, "Do we have a walid connection for WiFi?");

		ArrayList<Model> objects = gson.fromJson(
				this.preferences.getString(Constants.KEY_WIFI_LIST, null),
				gsonType);

		if (null == objects) {
			this.addItem(getResources().getString(
					R.string.item_wifi_auto_connect_list_empty));
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
			this.addItem(getResources().getString(
					R.string.item_retrieve_ssid_failed));
			Log.w(LOG_TAG, "Couldn't retrieve the SSID for the WiFi network");
			return false;
		}

		if (objects.contains(new Model(SSID))) {
			this.addItem(getResources().getString(
					R.string.item_wifi_network_in_list, SSID));
			Log.d(LOG_TAG, SSID + " WiFi network is in the auto connect list.");
			return true;
		}

		this.addItem(getResources().getString(
				R.string.item_wifi_network_not_in_list, SSID));
		Log.d(LOG_TAG, SSID + " WiFi network is not in the auto connect list.");

		return false;
	}

	public void notificationUpdate() {
		this.mShowNotification = this.preferences.getBoolean(
				Constants.KEY_NOTIFICATIONS, Constants.SHOW_NOTIFICATIONS);
		this.notificationUpdate(this.mShowNotification);
	}

	public void notificationUpdate(boolean update) {
		notificationUpdate(update, bNetworkADBStatus);
	}

	public void notificationUpdateRemoteOnly(boolean isNetworkADBRunning) {

		this.mShowNotification = this.preferences.getBoolean(
				Constants.KEY_NOTIFICATIONS, Constants.SHOW_NOTIFICATIONS);

		boolean update = mShowNotification;

		this.addItem(getResources().getString(
				R.string.item_triggered_notification_update,
				Boolean.toString(update)));

		updateWidgets(isNetworkADBRunning);

		if (update) {
			showNotification(isNetworkADBRunning, true);
		} else {
			this.removeNotification();
		}

	}

	public void notificationUpdate(boolean update, boolean isNetworkADBRunning) {

		Log.i(LOG_TAG,
				"Triggered notification update: " + Boolean.toString(update));

		this.addItem(getResources().getString(
				R.string.item_triggered_notification_update,
				Boolean.toString(update)));

		updateWidgets(isNetworkADBRunning);

		if (update) {
			showNotification(isNetworkADBRunning);
		} else {
			this.removeNotification();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(LOG_TAG, "Service bound to "
				+ intent.getComponent().getClassName());
		return this.mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Log.d(LOG_TAG, "Manager service created");

		this.mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		this.preferences = PreferenceManager.getDefaultSharedPreferences(this);

		// this.mPowerManager = (PowerManager)
		// getSystemService(Context.POWER_SERVICE);
		// this.wakeLock = this.mPowerManager.newWakeLock(
		// PowerManager.PARTIAL_WAKE_LOCK, LOG_TAG);

		String resourceName;
		Integer resourceId;

		for (Field field : R.string.class.getFields()) {
			try {
				resourceId = field.getInt(R.string.class);
				resourceName = getResources().getString(resourceId);
				mResourcesStringMap.put(resourceId, resourceName);
				Log.w(LOG_TAG,
						String.format("%d = %s", resourceId, resourceName));
			} catch (Exception e) {
				Log.e(LOG_TAG, e.getMessage(), e);
			}
		}

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

		this.notificationUpdate();

		if (this.ADB_START_ON_KNOWN_WIFI && this.isValidConnectionToWiFi()) {
			this.startNetworkADB();
		}

		(new MyNetworkStatusChecker()).execute();

	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "Destroying Manager Service");
		super.onDestroy();
		this.mNM.cancelAll();
	}

	@Override
	public void onRebind(Intent intent) {
		Log.d(LOG_TAG, "Service rebound to "
				+ intent.getComponent().getClassName());
		super.onRebind(intent);
	}

	public void updateWidgets() {
		updateWidgets(bNetworkADBStatus);
	}

	public void updateWidgets(boolean isNetworkADBRunning) {

		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
				.getApplicationContext());

		ComponentName thisWidget = new ComponentName(getApplicationContext(),
				ControlWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

		updateWidgets(allWidgetIds, bNetworkADBStatus);

	}

	public void updateWidgets(int[] allWidgetsIds, boolean isNetworkADBRunning) {

		AppWidgetManager widgetManager = AppWidgetManager.getInstance(this
				.getApplicationContext());

		NetworkInfo networkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		Log.d(LOG_TAG, "Updating widgets through the service");
		Log.d(LOG_TAG, String.format("Widget has queued for been updated: %s",
				ArrayUtils.join(allWidgetsIds, ",")));

		IP ip = NetworkUtil.getLocalAddress();

		String stringADB = getResources().getString(
				R.string.status_adb_service_not_running);
		String stringIP = getResources().getString(
				R.string.status_no_wifi_connection);

		int imageViewId = R.drawable.play;

		if (networkInfo.isConnected()) {

			if (isNetworkADBRunning) {
				stringADB = getResources().getString(
						R.string.status_adb_service_running);
				stringIP = String.format(
						getResources().getString(R.string.ip_and_port),
						ip.ipv4, Long.toString(this.mADBPort));
				imageViewId = R.drawable.stop;
			} else {
				stringIP = getResources().getString(
						R.string.status_wifi_connection_available);
			}

		}

		for (int widgetId : allWidgetsIds) {

			RemoteViews remoteView = new RemoteViews(getApplicationContext()
					.getPackageName(), R.layout.control_widget);

			remoteView.setTextViewText(R.id.notification_title, stringADB);
			remoteView.setTextViewText(R.id.notification_text, stringIP);
			remoteView.setImageViewResource(R.id.notification_image,
					imageViewId);

			Intent mServiceIntent = new Intent(getApplicationContext(),
					ManagerService.class);
			mServiceIntent.putExtra(Constants.EXTRA_ACTION,
					Constants.KEY_ACTION_ADB_TOGGLE);

			PendingIntent pendingIntent = PendingIntent.getService(
					getApplicationContext(), 0, mServiceIntent, 0);

			remoteView.setOnClickPendingIntent(R.id.notification_image,
					pendingIntent);

			addItem(getResources().getString(R.string.item_widget_update,
					widgetId));
			widgetManager.updateAppWidget(widgetId, remoteView);

		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		final Bundle extras = intent.getExtras();

		String action = "No available action";

		if (extras != null) {

			try {

				action = extras.getString(Constants.EXTRA_ACTION);

				Log.d(LOG_TAG, String.format("Running action: %s", action));

				if (action.equals(Constants.KEY_ACTION_ADB_STOP)) {
					this.stopNetworkADB();
				} else if (action.equals(Constants.KEY_ACTION_ADB_START)) {
					this.startNetworkADB();
				} else if (action.equals(Constants.KEY_ACTION_AUTO_WIFI)) {
					this.autoConnectionAdb();
				} else if (action
						.equals(Constants.KEY_ACTION_UPDATE_NOTIFICATION)) {
					this.notificationUpdate();
				} else if (action.equals(Constants.KEY_ACTION_ADB_TOGGLE)) {
					this.toggleADB();
				} else if (action
						.equals(Constants.KEY_ACTION_UPDATE_NOTIFICATION_NETWORK_ADB)) {
					this.isNetworkADBRunning();
				} else if (action.equals(Constants.KEY_UPDATE_WIDGETS)) {
					this.updateWidgets(extras
							.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS),
							bNetworkADBStatus);
				} else if (action.equals(Constants.KEY_WAKELOCK_ACQUIRE)) {
					this.acquireWakeLock();
				} else if (action.equals(Constants.KEY_WAKELOCK_RELEASE)) {
					this.releaseWakeLock();
				} else {
					Log.e(LOG_TAG, String.format("Invalid action: %", action));
				}

			} catch (Exception e) {
				Log.e(LOG_TAG, e.getMessage(), e);
			}
		}

		Log.i(LOG_TAG, "onStartCommand: " + action);
		return Service.START_STICKY;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(LOG_TAG, "Service unbound from "
				+ intent.getComponent().getClassName());
		return super.onUnbind(intent);
	}

	private void removeNotification() {
		Log.d(LOG_TAG, "Removing notification");
		this.mNM.cancelAll();
		triggerBoundActivityUpdate();
	}

	/**
	 * @param handler
	 *            the handler to set
	 */
	public void setHandler(IMessageHandler handler) {
		this.handler = handler;
	}

	private void showNotification(boolean isNetworkADBRunning) {
		showNotification(isNetworkADBRunning, false);
	}

	private void showNotification(boolean isNetworkADBRunning,
			boolean dontTriggerHandlerUpdate) {

		Log.d(LOG_TAG, "Prepearing notification bar");

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				getApplicationContext());

		NetworkInfo networkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		IP ip = NetworkUtil.getLocalAddress();

		String stringADB = getResources().getString(
				R.string.status_adb_service_not_running);
		String stringIP = getResources().getString(
				R.string.status_no_wifi_connection);

		int imageViewId = R.drawable.ic_launcher;

		if (networkInfo.isConnected()) {

			if (isNetworkADBRunning) {
				stringADB = getResources().getString(
						R.string.status_adb_service_running);
				stringIP = String.format(
						getResources().getString(R.string.ip_and_port),
						ip.ipv4, Long.toString(this.mADBPort));
				imageViewId = R.drawable.ic_launcher_running;
			} else {
				stringIP = getResources().getString(
						R.string.status_wifi_connection_available);
				imageViewId = R.drawable.ic_launcher_wifi;
			}

		}

		builder.setSmallIcon(imageViewId);
		builder.setContentTitle(stringADB);
		builder.setContentText(stringIP);
		builder.setContentIntent(PendingIntent.getActivity(
				getApplicationContext(), 0,
				new Intent(this, MainActivity.class), 0));

		RemoteViews remoteView = new RemoteViews(getPackageName(),
				R.layout.my_notification);

		remoteView.setImageViewResource(R.id.notification_image, imageViewId);
		remoteView.setTextViewText(R.id.notification_title, stringADB);
		remoteView.setTextViewText(R.id.notification_text, stringIP);

		builder.setContent(remoteView);

		Notification notification = builder.build();

		bNotificationHideable = this.preferences.getBoolean(
				Constants.KEY_HIDEABLE_NOTIFICATION_BAR,
				Constants.SHOW_HIDEABLE_NOTIFICATIONS);

		if (!bNotificationHideable) {
			notification.flags |= Notification.FLAG_NO_CLEAR;
		}

		this.mNM.notify(NOTIFICATION, notification);

		if (!dontTriggerHandlerUpdate) {
			triggerBoundActivityUpdate();
		}

	}

	public void startNetworkADB() {
		Log.i(LOG_TAG, "Starting network ADB.");

		NetworkInfo networkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (networkInfo.isConnected()) {

			this.mADBPort = Long.parseLong(PreferenceUtil.getString(
					getBaseContext(), Constants.KEY_ADB_PORT,
					Constants.ADB_PORT));

			(new MyRootCommandExecuter()).execute(new String[] {
					"setprop service.adb.tcp.port "
							+ Long.toString(this.mADBPort), "stop adbd",
					"start adbd" });
		} else {
			this.addItem(getResources().getString(
					R.string.item_no_wifi_connection_available));
			this.isNetworkADBRunning();
		}
	}

	public void stopNetworkADB() {
		Log.i(LOG_TAG, "Stopping network ADB.");
		(new MyRootCommandExecuter()).execute(new String[] {
				"setprop service.adb.tcp.port -1", "stop adbd", "start adbd" });

	}

	final public SparseArray<String> mResourcesStringMap = new SparseArray<String>();

	// private PowerManager.WakeLock wakeLock;

	public void acquireWakeLock() {

		// if (!wakeLock.isHeld()) {
		// wakeLock.acquire();
		// }

	}

	public void releaseWakeLock() {

		// if (wakeLock.isHeld()) {
		// wakeLock.release();
		// }

	}

}
