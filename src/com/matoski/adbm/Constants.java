package com.matoski.adbm;

public class Constants {

	public final static boolean START_ON_BOOT = true;
	public final static int DELAY_START_AFTER_BOOT = 30;
	public final static int CHECK_IF_STARTED_INTERVAL = 30;
	public final static int ALARM_TIMEOUT_INTERVAL = 30;
	public final static long ADB_PORT = 5555;
	
	public final static String KEY_ALARM_TIMEOUT_INTERVAL = "alarm_timeout_interval";
	public final static String KEY_START_DELAY = "start_delay";
	public final static String KEY_START_ON_BOOT = "start_on_boot";
	public final static String KEY_ADB_PORT = "adb_port";
	public final static String KEY_ADB_START_ON_UP = "start_on_app_start";


}
