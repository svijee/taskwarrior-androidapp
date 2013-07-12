package org.svij.taskwarriorapp.db;

import java.util.ArrayList;

import org.svij.taskwarriorapp.R;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ActionBarAdapter extends BaseAdapter {

	Context context;
	int layoutResourceId;
	ArrayList<String> data;
	LayoutInflater inflater;

	public ActionBarAdapter(Context context, int textViewResourceId,
			ArrayList<String> data, FragmentManager fm) {
		this.data = data;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.layoutResourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View actionBarView = inflater.inflate(R.layout.ab_main_view, null);

		TextView title = (TextView) actionBarView
				.findViewById(R.id.ab_basemaps_title);
		title.setText(data.get(position));

		return actionBarView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View actionBarDropDownView = inflater.inflate(
				R.layout.ab_dropdown_view, null);
		TextView dropDownTitle = (TextView) actionBarDropDownView
				.findViewById(R.id.ab_basemaps_dropdown_title);

		dropDownTitle.setText(data.get(position));

		return actionBarDropDownView;

	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}