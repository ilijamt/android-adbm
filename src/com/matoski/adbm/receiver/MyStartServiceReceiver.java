package com.matoski.adbm.receiver;

import com.matoski.adbm.Constants;
import com.matoski.adbm.service.ManagerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyStartServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service = new Intent(context, ManagerService.class);
		service.setAction(Constants.KEY_SERVICE_START);
		context.startService(service);
	}
}
