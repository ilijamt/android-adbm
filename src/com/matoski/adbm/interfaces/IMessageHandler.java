package com.matoski.adbm.interfaces;

import com.matoski.adbm.enums.AdbStateEnum;

public interface IMessageHandler {

	public void message(String message);
	public void update(AdbStateEnum state);
	
}
