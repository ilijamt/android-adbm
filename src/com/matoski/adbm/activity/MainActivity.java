package com.matoski.adbm.activity;

import android.app.Activity;
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
 * The Main activity for the screen on the device
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public class MainActivity extends Activity {

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private final class MyNetworkStatusChecker extends NetworkStatusChecker {

		@Override
		protected void onPostExecute(AdbStateEnum result) {
			super.onPostExecute(result);
			updateNetworkDependentScreenDetails(result);
			service.notificationUpdateRemoteOnly(result == AdbStateEnum.ACTIVE);
		}

		@Override
		protected void onProgressUpdate(String... messages) {
			super.onProgressUpdate(messages);
			for (String message : messages) {
				addItem(message);
			}
		}
	}

	private static final String LOG_TAG = MainActivity.class.getName();

	/** The preferences for the entire application */
	private SharedPreferences prefs;

	/** The service status text view id */
	private TextView viewServiceStatus;

	/** The status text view id */
	private TextView viewStatus;

	private TextView viewIP;

	private MenuItem mMenuADB;

	private TextView mList;

	private ToggleButton mSsButton;

	private String mNewLine;

	/**
	 * The {@link ManagerService} is the service used to control the application
	 * for ADB.
	 */
	private ManagerService service;

	protected IMessageHandler handler = new IMessageHandler() {

		@Override
		public void message(String message) {
			addItem(message);
		}

		@Override
		public void update(AdbStateEnum state) {
			Log.w(LOG_TAG, "Update through handler");
			updateScreenDetails(false, state);
		}
	};

	/** Interface connection to the {@link ManagerService} service */
	private ServiceConnection mConnection = new ServiceConnection() {

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

		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;
			updateScreenDetails();
		}

	};

	private long alarmTimeout;

	// private int counter = 0;

	protected boolean addItem(String message) {

		if (null == this.mList) {
			return false;
		}

		// this.mList.append(String.format("%03d", ++counter) + ". "
		// + message.concat(this.mNewLine));

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
	 * Do bind service.
	 */
	private void doBindService() {
		if (ServiceUtil.bind(this, mConnection)) {
			this.addItem("Service binded successfully");
		} else {
			this.addItem("Service failed to bind");
		}
	}

	private void doUnBindService() {
		if (ServiceUtil.unbind(this, mConnection)) {
			this.addItem("Service unbinded sucessfully");
		} else {
			this.addItem("Service unbind failed");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.viewServiceStatus = (TextView) findViewById(R.id.adb_service_status);
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

							// updateScreenDetails();

						}

					}
				});

		this.addItem("Initialization of ADB Manager");

		this.prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		if (!ServiceUtil.isServiceRunning(getApplicationContext())) {
			try {
				this.alarmTimeout = this.prefs.getLong(
						Constants.KEY_ALARM_TIMEOUT_INTERVAL,
						Constants.ALARM_TIMEOUT_INTERVAL * 1000);
			} catch (Exception e) {
				this.alarmTimeout = Constants.ALARM_TIMEOUT_INTERVAL * 1000;
			}
			ServiceUtil.start(getApplicationContext(), 0, this.alarmTimeout);
		}

		updateScreenDetails(false, AdbStateEnum.NOT_ACTIVE);
		this.doBindService();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		this.addItem("Menu items loaded");
		return true;
	}

	@Override
	protected void onDestroy() {
		this.doUnBindService();
		super.onDestroy();
	}

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
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_settings:
			this.addItem("Openning " + item.getTitle());
			startActivity(new Intent(this, MyPreferencesActivity.class));
			return true;
		case R.id.action_refresh:
			updateScreenDetails();
			return true;
		case R.id.action_adb:
			if (this.service != null) {
				toggleNetworkState(item.isChecked());
				// this.updateScreenDetails();
			}
			return true;

		case R.id.action_clear_list:
			this.mList.setText("");
			// this.counter = 0;
			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		this.addItem("ADB Manager activity paused");
		// this.updateScreenDetails();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		this.mMenuADB = (MenuItem) menu.findItem(R.id.action_adb);
		// this.updateScreenDetails();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		this.addItem("ADB Manager resumed");
		this.updateScreenDetails();
	};

	private void toggleNetworkState(boolean isActive) {

		if (isActive) {
			this.service.stopNetworkADB();
		} else {
			this.service.startNetworkADB();
		}
	}

	private final void updateNetworkDependentScreenDetails(
			AdbStateEnum stateEnum) {

		this.addItem("Updating screen details based on state result: " + stateEnum.toString());

		Log.d(LOG_TAG, "Updating screen details based on state result: " + stateEnum.toString());
		
		// update the view status
		viewStatus.setText(service == null ? R.string.stopped
				: (stateEnum == AdbStateEnum.ACTIVE ? R.string.running
						: R.string.stopped));

		// update the toggle button
		mSsButton.setChecked(service == null ? false
				: stateEnum == AdbStateEnum.ACTIVE);

		// menu item for ADB
		if (mMenuADB != null) {
			mMenuADB.setChecked(service == null ? false
					: stateEnum == AdbStateEnum.ACTIVE);
		}

	}

	private void updateScreenDetails() {
		updateScreenDetails(true, AdbStateEnum.NOT_ACTIVE);
	}

	/**
	 * Update details.
	 */
	private void updateScreenDetails(boolean bCheckNetworkState,
			AdbStateEnum stateEnum) {

		this.addItem("Updating screen details");

		Log.d(LOG_TAG, "Updating screen details");

		this.viewServiceStatus.setText(this.service == null ? R.string.stopped
				: R.string.running);

		String ip = NetworkUtil.getLocalIPAddress(IPMode.ipv4);
		String port = this.prefs.getString(Constants.KEY_ADB_PORT,
				Long.toString(Constants.ADB_PORT));

		if (ip == null) {
			this.viewIP.setText(R.string.no_net_connection);
		} else {
			this.viewIP.setText(String.format("%s:%s", ip, port));
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
