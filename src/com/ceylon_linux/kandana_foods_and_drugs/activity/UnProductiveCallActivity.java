/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Aug 14, 2014, 2:13:10 PM
 */
package com.ceylon_linux.kandana_foods_and_drugs.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.ceylon_linux.kandana_foods_and_drugs.R;
import com.ceylon_linux.kandana_foods_and_drugs.controller.UnProductiveCallController;
import com.ceylon_linux.kandana_foods_and_drugs.model.UnProductiveCall;

import java.util.ArrayList;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.unproductive_page);
		initialize();
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
				return null;
			}
		};
		listView.setAdapter(adapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case ADD_UNPRODUCTIVE_CALL_OPTION:
				if (resultCode == RESULT_OK) {
					unProductiveCalls.clear();
					unProductiveCalls.addAll(UnProductiveCallController.getUnProductiveCalls(UnProductiveCallActivity.this));
				}
				break;
			case UPDATE_UNPRODUCTIVE_CALL_OPTION:
				if (resultCode == RESULT_OK) {
					//do something
				}
				break;
		}
	}

	private void initialize() {
		listView = (ListView) findViewById(R.id.listView);
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

			}
		});
	}
}
