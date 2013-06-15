package com.matoski.adbm.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

/**
 * A generic implementation of {@link AsyncTask} for running tasks
 * 
 * @param <Params>
 *            Parameter type
 * @param <Progress>
 *            Progress type
 * @param <Result>
 *            Result type
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public abstract class GenericAsyncTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result> {

	/**
	 * The tag used when logging with {@link Log}
	 */
	@SuppressWarnings("unused")
	private static final String LOG_TAG = GenericAsyncTask.class.getName();

	/**
	 * Should we use root access for running the {@link GenericAsyncTask}
	 */
	protected Boolean mUseRoot = false;

	/**
	 * A map of <{@link Integer}, {@link String}>, and used to fetch resources with
	 * {@link GenericAsyncTask#getString(int)}
	 */
	protected SparseArray<String> map = new SparseArray<String>();

	/**
	 * Gets from the defined {@link GenericAsyncTask#map}
	 * 
	 * @param resourceId
	 *            The resource id value in the map
	 * 
	 * @return The string from the {@link GenericAsyncTask#map}, or empty string if it cannot find it in the
	 *         {@link GenericAsyncTask#map}
	 */
	protected String getString(int resourceId) {
		String data = this.map.get(resourceId);

		if (data == null) {
			return "";
		}

		return data;
	}

}
