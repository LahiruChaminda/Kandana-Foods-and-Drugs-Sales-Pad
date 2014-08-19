/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 11:04:59 AM
 */

package com.ceylon_linux.kandana_foods_and_drugs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.ceylon_linux.kandana_foods_and_drugs.R;
import com.ceylon_linux.kandana_foods_and_drugs.controller.ItemController;
import com.ceylon_linux.kandana_foods_and_drugs.controller.OutletController;
import com.ceylon_linux.kandana_foods_and_drugs.controller.UserController;
import com.ceylon_linux.kandana_foods_and_drugs.db.SQLiteDatabaseHelper;
import com.ceylon_linux.kandana_foods_and_drugs.model.User;
import com.ceylon_linux.kandana_foods_and_drugs.util.InternetObserver;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class LoginActivity extends Activity {

	private EditText inputUserName;
	private EditText inputPassword;
	private Button btnLogin;
	private Button btnExit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);
		initialize();
	}

	// <editor-fold defaultstate="collapsed" desc="Initialize">
	private void initialize() {
		inputUserName = (EditText) findViewById(R.id.inputUserName);
		inputPassword = (EditText) findViewById(R.id.inputPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnExit = (Button) findViewById(R.id.btnExit);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnLoginClicked(view);
			}
		});
		btnExit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnExitClicked(view);
			}
		});
	}
	// </editor-fold>

	private void btnExitClicked(View view) {
		finish();
		System.exit(0);
	}

	private void btnLoginClicked(View view) {
		new Thread() {
			private Handler handler = new Handler();
			private ProgressDialog progressDialog;
			private User user;
			private volatile boolean internetAvailability;

			@Override
			public void run() {
				internetAvailability = InternetObserver.isConnectedToInternet(LoginActivity.this);
				if (internetAvailability) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							progressDialog = ProgressDialog.show(LoginActivity.this, null, "Download Data...", false);
						}
					});
					try {
						SQLiteDatabaseHelper.dropDatabase(LoginActivity.this);
						publishProgress("Authenticating...");
						user = UserController.authenticate(LoginActivity.this, inputUserName.getText().toString().trim(), inputPassword.getText().toString().trim());
						if (user != null && user.isValidUser()) {
							UserController.setAuthorizedUser(LoginActivity.this, user);
							publishProgress("Authenticated");
							OutletController.downloadOutlets(LoginActivity.this, user.getUserId());
							publishProgress("Outlets Downloaded Successfully");
							ItemController.downloadItems(LoginActivity.this, user.getUserId());
							publishProgress("Items Downloaded Successfully");
						}
					} catch (Exception e) {
						UserController.clearAuthentication(LoginActivity.this);
						e.printStackTrace();
					}
					handler.post(new Runnable() {
						@Override
						public void run() {
							if (progressDialog != null && progressDialog.isShowing()) {
								progressDialog.dismiss();
							}
							if (user == null) {
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
								alertDialogBuilder.setTitle(R.string.app_name);
								alertDialogBuilder.setMessage("Web Error");
								alertDialogBuilder.setPositiveButton("Ok", null);
								alertDialogBuilder.show();
							} else if (user.isValidUser()) {
								Intent homeActivity = new Intent(LoginActivity.this, HomeActivity.class);
								startActivity(homeActivity);
								finish();
							} else {
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
								alertDialogBuilder.setTitle(R.string.app_name);
								alertDialogBuilder.setMessage("Incorrect UserName Password Combination");
								alertDialogBuilder.setPositiveButton("Ok", null);
								alertDialogBuilder.show();
							}
						}
					});
				} else {
					handler.post(new Runnable() {
						@Override
						public void run() {
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
							alertDialogBuilder.setTitle(R.string.app_name);
							alertDialogBuilder.setMessage("No Internet Connection");
							alertDialogBuilder.setPositiveButton("Ok", null);
							alertDialogBuilder.show();
						}
					});
				}
			}

			private void publishProgress(final String message) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
					}
				});
			}

		}.start();
	}
}
