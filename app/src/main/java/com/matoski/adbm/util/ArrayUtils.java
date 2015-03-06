package com.matoski.adbm.util;

import android.util.Log;

/**
 * Helper class for dealing with arrays
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public class ArrayUtils {

	/**
	 * The tag used when logging with {@link Log}
	 */
	private static String LOG_TAG = ArrayUtils.class.getName();

	/**
	 * Join the array into a delimited string
	 * 
	 * @param aArr
	 *            The array we need to convert to a string delimited list
	 * 
	 * @param sSep
	 *            Separator to use
	 * 
	 * @return The string delimited list
	 */
	public static String join(int[] aArr, String sSep) {
		Log.d(LOG_TAG, "Joinig an array with a separator: " + sSep);
		StringBuilder sbStr = new StringBuilder();
		for (int i = 0, il = aArr.length; i < il; i++) {
			if (i > 0) sbStr.append(sSep);
			sbStr.append(aArr[i]);
		}
		return sbStr.toString();
	}

}
