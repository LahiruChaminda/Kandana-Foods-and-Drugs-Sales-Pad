/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2013, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Mar 10, 2014, 12:26:28 AM
 */
package com.ceylon_linux.kandana_foods_and_drugs.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import com.ceylon_linux.kandana_foods_and_drugs.R;
import com.ceylon_linux.kandana_foods_and_drugs.controller.OutletController;
import com.ceylon_linux.kandana_foods_and_drugs.controller.UnProductiveCallController;
import com.ceylon_linux.kandana_foods_and_drugs.controller.UserController;
import com.ceylon_linux.kandana_foods_and_drugs.model.Outlet;
import com.ceylon_linux.kandana_foods_and_drugs.model.UnProductiveCall;
import com.ceylon_linux.kandana_foods_and_drugs.util.BatteryUtility;
import com.ceylon_linux.kandana_foods_and_drugs.util.GpsReceiver;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class MakeUnProductiveCallActivity extends Activity {

	private AutoCompleteTextView unProductiveCallOutletAuto;
	private EditText txtMakeUnProductiveCallReason;
	//	private Button btnUnProductiveCallSubmit;
	private Button btnUnProductiveCallSync;
	private int outletId;

	private Location lastKnownLocation;
	private GpsReceiver gpsReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gpsReceiver = GpsReceiver.getGpsReceiver(getApplicationContext());
		setContentView(R.layout.make_unproductive_call_page);
		initialize();

		ArrayList<Outlet> outlets = OutletController.getOutlets(this);
		ArrayAdapter<Outlet> outletAdapter = new ArrayAdapter<Outlet>(this, android.R.layout.simple_dropdown_item_1line, outlets);
		unProductiveCallOutletAuto.setAdapter(outletAdapter);
		GPS_CHECKER.start();
	}

	private final Thread GPS_CHECKER = new Thread() {
		private ProgressDialog progressDialog;
		private Handler handler = new Handler();

		@Override
		public void run() {
			handler.post(new Runnable() {
				@Override
				public void run() {
					progressDialog = ProgressDialog.show(MakeUnProductiveCallActivity.this, null, "Waiting for GPS Location...", false);
				}
			});
			do {
				lastKnownLocation = gpsReceiver.getLastKnownLocation();
				try {
					Thread.sleep(500);//delay 0.5 sec
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			} while (lastKnownLocation == null);
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
				}
			});
		}
	};

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
	}

	// <editor-fold defaultstate="collapsed" desc="Initialize">
	private void initialize() {
		unProductiveCallOutletAuto = (AutoCompleteTextView) findViewById(R.id.unProductiveCallOutletAuto);
		txtMakeUnProductiveCallReason = (EditText) findViewById(R.id.txtMakeUnProductiveCallReason);
//		btnUnProductiveCallSubmit = (Button) findViewById(R.id.btnUnProductiveCallSubmit);
		btnUnProductiveCallSync = (Button) findViewById(R.id.btnUnProductiveCallSync);
		unProductiveCallOutletAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				unProductiveCallOutletAutoItemSelected(adapterView, view, position, id);
			}
		});
//		btnUnProductiveCallSubmit.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//				btnUnProductiveCallSubmitClicked(view);
//			}
//		});
		btnUnProductiveCallSync.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				btnUnProductiveCallSyncClicked(view);
			}
		});
	}

	private void btnUnProductiveCallSyncClicked(View view) {
		new AsyncTask<Void, Void, Boolean>() {
			private ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {
				progressDialog = ProgressDialog.show(MakeUnProductiveCallActivity.this, null, "Syncing", false);
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				UnProductiveCall unProductiveCall = new UnProductiveCall(
					outletId,
					txtMakeUnProductiveCallReason.getText().toString(),
					lastKnownLocation.getTime(),
					lastKnownLocation.getLongitude(),
					lastKnownLocation.getLatitude(),
					BatteryUtility.getBatteryLevel(MakeUnProductiveCallActivity.this),
					UserController.getAuthorizedUser(MakeUnProductiveCallActivity.this).getUserId()
				);
				boolean response = false;
				try {
					response = UnProductiveCallController.syncUnProductiveCall(MakeUnProductiveCallActivity.this, unProductiveCall);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				UnProductiveCallController.saveUnProductiveCall(MakeUnProductiveCallActivity.this, unProductiveCall, response);
				return response;
			}

			@Override
			protected void onPostExecute(Boolean response) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				Toast.makeText(MakeUnProductiveCallActivity.this, (response) ? "Unproductive Call Synced" : "Unproductive Call saved in local database", Toast.LENGTH_LONG).show();
				setResult(RESULT_OK);
				finish();
			}
		}.execute();
	}
	// </editor-fold>

	private void unProductiveCallOutletAutoItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
		Outlet outlet = (Outlet) adapterView.getAdapter().getItem(position);
		outletId = outlet.getOutletId();
	}

	private void btnUnProductiveCallSubmitClicked(View view) {
		UnProductiveCall unProductiveCall = new UnProductiveCall(
			outletId,
			txtMakeUnProductiveCallReason.getText().toString(),
			lastKnownLocation.getTime(),
			lastKnownLocation.getLongitude(),
			lastKnownLocation.getLatitude(),
			BatteryUtility.getBatteryLevel(this),
			UserController.getAuthorizedUser(this).getUserId()
		);
		UnProductiveCallController.saveUnProductiveCall(this, unProductiveCall, false);
		setResult(RESULT_OK);
		finish();
	}


}
