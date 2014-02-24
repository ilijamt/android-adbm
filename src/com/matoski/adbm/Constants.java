package com.matoski.adbm;

import com.matoski.adbm.service.ManagerService;

import android.content.Intent;

/**
 * A collection of constants to be used in the application
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public class Constants {

	/** Do we start the service on boot? (default value) */
	public final static boolean START_ON_BOOT = true;

	/** Start on known WiFi networks (default value) */
	public final static boolean ADB_START_ON_KNOWN_WIFI = false;

	/** How long after boot should we start the service (default value) */
	public final static int DELAY_START_AFTER_BOOT = 10;

	/**
	 * How often should we check if the service is running when started from
	 * boot. Used for AlarmManager. (default value)
	 */
	public final static int ALARM_TIMEOUT_INTERVAL = 7200;

	/**
	 * The port on which ADB listens to. (default value)
	 */
	public final static long ADB_PORT = 5555;

	/**
	 * Do we show notifications while the service is running? (default value)
	 */
	public final static boolean SHOW_NOTIFICATIONS = true;

	/**
	 * Can we hide the notifications? (default value)
	 */
	public final static boolean SHOW_HIDEABLE_NOTIFICATIONS = false;

	/**
	 * Do we use root for service functionality? (default value)
	 */
	public final static boolean USE_ROOT = true;

	/**
	 * Minimum SDK for which we can use without root? (default value)
	 */
	public final static int MINIMUM_SDK_WITHOUT_ROOT = 100;

	/**
	 * Should we keep the screen on? (default value)
	 * Mutually exclusive with {@link Constants#WAKE_ON_NEW_PACKAGE}
	 */
	public static final boolean KEEP_SCREEN_ON = false;

	/**
	 * Should we wake up the screen on a new installation? (default value)
	 * Mutually exclusive with {@link Constants#KEEP_SCREEN_ON}
	 */
	public static final boolean WAKE_ON_NEW_PACKAGE = false;

	/**
	 * The shell to use if we can start the ADB without root. (default value)
	 * Depends on: {@link Constants#USE_ROOT}
	 */
	public final static String SHELL_NON_ROOT_DEVICE = "sh";

	/**
	 * Preference key for {@link Constants#USE_ROOT}
	 */
	public final static String KEY_USE_ROOT = "use_root";

	/**
	 * Preference key for {@link Constants#ALARM_TIMEOUT_INTERVAL}
	 */
	public final static String KEY_ALARM_TIMEOUT_INTERVAL = "alarm_timeout_interval";

	/**
	 * Preference key for {@link Constants#KEY_START_DELAY}
	 */
	public final static String KEY_START_DELAY = "start_delay";

	/**
	 * Preference key for {@link Constants#START_ON_BOOT}
	 */
	public final static String KEY_START_ON_BOOT = "start_on_boot";

	/**
	 * Preference key for {@link Constants#ADB_PORT}
	 */
	public final static String KEY_ADB_PORT = "adb_port";

	/**
	 * Preference key for {@link Constants#ADB_START_ON_KNOWN_WIFI}
	 */
	public final static String KEY_ADB_START_ON_KNOWN_WIFI = "auto_start_on_known_wifi";

	/**
	 * Preference key for {@link Constants#SHOW_NOTIFICATIONS}
	 */
	public final static String KEY_NOTIFICATIONS = "show_notifications";

	/**
	 * Preference key for the available WiFi list
	 */
	public final static String KEY_WIFI_LIST = "wifi_list";

	/**
	 * Preference key for {@link Constants#SHOW_HIDEABLE_NOTIFICATIONS}
	 */
	public final static String KEY_HIDEABLE_NOTIFICATION_BAR = "hideable_notification_bar";

	/**
	 * Extra action to be used when sending data in {@link Intent}
	 */
	public static final String EXTRA_ACTION = "action";

	/**
	 * Preference key for {@link Constants#KEEP_SCREEN_ON}
	 */
	public static final String KEY_KEEP_SCREEN_ON = "keep_screen_on";

	/**
	 * Preference key for {@link Constants#WAKE_ON_NEW_PACKAGE}
	 */
	public static final String KEY_WAKE_ON_NEW_PACKAGE = "wake_on_new_package";

	/**
	 * Used to start the service
	 * An action of the service {@link ManagerService}
	 */
	public static final String ACTION_SERVICE_START = "SERVICE_START";

	/**
	 * Used to bind the service
	 * An action of the service {@link ManagerService}
	 */
	public static final String ACTION_SERVICE_BIND = "SERVICE_BIND";

	/**
	 * Auto connect to WiFi
	 * An action of the service {@link ManagerService}
	 */
	public static final String ACTION_SERVICE_AUTO_WIFI = "AUTO_WIFI_CONNECT";

	/**
	 * Stop the ADB
	 * An action of the service {@link ManagerService}
	 */
	public static final String ACTION_SERVICE_ADB_STOP = "ADB_STOP";

	/**
	 * Start the ADB
	 * An action of the service {@link ManagerService}
	 */
	public static final String ACTION_SERVICE_ADB_START = "ADB_START";

	/**
	 * ADB Toggle
	 * An action of the service {@link ManagerService}
	 */
	public static final String ACTION_SERVICE_ADB_TOGGLE = "ADB_TOGGLE";

	/**
	 * Update the notifications
	 * An action of the service {@link ManagerService}
	 */
	public static final String ACTION_SERVICE_UPDATE_NOTIFICATION = "UPDATE_NOTIFICATION";

	/**
	 * Update the notifications for ADB network status
	 * An action of the service {@link ManagerService}
	 */
	public static final String ACTION_SERVICE_UPDATE_NOTIFICATION_NETWORK_ADB = "UPDATE_NOTIFICATION_NETWORK_ADB";

	/**
	 * Update all the available widgets
	 * An action of the service {@link ManagerService}
	 */
	public static final String ACTION_SERVICE_UPDATE_WIDGETS = "UPDATE_WIDGETS";

	/**
	 * Acquire wake lock
	 * An action of the service {@link ManagerService}
	 */
	public static final String SERVICE_ACTION_WAKELOCK_ACQUIRE = "WAKELOCK_ACQUIRE";

	/**
	 * Release wake lock
	 * An action of the service {@link ManagerService}
	 */
	public static final String SERVICE_ACTION_WAKELOCK_RELEASE = "WAKELOCK_RELEASE";

	/**
	 * Notify the service that package has been added
	 * An action of the service {@link ManagerService}
	 */
	public static final String SERVICE_ACTION_PACKAGE_ADD = "PACKAGE_ADD";


	/**
	 * How many times to retry to get the network list before giving up
	 */
	public static final int RETRY_GET_NETWORK_LIST = 3;
}
