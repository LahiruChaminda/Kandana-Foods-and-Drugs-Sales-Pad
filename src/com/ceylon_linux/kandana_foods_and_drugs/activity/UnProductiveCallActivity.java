/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Aug 14, 2014, 2:13:10 PM
 */
package com.ceylon_linux.kandana_foods_and_drugs.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ceylon_linux.kandana_foods_and_drugs.R;
import com.ceylon_linux.kandana_foods_and_drugs.controller.UnProductiveCallController;
import com.ceylon_linux.kandana_foods_and_drugs.model.UnProductiveCall;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class UnProductiveCallActivity extends Activity {
	private final int ADD_UNPRODUCTIVE_CALL_OPTION = 0;
	private final int UPDATE_UNPRODUCTIVE_CALL_OPTION = 1;

	private ListView listView;
	private Button btnAddUnProductiveCall;
	private Button btnBack;
	private BaseAdapter adapter;
	private ArrayList<UnProductiveCall> unProductiveCalls = new ArrayList<UnProductiveCall>();
	private SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("EEEE, dd MMMM, yyyy");
	private UnProductiveCall selectedUnProductiveCall;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.unproductive_page);
		initialize();
		unProductiveCalls.addAll(UnProductiveCallController.getUnProductiveCalls(UnProductiveCallActivity.this));
		adapter = new BaseAdapter() {
			@Override
			public int getCount() {
				return unProductiveCalls.size();
			}

			@Override
			public UnProductiveCall getItem(int position) {
				return unProductiveCalls.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder viewHolder;
				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
					convertView = inflater.inflate(R.layout.un_productive_call_item, null);
					convertView.setTag(viewHolder = new ViewHolder());
					viewHolder.txtDateTime = (TextView) convertView.findViewById(R.id.txtDateTme);
					viewHolder.txtOutletName = (TextView) convertView.findViewById(R.id.txtOutletName);
					viewHolder.txtReason = (TextView) convertView.findViewById(R.id.txtReason);
				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}
				UnProductiveCall unProductiveCall = getItem(position);
				viewHolder.txtOutletName.setText(unProductiveCall.getOutletName());
				viewHolder.txtDateTime.setText(simpleDateFormatter.format(new Date(unProductiveCall.getTimestamp())));
				viewHolder.txtReason.setText(unProductiveCall.getReason());
				convertView.setBackgroundColor((unProductiveCall.isSynced()) ? Color.GREEN : Color.RED);
				return convertView;
			}
		};
		listView.setAdapter(adapter);
	}

	@Override
	public void onBackPressed() {
		Intent homeActivity = new Intent(UnProductiveCallActivity.this, HomeActivity.class);
		startActivity(homeActivity);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*switch (requestCode) {
			case ADD_UNPRODUCTIVE_CALL_OPTION:
				if (resultCode == RESULT_OK) {
					Toast.makeText(UnProductiveCallActivity.this, "Unproductive Call Added", Toast.LENGTH_LONG).show();
				}
				break;
			case UPDATE_UNPRODUCTIVE_CALL_OPTION:
				if (resultCode == RESULT_OK) {
					Toast.makeText(UnProductiveCallActivity.this, "Unproductive Call Updated", Toast.LENGTH_LONG).show();
				}
				break;
		}*/
		if (resultCode == RESULT_OK) {
			unProductiveCalls.clear();
			unProductiveCalls.addAll(UnProductiveCallController.getUnProductiveCalls(UnProductiveCallActivity.this));
			adapter.notifyDataSetChanged();
		}
	}

	private void initialize() {
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				selectedUnProductiveCall = (UnProductiveCall) parent.getAdapter().getItem(position);
				return true;
			}
		});
		btnAddUnProductiveCall = (Button) findViewById(R.id.btnAddUnProductiveCall);
		btnBack = (Button) findViewById(R.id.btnBack);
		btnAddUnProductiveCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent makeUnProductiveCallActivity = new Intent(UnProductiveCallActivity.this, MakeUnProductiveCallActivity.class);
				startActivityForResult(makeUnProductiveCallActivity, ADD_UNPRODUCTIVE_CALL_OPTION);
			}
		});
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent homeActivity = new Intent(UnProductiveCallActivity.this, HomeActivity.class);
				startActivity(homeActivity);
				finish();
			}
		});
	}

	private static class ViewHolder {
		TextView txtOutletName;
		TextView txtDateTime;
		TextView txtReason;
	}
}
