package com.matoski.adbm;

import com.matoski.adbm.service.ManagerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyStartServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	    Intent service = new Intent(context, ManagerService.class);
	    context.startService(service);
		
	}
}
