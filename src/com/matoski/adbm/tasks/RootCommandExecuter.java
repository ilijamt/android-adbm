package com.matoski.adbm.tasks;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.matoski.adbm.enums.AdbStateEnum;

import eu.chainfire.libsuperuser.Shell;

public class RootCommandExecuter extends
		AsyncTask<String, String, AdbStateEnum> {

	private static final String LOG_TAG = RootCommandExecuter.class.getName();

	@Override
	protected AdbStateEnum doInBackground(String... commands) {
				
		if (!Shell.SU.available()) {
			Log.e(LOG_TAG, "No superuser access available");
			publishProgress("No superuser access available");
			return AdbStateEnum.NOT_ACTIVE;
		}

		if ( null == Shell.SU.run(commands) ) {
			publishProgress("Root access denied");
		}
		
		Log.d(LOG_TAG, "Processed the root commands");

		List<String> networkStatus = Shell.SU
				.run("getprop service.adb.tcp.port");
		
		if ( null == networkStatus ) {
			publishProgress("Root access denied");
		}
		
		Log.d(LOG_TAG, "Checking network status");

		if (networkStatus.isEmpty()) {
			Log.w(LOG_TAG, "Couldn't retrive network status");
			publishProgress("Couldn't retrive network status");
			return AdbStateEnum.NOT_ACTIVE;
		}
	

		final String command = networkStatus.get(0);
		final AdbStateEnum stateEnum = command.equalsIgnoreCase("-1") ? AdbStateEnum.NOT_ACTIVE
				: AdbStateEnum.ACTIVE;

		Log.d(LOG_TAG, "Network status: " + stateEnum.toString());

		return stateEnum;
	}
}