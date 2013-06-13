package com.matoski.adbm.tasks;

import java.util.List;

import android.util.Log;

import com.matoski.adbm.Constants;
import com.matoski.adbm.R;
import com.matoski.adbm.enums.AdbStateEnum;

import eu.chainfire.libsuperuser.Shell;

public class NetworkStatusChecker extends
		GenericAsyncTask<Void, String, AdbStateEnum> {

	private static final String LOG_TAG = NetworkStatusChecker.class.getName();

	@Override
	protected AdbStateEnum doInBackground(Void... params) {

		Log.d(LOG_TAG, String.format("Use root: %s", mUseRoot));
		publishProgress(String.format(getString(R.string.item_use_root),
				mUseRoot));

		final String getTcpPort = "getprop service.adb.tcp.port";

		if (mUseRoot) {

			if (!Shell.SU.available()) {
				publishProgress(getString(R.string.item_no_su_access));
				Log.e(LOG_TAG, "No superuser access available");
				return AdbStateEnum.NOT_ACTIVE;
			}

		}

		Log.d(LOG_TAG, "Checking network status");

		final List<String> output;

		if (mUseRoot) {
			output = Shell.SU.run(getTcpPort);
		} else {
			output = Shell.run(Constants.SHELL_NON_ROOT_DEVICE,
					new String[] { getTcpPort }, null, false);
		}

		publishProgress(String.format(
				getString(R.string.item_executing_command), getTcpPort));

		if (null == output) {

			if (mUseRoot) {
				publishProgress(getString(R.string.item_root_access_denied));
			}

			publishProgress(getString(R.string.item_fail_retrieve_network_status));
			return AdbStateEnum.NOT_ACTIVE;
		}
		if (!(output.size() > 0)) {
			publishProgress(getString(R.string.item_fail_retrieve_network_status));
			return AdbStateEnum.NOT_ACTIVE;
		}

		final String command = output.get(0);
		final AdbStateEnum stateEnum = command.equalsIgnoreCase("-1")
				|| !(command.length() > 0) ? AdbStateEnum.NOT_ACTIVE
				: AdbStateEnum.ACTIVE;

		publishProgress(String.format("%s = %s", getTcpPort, command));

		Log.d(LOG_TAG,
				String.format("Network status: %s", stateEnum.toString()));

		publishProgress(String.format(getString(R.string.item_network_status),
				stateEnum.toString()));

		return stateEnum;
	}
}