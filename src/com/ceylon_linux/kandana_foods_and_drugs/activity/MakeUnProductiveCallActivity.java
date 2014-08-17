/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2013, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Mar 10, 2014, 12:26:28 AM
 */
package com.ceylon_linux.kandana_foods_and_drugs.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.ceylon_linux.kandana_foods_and_drugs.util.InternetObserver;
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
	private Button btnUnProductiveCallSubmit;
	private Button btnUnProductiveCallSync;
	private int outletId;

	private Location lastKnownLocation;
	private final AsyncTask<Void, Void, Void> GPS_CHECKER = new AsyncTask<Void, Void, Void>() {
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(MakeUnProductiveCallActivity.this);
			progressDialog.setMessage("Waiting for GPS Location...");
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			do {
				lastKnownLocation = gpsReceiver.getLastKnownLocation();
				try {
					Thread.sleep(1000);//delay 1 sec
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			} while (lastKnownLocation == null);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			btnUnProductiveCallSubmit.setEnabled(true);
		}
	};
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
		GPS_CHECKER.execute();
	}

	@Override
	public void onBackPressed() {
		Intent unProductiveCall = new Intent(this, UnProductiveCallActivity.class);
		startActivity(unProductiveCall);
		finish();
	}

	// <editor-fold defaultstate="collapsed" desc="Initialize">
	private void initialize() {
		unProductiveCallOutletAuto = (AutoCompleteTextView) findViewById(R.id.unProductiveCallOutletAuto);
		txtMakeUnProductiveCallReason = (EditText) findViewById(R.id.txtMakeUnProductiveCallReason);
		btnUnProductiveCallSubmit = (Button) findViewById(R.id.btnUnProductiveCallSubmit);
		btnUnProductiveCallSync = (Button) findViewById(R.id.btnUnProductiveCallSync);
		btnUnProductiveCallSubmit.setEnabled(false);
		unProductiveCallOutletAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				unProductiveCallOutletAutoItemSelected(adapterView, view, position, id);
			}
		});
		btnUnProductiveCallSubmit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				btnUnProductiveCallSubmitClicked(view);
			}
		});
		btnUnProductiveCallSync.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				btnUnProductiveCallSyncClicked(view);
			}
		});
	}
	// </editor-fold>

	private void btnUnProductiveCallSyncClicked(View view) {
		UnProductiveCall unProductiveCall = new UnProductiveCall(
			outletId,
			txtMakeUnProductiveCallReason.getText().toString(),
			lastKnownLocation.getTime(),
			lastKnownLocation.getLongitude(),
			lastKnownLocation.getLatitude(),
			BatteryUtility.getBatteryLevel(this),
			UserController.getAuthorizedUser(this).getUserId()
		);
		if (InternetObserver.isConnectedToInternet(this)) {
			try {
				UnProductiveCallController.syncUnProductiveCall(this, unProductiveCall);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			UnProductiveCallController.saveUnProductiveCall(this, unProductiveCall);
		}
	}

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
		UnProductiveCallController.saveUnProductiveCall(this, unProductiveCall);
	}
}
