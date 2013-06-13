package com.matoski.adbm.tasks;

import android.os.AsyncTask;
import android.util.SparseArray;

public abstract class GenericAsyncTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result> {

	@SuppressWarnings("unused")
	private static final String LOG_TAG = GenericAsyncTask.class.getName();

	protected Boolean mUseRoot = false;

	protected SparseArray<String> map = new SparseArray<String>();

	final protected String getString(int resourceId) {
		String data = this.map.get(resourceId);

		if (data == null) {
			return "%s";
		}

		return data;
	}

}
