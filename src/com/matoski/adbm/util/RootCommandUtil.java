package com.matoski.adbm.util;

import java.io.DataOutputStream;

import android.util.Log;

public class RootCommandUtil {

	public static final String NEWLINE = "\n";

	public static String formatCommand(String command) {
		return command + RootCommandUtil.NEWLINE;
	}
	
	public static boolean execute(String[] commands) {
		return false;
	}

	public static boolean execute(String command) {

		Process process = null;
		DataOutputStream os = null;

		try {
			
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());

			os.writeBytes(RootCommandUtil.formatCommand(command));
			os.writeBytes(RootCommandUtil.formatCommand("exit"));
			os.flush();

			process.waitFor();

		} catch (Exception e) {
			Log.e(RootCommandUtil.class.getSimpleName(), e.getMessage());
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
				Log.e(RootCommandUtil.class.getSimpleName(), e.getMessage());
			}
		}

		return true;
	}

}