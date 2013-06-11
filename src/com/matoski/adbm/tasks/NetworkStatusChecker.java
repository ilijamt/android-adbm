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

		final String getTcpPort = "getprop service.adb.tcp.port";

		List<String> output = Shell.SU.run(getTcpPort);

		publishProgress(String.format("Executing command: %s", getTcpPort));

		if (null == output) {
			publishProgress("Root access denied");
			publishProgress("Couldn't retrieve the adb network status");
			return AdbStateEnum.NOT_ACTIVE;
		}
		if (!(output.size() > 0)) {
			publishProgress("Couldn't retrieve the adb network status");
			return AdbStateEnum.NOT_ACTIVE;
		}

		final String command = output.get(0);
		final AdbStateEnum stateEnum = command.equalsIgnoreCase("-1") ? AdbStateEnum.NOT_ACTIVE
				: AdbStateEnum.ACTIVE;

		publishProgress(String.format("%s = %s", getTcpPort, command));

		Log.d(LOG_TAG,
				String.format("Network status: %s", stateEnum.toString()));

		publishProgress(String.format("Network status: %s",
				stateEnum.toString()));

		return stateEnum;
	}

}