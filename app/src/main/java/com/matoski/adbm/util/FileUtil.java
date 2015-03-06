package com.matoski.adbm.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

/**
 * Helper for file manipulation in the Android package
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public class FileUtil {

	/**
	 * The tag used when logging with {@link Log}
	 */
	private static String LOG_TAG = FileUtil.class.getName();

	/**
	 * Read a raw file from the android application package
	 * 
	 * @param ctx
	 *            Context to use when getting the data
	 * @param resId
	 *            The resource ID to read
	 * 
	 * @return The whole file in a string
	 */
	public static String readRawFile(Context ctx, int resId) {

		Log.d(LOG_TAG, "Fetching raw file with id: " + resId);

		InputStream inputStream = ctx.getResources().openRawResource(resId);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		int i;
		try {
			i = inputStream.read();
			while (i != -1) {
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
			inputStream.close();
		} catch (IOException e) {
			return null;
		}
		return byteArrayOutputStream.toString();
	}
}