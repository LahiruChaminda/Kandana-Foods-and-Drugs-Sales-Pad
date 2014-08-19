/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 11:22:52 AM
 */

package com.ceylon_linux.kandana_foods_and_drugs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ceylon_linux.kandana_foods_and_drugs.R;
import com.ceylon_linux.kandana_foods_and_drugs.controller.OrderController;
import com.ceylon_linux.kandana_foods_and_drugs.controller.UserController;
import com.ceylon_linux.kandana_foods_and_drugs.model.User;
import org.json.JSONException;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class HomeActivity extends Activity {

	private TextView txtName;
	private TextView txtAddress;
	private TextView txtRepTarget;
	private TextView txtAchievedTarget;
	private TextView txtOriginDate;
	private TextView txtDueDate;
	private Button btnStart;
	private Button btnUnProductiveCall;
	private Button btnSignOut;
	private LinearLayout newsFeed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_page);
		initialize();
		User authorizedUser = UserController.getAuthorizedUser(this);
		txtName.setText(authorizedUser.getName());
		txtAddress.setText(authorizedUser.getAddress());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.my_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		new AsyncTask<Void, Void, Boolean>() {
			ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progressDialog = new ProgressDialog(HomeActivity.this);
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.setCancelable(false);
				progressDialog.setMessage("Syncing Orders");
				progressDialog.show();
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					return OrderController.syncUnSyncedOrders(HomeActivity.this);
				} catch (IOException ex) {
					ex.printStackTrace();
					return false;
				} catch (JSONException ex) {
					ex.printStackTrace();
					return false;
				}
			}

			@Override
			protected void onPostExecute(Boolean response) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				if (response) {
					Toast.makeText(HomeActivity.this, "Orders Synced Successfully", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(HomeActivity.this, "Unable to Sync Orders", Toast.LENGTH_LONG).show();
				}
			}
		}.execute();
		return true;
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
		builder.setTitle(R.string.app_name);
		builder.setMessage("You are about to sign out from sales pad\nIf you continue your un-synced data will be lost");
		builder.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				UserController.clearAuthentication(HomeActivity.this);
				Intent loginActivity = new Intent(HomeActivity.this, LoginActivity.class);
				startActivity(loginActivity);
				finish();
			}
		});
		builder.setNegativeButton("Cancel", null);
		builder.show();
	}

	// <editor-fold defaultstate="collapsed" desc="Initialize">
	private void initialize() {
		btnStart = (Button) findViewById(R.id.btnStart);
		btnUnProductiveCall = (Button) findViewById(R.id.btnUnProductiveCall);
		btnSignOut = (Button) findViewById(R.id.btnSignOut);
		txtName = (TextView) findViewById(R.id.txtName);
		txtAddress = (TextView) findViewById(R.id.txtAddress);
		txtRepTarget = (TextView) findViewById(R.id.txtRepTarget);
		txtOriginDate = (TextView) findViewById(R.id.txtOriginDate);
		txtDueDate = (TextView) findViewById(R.id.txtDueDate);
		txtAchievedTarget = (TextView) findViewById(R.id.txtAchievedTarget);
		newsFeed = (LinearLayout) findViewById(R.id.newsFeed);
		btnStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnStartClicked(view);
			}
		});
		btnUnProductiveCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnUnProductiveCallClicked(view);
			}
		});
		btnSignOut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnSignOutClicked(view);
			}
		});
	}
	// </editor-fold>

	private void btnUnProductiveCallClicked(View view) {
		Intent unProductiveCallActivity = new Intent(HomeActivity.this, UnProductiveCallActivity.class);
		startActivity(unProductiveCallActivity);
		finish();
	}

	private void btnStartClicked(View view) {
		Intent loadAddInvoiceActivity = new Intent(HomeActivity.this, LoadAddInvoiceActivity.class);
		startActivity(loadAddInvoiceActivity);
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		new Thread() {
			private Handler handler = new Handler();
			private ProgressDialog progressDialog;
			private ArrayList<String> messages;
			private UserController.RepTarget repTarget;

			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						progressDialog = ProgressDialog.show(HomeActivity.this, null, "Loading Messages", false);
					}
				});
				try {
					messages = UserController.getMessages(HomeActivity.this);
					repTarget = UserController.getTarget(HomeActivity.this);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
						if (messages != null) {
							newsFeed.removeAllViews();
							LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
							boolean colour = false;
							for (String message : messages) {
								TextView textView = (TextView) layoutInflater.inflate(R.layout.message_layout, null);
								textView.setText(message);
								textView.setBackgroundColor((colour = !colour) ? Color.parseColor("#B2B2FF") : Color.parseColor("#E6E6FF"));
								newsFeed.addView(textView);
							}
						}
						if (repTarget != null) {
							NumberFormat numberFormat = NumberFormat.getInstance();
							numberFormat.setMaximumFractionDigits(2);
							numberFormat.setMinimumFractionDigits(2);
							txtRepTarget.setText("Rs " + numberFormat.format(repTarget.getTargetAmount()));
							txtAchievedTarget.setText("Rs " + numberFormat.format(repTarget.getArchivedAmount()));
							SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd MMM, yyyy");
							txtOriginDate.setText(dateFormatter.format(repTarget.getStartDate()));
							txtDueDate.setText(dateFormatter.format(repTarget.getEndDate()));
						}
					}
				});
			}
		}.start();
	}

	private void btnSignOutClicked(View view) {
		onBackPressed();
	}
}
