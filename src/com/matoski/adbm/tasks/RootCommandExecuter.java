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

		Log.d(LOG_TAG, "Executing root commands");
		
		if (!Shell.SU.available()) {
			Log.e(LOG_TAG, "No superuser access available");
			publishProgress("No superuser access available");
			return AdbStateEnum.NOT_ACTIVE;
		}

		publishProgress("Executing commands");
		publishProgress(commands);

		if (null == Shell.SU.run(commands)) {
			publishProgress("Root access denied");
		}

		Log.d(LOG_TAG, "Processed the root commands");
		
		final String getTcpPort = "getprop service.adb.tcp.port";
		publishProgress(String.format("Executing command: %s", getTcpPort));
		
		List<String> networkStatus = Shell.SU
				.run(getTcpPort);
		
		if (null == networkStatus) {
			publishProgress("Root access denied");
			publishProgress("Couldn't retrieve the adb network status");
			return AdbStateEnum.NOT_ACTIVE;
		}
		if (!(networkStatus.size() > 0)) {
			publishProgress("Couldn't retrieve the adb network status");
			return AdbStateEnum.NOT_ACTIVE;
		}
	
		final String command = networkStatus.get(0);
		final AdbStateEnum stateEnum = command.equalsIgnoreCase("-1") ? AdbStateEnum.NOT_ACTIVE
				: AdbStateEnum.ACTIVE;

		publishProgress(String.format("%s = %s", getTcpPort, command));
		
		Log.d(LOG_TAG,
				String.format("Network status: %s", stateEnum.toString()));

		publishProgress(String.format("Network status: %s", stateEnum.toString()));
		
		return stateEnum;

	}
}