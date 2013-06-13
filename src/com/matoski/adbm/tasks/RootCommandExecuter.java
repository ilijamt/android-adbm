package com.matoski.adbm.tasks;

import java.util.List;

import android.util.Log;

import com.matoski.adbm.Constants;
import com.matoski.adbm.R;
import com.matoski.adbm.enums.AdbStateEnum;

import eu.chainfire.libsuperuser.Shell;

public class RootCommandExecuter extends
		GenericAsyncTask<String, String, AdbStateEnum> {

	private static final String LOG_TAG = RootCommandExecuter.class.getName();

	@Override
	protected AdbStateEnum doInBackground(String... commands) {

		final String getTcpPort = "getprop service.adb.tcp.port";

		Log.d(LOG_TAG, String.format("Use root: %s", mUseRoot));

		publishProgress(String.format(getString(R.string.item_use_root),
				mUseRoot));

		if (mUseRoot) {
			Log.d(LOG_TAG, "Executing root commands");
			if (!Shell.SU.available()) {
				Log.e(LOG_TAG, "No superuser access available");
				publishProgress(getString(R.string.item_no_su_access));
				return AdbStateEnum.NOT_ACTIVE;
			}
		}

		publishProgress(getString(R.string.item_executing_commands));

		publishProgress(commands);

		if (mUseRoot) {

			if (null == Shell.SU.run(commands)) {
				publishProgress(getString(R.string.item_root_access_denied));
			}

			Log.d(LOG_TAG, "Processed the root commands");

		} else {

			Shell.run(Constants.SHELL_NON_ROOT_DEVICE, commands, null, false);

			Log.d(LOG_TAG, "Processed the shell commands");

		}

		publishProgress(String.format(
				getString(R.string.item_executing_command), getTcpPort));

		final List<String> networkStatus;

		if (mUseRoot) {
			networkStatus = Shell.SU.run(getTcpPort);
		} else {
			networkStatus = Shell.run(Constants.SHELL_NON_ROOT_DEVICE,
					new String[] { getTcpPort }, null, false);
		}

		if (null == networkStatus) {
			if (mUseRoot) {
				publishProgress(getString(R.string.item_root_access_denied));
			}
			publishProgress(getString(R.string.item_fail_retrieve_network_status));
			return AdbStateEnum.NOT_ACTIVE;
		}
		if (!(networkStatus.size() > 0)) {
			publishProgress(getString(R.string.item_fail_retrieve_network_status));
			return AdbStateEnum.NOT_ACTIVE;
		}

		final String command = networkStatus.get(0);
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