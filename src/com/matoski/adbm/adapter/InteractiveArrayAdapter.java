package com.matoski.adbm.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.matoski.adbm.R;
import com.matoski.adbm.pojo.Model;

/**
 * An interactive array adapter, used to with {@link Model}, to provide an array
 * adapter that can have two states, selected or not selected
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public class InteractiveArrayAdapter extends ArrayAdapter<Model> {

	/**
	 * The current list of the available items in the array adapter
	 */
	private final ArrayList<Model> list;

	/**
	 * Context of the Activity that implements the this adapter
	 */
	private final Activity context;

	/**
	 * Instantiates a new interactive array adapter.
	 * 
	 * @param context
	 *            Context of the Activity that implements the this adapter
	 * @param list
	 *            The list of the available items in the array adapter
	 */
	public InteractiveArrayAdapter(Activity context, ArrayList<Model> list) {
		super(context, R.layout.list_item, list);
		this.context = context;
		this.list = list;
	}

	/**
	 * View holder class used to hold the view model for an item
	 * 
	 * @author Ilija Matoski (ilijamt@gmail.com)
	 */
	static class ViewHolder {

		/**
		 * The model checkbox
		 */
		protected CheckBox checkbox;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.list_item, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
			viewHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							Model element = (Model) viewHolder.checkbox
									.getTag();
							element.setSelected(buttonView.isChecked());

						}
					});
			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.checkbox.setText(list.get(position).getName());
		holder.checkbox.setChecked(list.get(position).isSelected());
		return view;
	}

	/**
	 * Returns the available list.
	 * 
	 * @return the list of available {@link Model}
	 */
	public ArrayList<Model> getList() {
		return this.list;
	}
}
