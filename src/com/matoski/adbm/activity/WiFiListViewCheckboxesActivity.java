/*
 * 
 */
package com.matoski.adbm.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.matoski.adbm.Constants;
import com.matoski.adbm.R;
import com.matoski.adbm.adapter.InteractiveArrayAdapter;
import com.matoski.adbm.pojo.Model;

/**
 * Activity that display the saved WiFi networks in an {@link ListView} to be able to select which ones to connect if
 * the network is in {@link Constants#KEY_WIFI_LIST}
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public class WiFiListViewCheckboxesActivity extends Activity {

	/**
	 * The tag used when logging with {@link Log}
	 */
	private static final String LOG_TAG = WiFiListViewCheckboxesActivity.class
			.getName();

	/** The WifiManager interface */
	private WifiManager wifiManager = null;

	/**
	 * The array adapter used to display the data in the activity
	 */
	protected InteractiveArrayAdapter adapter = null;

	/**
	 * The preferences for the {@link Application}
	 */
	protected SharedPreferences preferences = null;

	/**
	 * {@link Gson} object for building JSON strings
	 */
	private Gson gson;

	/**
	 * The type of the {@link Model} used to convert to JSON or convert from JSON to {@link Model}
	 */
	private Type gsonType;

	/**
	 * The {@link ListView} that holds the data
	 */
	private ListView listView;

	/**
	 * The save button reference, used for setting handlers
	 */
	private Button buttonSave;

	/**
	 * The cancel button reference, used for setting handlers
	 */
	private Button buttonCancel;

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_list_activity);
		this.wifiManager = (WifiManager) getBaseContext().getSystemService(
				Context.WIFI_SERVICE);
		this.gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.serializeNulls().create();
		this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
		this.buttonSave = (Button) findViewById(R.id.btn_save);
		this.buttonCancel = (Button) findViewById(R.id.btn_cancel);
		this.gsonType = new TypeToken<ArrayList<Model>>() {
		}.getType();

		prepareAdapter();

		this.buttonSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String data = "";
				if (!adapter.isEmpty()) {
					ArrayList<Model> objects = adapter.getList();
					data = gson.toJson(objects, gsonType);
					preferences.edit().putString(Constants.KEY_WIFI_LIST, data)
							.commit();
					Log.d(LOG_TAG, "Updated the WiFi auto connect list.");
				}
				finish();
			}
		});

		this.buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	/**
	 * Helper function that prepares the adapter for the items in the list, also takes care that we integrate known
	 * configuration networks and scan for new saved ones and add them to the list.
	 */
	protected void prepareAdapter() {

		ArrayList<Model> objects = gson.fromJson(
				this.preferences.getString(Constants.KEY_WIFI_LIST, null),
				gsonType);

		if (objects == null) {
			objects = new ArrayList<Model>();
		}

		Model model = null;

		for (WifiConfiguration configuration : this.wifiManager
				.getConfiguredNetworks()) {
			model = new Model(configuration.SSID.replaceAll("(^\")|(\"$)", ""));
			if (!objects.contains(model)) {
				Log.d(LOG_TAG,
						String.format("New saved network detected: %s",
								model.getName()));
				objects.add(model);
			}
		}

		this.adapter = new InteractiveArrayAdapter(this, objects);

		this.listView = (ListView) findViewById(R.id.list_wifi);
		this.listView.setAdapter(this.adapter);

	}
}
