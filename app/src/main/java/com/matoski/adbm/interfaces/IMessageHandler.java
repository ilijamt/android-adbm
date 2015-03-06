package com.matoski.adbm.interfaces;

import com.matoski.adbm.enums.AdbStateEnum;

/**
 * Message handler, used to transfer messages between the processes
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public interface IMessageHandler {

	/**
	 * Message to be sent to the process
	 * 
	 * @param message
	 *            the message
	 */
	public void message(String message);

	/**
	 * Update the process based on {@link AdbStateEnum}
	 * 
	 * @param state
	 *            the state
	 */
	public void update(AdbStateEnum state);

}
