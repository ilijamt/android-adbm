package com.matoski.adbm.service;

import com.matoski.adbm.R;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ManagerService extends Service {

    private NotificationManager mNM;
	private final IBinder mBinder = new ServiceBinder();
    private int NOTIFICATION = R.string.service_name;

	
	@Override
	public void onCreate() {
		super.onCreate();
        this.mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);		
        this.showNotification();
	}
	
	private void showNotification() {
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.mNM.cancel(this.NOTIFICATION);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ManagerService", "Received start id " + startId + ": " + intent);
		return Service.START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return this.mBinder;
	}

	public class ServiceBinder extends Binder {
		
		ManagerService getService() {
			return ManagerService.this;
		}

	}

}
