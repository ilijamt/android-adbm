package com.matoski.adbm.activity;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.matoski.adbm.Constants;
import com.matoski.adbm.R;
import com.matoski.adbm.enums.AdbStateEnum;
import com.matoski.adbm.enums.IPMode;
import com.matoski.adbm.interfaces.IMessageHandler;
import com.matoski.adbm.service.ManagerService;
import com.matoski.adbm.tasks.NetworkStatusChecker;
import com.matoski.adbm.util.NetworkUtil;
import com.matoski.adbm.util.ServiceUtil;

/**
 * The Main activity for the screen on the device.
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public class MainActivity extends Activity {

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * A task that implements {@link NetworkStatusChecker} so we can use it to update the UI thread on the
	 * {@link MyNetworkStatusChecker#onPostExecute(AdbStateEnum)}
	 * 
	 * @author Ilija Matoski (ilijamt@gmail.com)
	 */
	private final class MyNetworkStatusChecker extends NetworkStatusChecker {

		/*
		 * (non-Javadoc)
		 * @see com.matoski.adbm.tasks.GenericAsyncTask#getString(int)
		 */
		@Override
		protected String getString(int resourceId) {
			return getResources().getString(resourceId);
		}

		/**
		 * Instantiates a new my network status checker.
		 */
		public MyNetworkStatusChecker() {
			this.mUseRoot = prefs.getBoolean(Constants.KEY_USE_ROOT,
					Constants.USE_ROOT);
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(AdbStateEnum result) {
			super.onPostExecute(result);
			updateScreenDetails(false, result);
			updateNetworkDependentScreenDetails(result);
			if (service != null) {
				service.notificationUpdateRemoteOnly(result == AdbStateEnum.ACTIVE);
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
	 * The tag used when logging with {@link Log}
	 */
	private static final String LOG_TAG = MainActivity.class.getName();

	/**
	 * The preferences for the {@link Application}
	 */
	private SharedPreferences prefs;

	/**
	 * The {@link TextView} reference for the service status
	 */
	private TextView viewServiceStatus;

	/**
	 * The {@link TextView} reference for the wakelock status
	 */
	private TextView viewWakeLockStatus;

	/**
	 * The {@link TextView} reference for the ADB status
	 */
	private TextView viewStatus;

	/**
	 * The {@link TextView} reference for the IP
	 */
	private TextView viewIP;

	/**
	 * The {@link MenuItem} reference for the menu
	 */
	private MenuItem mMenuADB;

	/**
	 * The {@link TextView} reference for the log view status
	 */
	private TextView mList;

	/**
	 * The {@link ToggleButton} reference to have the ability to toggle the state of the ADB service
	 */
	private ToggleButton mSsButton;

	/**
	 * Definition of the new line based on the java definition property
	 */
	private String mNewLine;

	/**
	 * The {@link ManagerService} is the service used to control the application
	 * for ADB.
	 */
	private ManagerService service;

	/**
	 * {@link MainActivity} handler used to add messages to {@link MainActivity#mList} to be used as a log of what has
	 * {@link ManagerService} done. And also to update the screen details based on the result of an action executed in
	 * {@link ManagerService}
	 */
	protected IMessageHandler handler = new IMessageHandler() {

		/*
		 * (non-Javadoc)
		 * @see com.matoski.adbm.interfaces.IMessageHandler#message(java.lang.String)
		 */
		@Override
		public void message(String message) {
			addItem(message);
		}

		/*
		 * (non-Javadoc)
		 * @see com.matoski.adbm.interfaces.IMessageHandler#update(com.matoski.adbm.enums.AdbStateEnum)
		 */
		@Override
		public void update(AdbStateEnum state) {
			Log.w(LOG_TAG, "Update through handler");
			updateScreenDetails(false, state);
		}
	};

	/**
	 * Interface connection to the {@link ManagerService} service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

		/*
		 * (non-Javadoc)
		 * @see android.content.ServiceConnection#onServiceConnected(android.content.ComponentName, android.os.IBinder)
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			service = ((ManagerService.ServiceBinder) binder).getService();
			service.setHandler(handler);
			if (prefs.getBoolean(Constants.KEY_ADB_START_ON_KNOWN_WIFI,
					Constants.ADB_START_ON_KNOWN_WIFI)) {
				service.autoConnectionAdb();

			} else {
				updateScreenDetails();
			}
		}

		/*
		 * (non-Javadoc)
		 * @see android.content.ServiceConnection#onServiceDisconnected(android.content.ComponentName)
		 */
		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;
			updateScreenDetails();
		}

	};

	/**
	 * Add an item to {@link MainActivity#mList}
	 * 
	 * @param message
	 *            The message to add to the list log
	 * 
	 * @return true, if successful
	 */
	protected boolean addItem(String message) {

		if (null == this.mList) {
			return false;
		}

		this.mList.append("> " + message.concat(this.mNewLine));

		try {

			final int totalLines = this.mList.getLineCount();
			final int lineTop = this.mList.getLayout().getLineTop(totalLines);
			final int viewHeight = this.mList.getHeight();
			final int scrollAmount = lineTop - viewHeight;

			if (scrollAmount > 0) {
				mList.scrollTo(0, scrollAmount);
			} else {
				mList.scrollTo(0, 0);
			}

		} catch (Exception e) {

		}

		return true;

	}

	/**
	 * Bind {@link ManagerService} for this {@link MainActivity}
	 */
	private void doBindService() {
		if (ServiceUtil.bind(this, mConnection)) {
			this.addItem(getResources().getString(
					R.string.item_service_bind_succesfully));
		} else {
			this.addItem(getResources().getString(
					R.string.item_service_bind_failed));
		}
	}

	/**
	 * Unbind {@link ManagerService} for this {@link MainActivity}
	 */
	private void doUnBindService() {
		if (ServiceUtil.unbind(this, mConnection)) {
			this.addItem(getResources().getString(
					R.string.item_service_unbind_succesfully));
		} else {
			this.addItem(getResources().getString(
					R.string.item_service_unbind_failed));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.viewServiceStatus = (TextView) findViewById(R.id.adb_service_status);
		this.viewWakeLockStatus = (TextView) findViewById(R.id.adb_wakelock_status);
		this.viewStatus = (TextView) findViewById(R.id.adb_status);
		this.viewIP = (TextView) findViewById(R.id.adb_ip_status);
		this.mList = (TextView) findViewById(R.id.adb_list);
		this.mList.setMovementMethod(ScrollingMovementMethod.getInstance());

		this.mNewLine = System.getProperty("line.separator");

		this.mSsButton = (ToggleButton) findViewById(R.id.btn_ss_service);
		this.mSsButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Log.d(LOG_TAG,
								"Toggle button checked: "
										+ Boolean.toString(isChecked));

						if (buttonView.isPressed()) {
							if (service == null) {
								buttonView.setChecked(false);
							} else {
								toggleNetworkState(!isChecked);
							}

						}

					}
				});

		this.addItem(getResources().getString(
				R.string.item_initializiation_of_manager));

		this.prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		updateScreenDetails(false, AdbStateEnum.NOT_ACTIVE);
		this.doBindService();
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		this.addItem(getResources().getString(R.string.item_menu_item_loaded));
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		this.doUnBindService();
		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				moveTaskToBack(true);
				return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.action_settings:
				this.addItem(getResources().getString(
						R.string.item_openning_item, item.getTitle()));
				startActivity(new Intent(this, MyPreferencesActivity.class));
				return true;
			case R.id.action_refresh:
				updateScreenDetails();
				return true;
			case R.id.action_adb:
				if (this.service != null) {
					toggleNetworkState(item.isChecked());
				}
				return true;
			case R.id.action_about:
				this.addItem(getResources().getString(
						R.string.item_openning_item, item.getTitle()));
				startActivity(new Intent(this, AboutActivity.class));
				return true;
			case R.id.action_change_log:
				this.addItem(getResources().getString(
						R.string.item_openning_item, item.getTitle()));
				startActivity(new Intent(this, ChangeLogActivity.class));
				return true;
			case R.id.action_clear_list:
				this.mList.setText("");
				return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		this.addItem(getResources().getString(R.string.item_adb_manager_paused));
		this.service.removeHandler();
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		this.mMenuADB = (MenuItem) menu.findItem(R.id.action_adb);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		this.service.removeHandler();
	}
	

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		this.service.setHandler(this.handler);
		this.addItem(getResources()
				.getString(R.string.item_adb_manager_resumed));
		this.updateScreenDetails();
	};

	/**
	 * Toggle the ADB network state
	 * 
	 * @param isActive
	 *            Is the ADB network currently active
	 */
	private void toggleNetworkState(boolean isActive) {

		if (service != null) {
			if (isActive) {
				this.service.stopNetworkADB();
			} else {
				this.service.startNetworkADB();
			}
		}
	}

	/**
	 * Update network dependent screen details
	 * 
	 * @param stateEnum
	 *            The state of the ADB
	 */
	private final void updateNetworkDependentScreenDetails(
			AdbStateEnum stateEnum) {

		this.addItem(getResources().getString(
				R.string.item_update_screen_based_on_state_result, stateEnum));

		Log.d(LOG_TAG, "Updating screen details based on state result: "
				+ stateEnum.toString());

		final boolean bServiceNotRunning = service == null;
		final boolean bIsNetworkAdbActive;

		final int iResourceString;

		switch (stateEnum) {
			case ACTIVE:
				bIsNetworkAdbActive = true;
				iResourceString = bServiceNotRunning ? R.string.stopped
						: R.string.running;
				break;

			case NOT_ACTIVE:
				bIsNetworkAdbActive = false;
				iResourceString = bServiceNotRunning ? R.string.stopped
						: R.string.stopped;
				break;

			default:
				bIsNetworkAdbActive = false;
				iResourceString = R.string.stopped;
		}

		// update the view status
		viewStatus.setText(iResourceString);

		// update the toggle button
		mSsButton.setChecked(bIsNetworkAdbActive);

		// menu item for ADB
		if (mMenuADB != null) {
			mMenuADB.setChecked(bIsNetworkAdbActive);
		}

	}

	/**
	 * Update the screen details, calls {@link MainActivity#updateScreenDetails(boolean, AdbStateEnum)}, to check the
	 * network state before updating and calling {@link MainActivity#updateNetworkDependentScreenDetails(AdbStateEnum)}
	 * with {@link MyNetworkStatusChecker}
	 */
	private void updateScreenDetails() {
		updateScreenDetails(true, AdbStateEnum.NOT_ACTIVE);
	}

	/**
	 * Update the screen details
	 * 
	 * @param bCheckNetworkState
	 *            Should we check the network state
	 * @param stateEnum
	 *            The state of the ADB
	 */
	private void updateScreenDetails(boolean bCheckNetworkState,
			AdbStateEnum stateEnum) {

		this.addItem(getResources().getString(
				R.string.item_update_screen_details, bCheckNetworkState,
				stateEnum));

		Log.d(LOG_TAG, "Updating screen details");

		this.viewServiceStatus.setText(this.service == null ? R.string.stopped
				: R.string.running);

		this.viewWakeLockStatus.setText(this.service == null ? R.string.unknown
				: (this.service.isWakeLockAcquired() ? R.string.acquired
						: R.string.released));

		String ip = NetworkUtil.getLocalIPAddress(IPMode.ipv4);
		String port = this.prefs.getString(Constants.KEY_ADB_PORT,
				Long.toString(Constants.ADB_PORT));

		if (ip == null) {
			this.viewIP.setText(R.string.no_net_connection);
		} else {
			this.viewIP.setText(String.format(
					getResources().getString(R.string.ip_and_port), ip, port));
		}

		this.mSsButton.setEnabled(this.service != null);

		if (this.mMenuADB != null) {
			this.mMenuADB.setEnabled(this.service != null);
		}

		if (bCheckNetworkState) {
			(new MyNetworkStatusChecker()).execute();
		} else {
			updateNetworkDependentScreenDetails(stateEnum);
		}

	}
}
