package com.matoski.adbm.tasks;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.matoski.adbm.enums.AdbStateEnum;

import eu.chainfire.libsuperuser.Shell;

public class NetworkStatusChecker extends AsyncTask<Void, String, AdbStateEnum> {

	private static final String LOG_TAG = NetworkStatusChecker.class.getName();

	@Override
	protected AdbStateEnum doInBackground(Void... params) {
		
		if (!Shell.SU.available()) {
			publishProgress("No superuser access available");
			Log.e(LOG_TAG, "No superuser access available");
			return AdbStateEnum.NOT_ACTIVE;
		}

		Log.d(LOG_TAG, "Checking network status");
		
		List<String> output = Shell.SU.run("getprop service.adb.tcp.port");

		if ( null == output ) {
			publishProgress("Root access denied");
		}
		
		final String command = output.get(0);
		final AdbStateEnum stateEnum = command.equalsIgnoreCase("-1") ? AdbStateEnum.NOT_ACTIVE
				: AdbStateEnum.ACTIVE;

		Log.d(LOG_TAG, "Network status: " + stateEnum.toString());

		return stateEnum;
	}

}