package com.matoski.adbm.aidl;

interface ManagerServiceInterface {

	boolean startNetworkADB();
	boolean stopNetworkADB();
	boolean isNetworkADBRunning();
	
}