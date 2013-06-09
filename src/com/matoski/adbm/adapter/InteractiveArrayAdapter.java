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

public class InteractiveArrayAdapter extends ArrayAdapter<Model> {

	private final ArrayList<Model> list;
	private final Activity context;

	public InteractiveArrayAdapter(Activity context, ArrayList<Model> list) {
		super(context, R.layout.list_item, list);
		this.context = context;
		this.list = list;
	}

	static class ViewHolder {
		protected CheckBox checkbox;
	}

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

	public ArrayList<Model> getList() {
		return this.list;
	}
}
