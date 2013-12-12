package com.matoski.adbm.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
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
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
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

/**
 * The main component to this application, contains all the functionality to successfully interact with the ADB,
 * toggling the ADB service on or off, update available widgets that extend {@link AppWidgetProvider}, provide a way to
 * interact with {@link WakeLock} to enable us to keep screen on or off
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
@SuppressWarnings("deprecation")
public class ManagerService extends Service {

	/**
	 * A task that implements {@link NetworkStatusChecker} so we can use it to update the UI thread on the
	 * {@link MyNetworkStatusChecker#onPostExecute(AdbStateEnum)} and also update the available widgets
	 * 
	 * @author Ilija Matoski (ilijamt@gmail.com)
	 */
	private final class MyNetworkStatusChecker extends NetworkStatusChecker {

		/**
		 * Instantiates a new my network status checker.
		 */
		public MyNetworkStatusChecker() {
			this.mUseRoot = preferences.getBoolean(Constants.KEY_USE_ROOT,
					Constants.USE_ROOT);
		}

		/*
		 * (non-Javadoc)
		 * @see com.matoski.adbm.tasks.GenericAsyncTask#getString(int)
		 */
		@Override
		protected String getString(int resourceId) {
			return getResources().getString(resourceId);
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(AdbStateEnum result) {
			super.onPostExecute(result);
			bNetworkADBStatus = result == AdbStateEnum.ACTIVE;
			mAdbState = result;
			notificationUpdate();
			determineIfWeNeedWakeLock(result);
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(String... messages) {
			super.onProgressUpdate(messages);
			for (String message : messages) {
				addItem(message);
			}
		}
	}

	/**
	 * A task that implements {@link RootCommandExecuter} so we can use it to update the UI thread on the
	 * {@link MyRootCommandExecuter#onPostExecute(AdbStateEnum)} and also update the available widgets
	 * 
	 * @author Ilija Matoski (ilijamt@gmail.com)
	 */
	private final class MyRootCommandExecuter extends RootCommandExecuter {

		/**
		 * Instantiates a new my root command executer
		 */
		public MyRootCommandExecuter() {
			this.mUseRoot = preferences.getBoolean(Constants.KEY_USE_ROOT,
					Constants.USE_ROOT);
		}

		/*
		 * (non-Javadoc)
		 * @see com.matoski.adbm.tasks.GenericAsyncTask#getString(int)
		 */
		@Override
		protected String getString(int resourceId) {
			return getResources().getString(resourceId);
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(AdbStateEnum result) {
			super.onPostExecute(result);
			bNetworkADBStatus = result == AdbStateEnum.ACTIVE;
			mAdbState = result;
			notificationUpdateRemoteOnly(result == AdbStateEnum.ACTIVE);
			triggerBoundActivityUpdate(result);
			determineIfWeNeedWakeLock(result);
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(String... messages) {
			super.onProgressUpdate(messages);
			for (String message : messages) {
				addItem(message);
			}
		}

	}

	/**
	 * This provides a way to toggle ADB service on or off.
	 * 
	 * A task that implements {@link NetworkStatusChecker} so we can use it to update the UI thread on the
	 * {@link MyNetworkStatusChecker#onPostExecute(AdbStateEnum)}.
	 * 
	 * @author Ilija Matoski (ilijamt@gmail.com)
	 */
	private final class MyToggleNetworkAdb extends NetworkStatusChecker {

		/**
		 * Instantiates a new task
		 */
		public MyToggleNetworkAdb() {
			this.mUseRoot = preferences.getBoolean(Constants.KEY_USE_ROOT,
					Constants.USE_ROOT);
		}

		/*
		 * (non-Javadoc)
		 * @see com.matoski.adbm.tasks.GenericAsyncTask#getString(int)
		 */
		@Override
		protected String getString(int resourceId) {
			return getResources().getString(resourceId);
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(AdbStateEnum result) {
			super.onPostExecute(result);
			Log.i(LOG_TAG, "Toggling the ADB state: " + result.toString());
			bNetworkADBStatus = result == AdbStateEnum.ACTIVE;
			mAdbState = result;
			determineIfWeNeedWakeLock(result);
			switch (result) {
				case ACTIVE:
					stopNetworkADB();
					break;

				case NOT_ACTIVE:
					startNetworkADB();
					break;
			}
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(String... messages) {
			super.onProgressUpdate(messages);
			for (String message : messages) {
				addItem(message);
			}
		}

	}

	/**
	 * Service binder for {@link ManagerService}, this is used to bind other parts of the application to
	 * {@link ManagerService}
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
	 * The tag used when logging with {@link Log}
	 */
	private static String LOG_TAG = ManagerService.class.getName();

	/**
	 * Should we start ADB on known WiFi networks
	 */
	private boolean ADB_START_ON_KNOWN_WIFI;

	/**
	 * The current network status, updated by various methods in {@link ManagerService}
	 */
	private boolean bNetworkADBStatus = false;

	/**
	 * Should the notification be hideable?
	 */
	private boolean bNotificationHideable = Constants.SHOW_HIDEABLE_NOTIFICATIONS;

	/**
	 * {@link Gson} object for building JSON strings
	 */
	private Gson gson;

	/**
	 * The type of the {@link Model} used to convert to JSON or convert from JSON to {@link Model}
	 */
	private Type gsonType;

	/**
	 * Handler that other applications bind to so we can send them updates to the binded components
	 */
	private IMessageHandler handler = null;

	/**
	 * The default port on which the ADB service starts
	 */
	private long mADBPort = Constants.ADB_PORT;

	/**
	 * The current state of the ADB, updated by various methods in {@link ManagerService}
	 */
	private AdbStateEnum mAdbState = AdbStateEnum.NOT_ACTIVE;

	/**
	 * The binder used to retrieve the running service
	 */
	private final IBinder mBinder = new ServiceBinder();

	/**
	 * Android {@link ConnectivityManager} used to check the network state
	 */
	private ConnectivityManager mConnectivityManager;

	/**
	 * For creating and updating notifications running on the system
	 */
	private NotificationManager mNM;

	/**
	 * Should we show the notifications?
	 */
	private boolean mShowNotification = Constants.SHOW_NOTIFICATIONS;

	/**
	 * The WiFi manager used to determine the WiFi state
	 */
	private WifiManager mWifiManager;

	/**
	 * Notification ID which is used to create/update for the application
	 */
	private int NOTIFICATION = R.string.service_name;

	/**
	 * The preferences for the {@link Application}
	 */
	private SharedPreferences preferences;

	/**
	 * {@link WakeLock} definition used to get or release a lock on the system, used in {@link #acquireWakeLock()} or
	 * {@link #releaseWakeLock()}
	 */
	private static volatile PowerManager.WakeLock wakeLock = null;

	/**
	 * Retrieve this {@link ManagerService} {@link WakeLock} for manipulation
	 * 
	 * @return
	 */
	synchronized private static PowerManager.WakeLock getLock() {
		return (wakeLock);
	}

	/**
	 * Create if there is no lock with the options supplied
	 * 
	 * @param context
	 *            The application context
	 * @param levelAndFlags
	 *            The levelAndFlags parameter specifies a wake lock level and optional flags combined using the logical
	 *            OR operator.
	 * @param referenceCounted
	 *            If false, it will release all the lock with one call to release, otherwise it will require a call of
	 *            release for each acquire
	 * 
	 * @return
	 */
	synchronized private static PowerManager.WakeLock getInitialLock(
			Context context, int levelAndFlags, boolean referenceCounted) {
		if (wakeLock == null) {
			PowerManager mgr = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);

			wakeLock = mgr.newWakeLock(levelAndFlags, LOG_TAG);
			wakeLock.setReferenceCounted(referenceCounted);
		}

		return (wakeLock);
	}

	/**
	 * Enables us to acquire a {@link WakeLock} that will enable us to keep the screen on for the duration of the lock
	 * 
	 * @todo Replace deprecated implementation {@link PowerManager#FULL_WAKE_LOCK}
	 */
	public boolean acquireWakeLock() {

		final Boolean bKeepScreenOn = preferences.getBoolean(
				Constants.KEY_KEEP_SCREEN_ON, Constants.KEEP_SCREEN_ON);

		Log.d(LOG_TAG, "Trying to acquire a wake lock.");

		if (!bKeepScreenOn) {
			addItem(getResources().getString(
					R.string.item_no_need_for_wake_lock));
			return false;
		}

		getInitialLock(getApplicationContext(),
				PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
				false).acquire();

		if (getLock() == null) {
			Log.e(LOG_TAG, "Failed to acquire wake lock");
			return false;
		}

		Log.w(LOG_TAG, "Wake lock acquired");
		addItem(getResources().getString(R.string.item_acquired_wake_lock,
				Boolean.toString(getLock().isHeld())));

		return getLock().isHeld();

	}
	
	/**
	 * A list of items to add
	 */
	private List<String> _add_items = new ArrayList<String>();
	

	/**
	 * Add a message to the list queue, and it's sent to the binded {@link #handler} to update the UI
	 * thread with the data sent through this interface
	 * 
	 * @param message
	 *            Message to send through the handler
	 */
	private void addItem(String message) {
		if (handler != null) {
			handler.message(message);
		} else {
			this._add_items.add(message);
		}
	}

	/**
	 * Initiate the auto connection for ADB, it will start the ADB service if we are in the known list of configured
	 * WiFi networks.
	 * Triggers {@link #startNetworkADB()} if {@link #isValidConnectionToWiFi()} return true
	 */
	public void autoConnectionAdb() {

		if (this.isValidConnectionToWiFi()) {
			this.startNetworkADB();
		}

	}

	/**
	 * Determine if we need wake lock based on the {@link AdbStateEnum} state of the system
	 * 
	 * @param state
	 *            The state of the system
	 */
	public void determineIfWeNeedWakeLock(AdbStateEnum state) {

		switch (state) {
			case ACTIVE:
				this.acquireWakeLock();
				break;

			case NOT_ACTIVE:
				this.releaseWakeLock();
				break;

		}

		triggerBoundActivityUpdate();

	}

	/**
	 * Checks if the ADB service is running in network mode.
	 * It's accomplished by running {@link MyNetworkStatusChecker}, and updates {@link #bNetworkADBStatus} and
	 * {@link #mAdbState} through the callback in {@link MyNetworkStatusChecker#onPostExecute(AdbStateEnum)}
	 */
	public void isNetworkADBRunning() {
		(new MyNetworkStatusChecker()).execute();
	}

	/**
	 * Checks if the connection is valid, to be used for auto ADB initialization.
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

	/**
	 * Trigger a notification update for the system based on {@link #mShowNotification}
	 */
	public void notificationUpdate() {
		this.mShowNotification = this.preferences.getBoolean(
				Constants.KEY_NOTIFICATIONS, Constants.SHOW_NOTIFICATIONS);
		this.notificationUpdate(this.mShowNotification);
	}

	/**
	 * Trigger a notification based on the update flag
	 * 
	 * @param update
	 *            Should we show the notification or not?
	 */
	public void notificationUpdate(boolean update) {
		notificationUpdate(update, bNetworkADBStatus);
	}

	/**
	 * Trigger a notification based on the update flag and {@link #bNetworkADBStatus}
	 * 
	 * @param update
	 *            Should we show the notification or not?
	 * 
	 * @param isNetworkADBRunning
	 *            Is network ADB running or not?
	 */
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

	/**
	 * Update the notifications without triggering the binded {@link Activity} through {@link #handler}
	 * 
	 * @param isNetworkADBRunning
	 *            Is the network ADB running
	 */
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

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(LOG_TAG, "Service bound to "
				+ intent.getComponent().getClassName());
		return this.mBinder;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
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

		this.notificationUpdate();

		if (this.ADB_START_ON_KNOWN_WIFI && this.isValidConnectionToWiFi()) {
			this.startNetworkADB();
		}

		(new MyNetworkStatusChecker()).execute();

	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "Destroying Manager Service");
		super.onDestroy();
		this.mNM.cancelAll();
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onRebind(android.content.Intent)
	 */
	@Override
	public void onRebind(Intent intent) {
		Log.d(LOG_TAG, "Service rebound to "
				+ intent.getComponent().getClassName());
		super.onRebind(intent);
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		String action = "No available action";

		if (intent == null) {
			Log.d(LOG_TAG, "onStartCommand: " + action + ", intent is null");
			return Service.START_STICKY;
		}

		try {

			final Bundle extras = intent.getExtras();

			if (extras == null) {
				Log.d(LOG_TAG, "onStartCommand: no extras in intent");
				return Service.START_STICKY;
			}

			action = intent.getAction();
			
			if (action == null) {
				Log.d(LOG_TAG, "onStartCommand: no action in extras");
				return Service.START_STICKY;
			}			
			
			Log.d(LOG_TAG, String.format("Running action: %s", action));

			if (action.equals(Constants.ACTION_SERVICE_START)) {
				Log.d(LOG_TAG, "Initial startup for the Service.");
			} else if (action.equals(Constants.ACTION_SERVICE_BIND)) {
				Log.d(LOG_TAG, "Binding for the Service.");
			} else if (action.equals(Constants.ACTION_SERVICE_ADB_STOP)) {
				this.stopNetworkADB();
			} else if (action.equals(Constants.ACTION_SERVICE_ADB_START)) {
				this.startNetworkADB();
			} else if (action.equals(Constants.ACTION_SERVICE_AUTO_WIFI)) {
				this.autoConnectionAdb();
			} else if (action
					.equals(Constants.ACTION_SERVICE_UPDATE_NOTIFICATION)) {
				this.notificationUpdate();
			} else if (action.equals(Constants.ACTION_SERVICE_ADB_TOGGLE)) {
				this.toggleADB();
			} else if (action
					.equals(Constants.ACTION_SERVICE_UPDATE_NOTIFICATION_NETWORK_ADB)) {
				this.isNetworkADBRunning();
			} else if (action.equals(Constants.ACTION_SERVICE_UPDATE_WIDGETS)) {
				this.updateWidgets(extras
						.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS),
						bNetworkADBStatus);
			} else if (action.equals(Constants.SERVICE_ACTION_WAKELOCK_ACQUIRE)) {
				this.acquireWakeLock();
			} else if (action.equals(Constants.SERVICE_ACTION_WAKELOCK_RELEASE)) {
				this.releaseWakeLock();
			} else if (action.equals(Constants.SERVICE_ACTION_PACKAGE_ADD)) {
				this.wakeUpPhone();
			} else {
				Log.e(LOG_TAG, String.format("Invalid action: %", action));
			}

		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}

		Log.d(LOG_TAG, "onStartCommand: " + action);
		return Service.START_STICKY;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onUnbind(android.content.Intent)
	 */
	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(LOG_TAG, "Service unbound from "
				+ intent.getComponent().getClassName());
		return super.onUnbind(intent);
	}

	/**
	 * Enables us to release an acquired {@link WakeLock}
	 * 
	 * @return
	 */
	public boolean releaseWakeLock() {

		Log.d(LOG_TAG, "Trying to release a wake lock.");

		if (getLock() == null) {
			// we don't have a wake lock
			return false;
		}

		getLock().release();

		Log.w(LOG_TAG,
				"Releasing Wake Lock: " + Boolean.toString(!getLock().isHeld()));

		addItem(getResources().getString(R.string.item_released_wake_lock,
				Boolean.toString(!getLock().isHeld())));

		return true;

	}

	/**
	 * Checks if we are holding a wake lock or not?
	 * 
	 * @return
	 */
	public boolean isWakeLockAcquired() {
		return getLock() == null ? false : getLock().isHeld();
	}

	/**
	 * Removes the notification bar from the system
	 */
	private void removeNotification() {
		Log.d(LOG_TAG, "Removing notification");
		this.mNM.cancelAll();
		triggerBoundActivityUpdate();
	}

	/**
	 * Sets the handler for communication between the bounded {@link Activity} with {@link #handler}
	 * 
	 * @param handler
	 *            the handler to set
	 */
	public void setHandler(IMessageHandler handler) {
		this.handler = handler;
		String[] messages = (String[]) this._add_items.toArray(new String[this._add_items.size()]);
		this._add_items.clear();
		for (String message : messages) {
			this.handler.message(message);
		}
	}
	
	/**
	 * Remove a handler, this shuold be called on pause
	 */
	public void removeHandler() {
		this.handler = null;
	}

	/**
	 * Show notification on the notification bar
	 * 
	 * @param isNetworkADBRunning
	 *            Is the ADB network running
	 */
	private void showNotification(boolean isNetworkADBRunning) {
		showNotification(isNetworkADBRunning, false);
	}

	/**
	 * Show notification on the notification bar
	 * 
	 * @param isNetworkADBRunning
	 *            Is the ADB network running
	 * @param dontTriggerHandlerUpdate
	 *            Do not trigger {@link IMessageHandler#update(AdbStateEnum)} for {@link #handler}
	 */
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

		final Intent mToggleIntent = new Intent(this, ManagerService.class);
		mToggleIntent.setAction(Constants.ACTION_SERVICE_ADB_TOGGLE);
		mToggleIntent.putExtra(Constants.EXTRA_ACTION,
				Constants.ACTION_SERVICE_ADB_TOGGLE);

		remoteView.setOnClickPendingIntent(R.id.notification_image,
				PendingIntent.getService(getApplicationContext(), 0,
						mToggleIntent, PendingIntent.FLAG_UPDATE_CURRENT));

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

	/**
	 * Start the ADB in network mode
	 */
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

	/**
	 * Stop the ADB if it's running in network ADB
	 */
	public void stopNetworkADB() {
		Log.i(LOG_TAG, "Stopping network ADB.");
		(new MyRootCommandExecuter()).execute(new String[] {
				"setprop service.adb.tcp.port -1", "stop adbd", "start adbd" });

	}

	/**
	 * Toggles the ADB status using {@link MyToggleNetworkAdb}
	 */
	public void toggleADB() {
		(new MyToggleNetworkAdb()).execute();
	}

	/**
	 * Triggers update with {@link IMessageHandler#update(AdbStateEnum)} through {@link #handler}, if {@link #handler}
	 * is set.
	 * 
	 * Class {@link #triggerBoundActivityUpdate(AdbStateEnum)} with {@link #mAdbState}
	 */
	private void triggerBoundActivityUpdate() {
		triggerBoundActivityUpdate(mAdbState);
	}

	/**
	 * Triggers update with {@link IMessageHandler#update(AdbStateEnum)} through {@link #handler}, if {@link #handler}
	 * is set.
	 * 
	 * @param state
	 *            The current state of ADB service
	 */
	private void triggerBoundActivityUpdate(AdbStateEnum state) {
		if (handler != null) {
			handler.update(state);
		}
	}

	/**
	 * Update the available widgets throught {@link #updateWidgets(boolean)} with {@link #bNetworkADBStatus}.
	 */
	public void updateWidgets() {
		updateWidgets(bNetworkADBStatus);
	}

	/**
	 * Update the available widgets through {@link #updateWidgets(int[], boolean)} with a list of available widgets
	 * through {@link AppWidgetManager#getAppWidgetIds(ComponentName)} and {@link #bNetworkADBStatus}
	 * 
	 * @param isNetworkADBRunning
	 *            Is network ADB running
	 */
	public void updateWidgets(boolean isNetworkADBRunning) {

		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
				.getApplicationContext());

		ComponentName thisWidget = new ComponentName(getApplicationContext(),
				ControlWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

		updateWidgets(allWidgetIds, bNetworkADBStatus);

	}

	/**
	 * Update the available widgets
	 * 
	 * @param allWidgetsIds
	 *            The widgets ID
	 * 
	 * @param isNetworkADBRunning
	 *            Is the network ADB running
	 */
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
			mServiceIntent.setAction(Constants.ACTION_SERVICE_ADB_TOGGLE);
			mServiceIntent.putExtra(Constants.EXTRA_ACTION,
					Constants.ACTION_SERVICE_ADB_TOGGLE);

			PendingIntent pendingIntent = PendingIntent.getService(
					getApplicationContext(), 0, mServiceIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			remoteView.setOnClickPendingIntent(R.id.notification_image,
					pendingIntent);

			addItem(getResources().getString(R.string.item_widget_update,
					widgetId));
			widgetManager.updateAppWidget(widgetId, remoteView);

		}
	}

	/**
	 * Wakes up the phone based on {@link SharedPreferences} property {@link Constants#KEY_WAKE_ON_NEW_PACKAGE}, it will
	 * not trigger a screen update depending on {@link #bNetworkADBStatus}
	 * 
	 * @todo Replace deprecated implementation {@link PowerManager#FULL_WAKE_LOCK}
	 */
	public void wakeUpPhone() {

		final Boolean bWakeUpPhone = preferences.getBoolean(
				Constants.KEY_WAKE_ON_NEW_PACKAGE,
				Constants.WAKE_ON_NEW_PACKAGE);

		Log.d(LOG_TAG,
				"Trying to wake up phone: "
						+ Boolean
								.toString(!(!this.bNetworkADBStatus || !bWakeUpPhone)));

		if (!this.bNetworkADBStatus || !bWakeUpPhone) {
			return;
		}

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.ON_AFTER_RELEASE, LOG_TAG);

		wl.setReferenceCounted(false);
		wl.acquire(10000);

	}

}