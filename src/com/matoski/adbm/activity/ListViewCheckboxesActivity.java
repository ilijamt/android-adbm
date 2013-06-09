package com.matoski.adbm.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

public class ListViewCheckboxesActivity extends Activity {

	private WifiManager wifiManager = null;
	protected InteractiveArrayAdapter adapter = null;
	protected SharedPreferences preferences = null;

	private Gson gson;
	private Type gsonType;

	private ListView listView;

	private Button buttonSave;
	private Button buttonCancel;

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
				objects.add(model);
			}
		}

		this.adapter = new InteractiveArrayAdapter(this, objects);

		this.listView = (ListView) findViewById(R.id.list_wifi);
		this.listView.setAdapter(this.adapter);

	}
}
