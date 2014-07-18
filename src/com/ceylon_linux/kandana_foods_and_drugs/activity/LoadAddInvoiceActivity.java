/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 11, 2014, 4:53 PM
 */

package com.ceylon_linux.kandana_foods_and_drugs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.ceylon_linux.kandana_foods_and_drugs.R;
import com.ceylon_linux.kandana_foods_and_drugs.controller.OutletController;
import com.ceylon_linux.kandana_foods_and_drugs.model.City;
import com.ceylon_linux.kandana_foods_and_drugs.model.District;
import com.ceylon_linux.kandana_foods_and_drugs.model.Outlet;
import com.ceylon_linux.kandana_foods_and_drugs.model.Route;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class LoadAddInvoiceActivity extends Activity {

	private final ArrayList<District> districts = new ArrayList<District>();
	private final ArrayList<Route> routes = new ArrayList<Route>();
	private final ArrayList<City> cities = new ArrayList<City>();
	private final ArrayList<Outlet> outlets = new ArrayList<Outlet>();
	ArrayAdapter<District> districtAdapter;
	ArrayAdapter<Route> routeAdapter;
	ArrayAdapter<City> cityAdapter;
	private Button btnNext;
	private TextView txtDate;
	private TextView txtTime;
	private Spinner districtAuto;
	private Spinner routeAuto;
	private Spinner cityAuto;
	private Spinner outletAuto;
	private Handler handler;
	private Timer timer;
	private ArrayAdapter<Outlet> outletAdapter;
	private Outlet outlet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load_add_invoice_page);
		initialize();

		districts.clear();
		districts.addAll(OutletController.loadDistrictsFromDb(LoadAddInvoiceActivity.this));

		districtAdapter = new ArrayAdapter<District>(LoadAddInvoiceActivity.this, R.layout.spinner_layout, districts);
		districtAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
		districtAuto.setAdapter(districtAdapter);

		routeAdapter = new ArrayAdapter<Route>(LoadAddInvoiceActivity.this, R.layout.spinner_layout, routes);
		routeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
		routeAuto.setAdapter(routeAdapter);

		cityAdapter = new ArrayAdapter<City>(LoadAddInvoiceActivity.this, R.layout.spinner_layout, cities);
		cityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
		cityAuto.setAdapter(cityAdapter);

		outletAdapter = new ArrayAdapter<Outlet>(LoadAddInvoiceActivity.this, R.layout.spinner_layout, outlets);
		outletAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
		outletAuto.setAdapter(outletAdapter);

		handler = new Handler();
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Date date = new Date();
						simpleDateFormat.applyPattern("EEEE, dd MMMM, yyyy");
						txtDate.setText(simpleDateFormat.format(date));
						simpleDateFormat.applyPattern("hh:mm:ss aa");
						txtTime.setText(simpleDateFormat.format(date));
					}
				});
			}
		}, new Date(), 1000);


	}

	// <editor-fold defaultstate="collapsed" desc="Initialize">
	private void initialize() {
		btnNext = (Button) findViewById(R.id.btnNext);
		txtDate = (TextView) findViewById(R.id.txtDate);
		txtTime = (TextView) findViewById(R.id.txtTime);
		districtAuto = (Spinner) findViewById(R.id.districtAuto);
		routeAuto = (Spinner) findViewById(R.id.routeAuto);
		cityAuto = (Spinner) findViewById(R.id.cityAuto);
		outletAuto = (Spinner) findViewById(R.id.outletAuto);
		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btnNextClicked(view);
			}
		});
		districtAuto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				districtAutoItemClicked(parent, view, position, id);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		routeAuto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				routeAutoItemClicked(parent, view, position, id);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		cityAuto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				cityAutoItemClicked(parent, view, position, id);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		outletAuto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				outletAutoItemClicked(parent, view, position, id);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}
	// </editor-fold>


	@Override
	public void onBackPressed() {
		Intent homeActivity = new Intent(LoadAddInvoiceActivity.this, HomeActivity.class);
		startActivity(homeActivity);
		finish();
	}

	private void districtAutoItemClicked(AdapterView<?> adapterView, View view, int position, long id) {
		District district = (District) adapterView.getAdapter().getItem(position);
		routes.clear();
		routes.addAll(district.getRoutes());
		routeAdapter.notifyDataSetChanged();
		routeAuto.setAdapter(routeAdapter);

		cities.clear();
		cityAdapter.notifyDataSetChanged();
		cityAuto.setAdapter(cityAdapter);

		outlets.clear();
		outletAdapter.notifyDataSetChanged();
		outletAuto.setAdapter(outletAdapter);
		outlet = null;

		routeAuto.requestFocus();
	}

	private void routeAutoItemClicked(AdapterView<?> adapterView, View view, int position, long id) {
		Route route = (Route) adapterView.getAdapter().getItem(position);

		cities.clear();
		cities.addAll(route.getCities());
		cityAdapter.notifyDataSetChanged();
		cityAuto.setAdapter(cityAdapter);

		outlets.clear();
		outletAdapter.notifyDataSetChanged();
		outletAuto.setAdapter(outletAdapter);
		outlet = null;

		cityAuto.requestFocus();
	}

	private void cityAutoItemClicked(AdapterView<?> adapterView, View view, int position, long id) {
		City city = (City) adapterView.getAdapter().getItem(position);
		Log.i("weda ", city.getOutlets().size() + "");
		outlets.clear();
		outlets.addAll(city.getOutlets());
		outletAdapter.notifyDataSetChanged();
		outletAuto.setAdapter(outletAdapter);
		outlet = null;

		outletAuto.requestFocus();
	}

	private void outletAutoItemClicked(AdapterView<?> adapterView, View view, int position, long id) {
		outlet = (Outlet) adapterView.getAdapter().getItem(position);
	}

	private void btnNextClicked(View view) {
		if (outlet == null) {
			AlertDialog.Builder alert = new AlertDialog.Builder(LoadAddInvoiceActivity.this);
			alert.setTitle(R.string.app_name);
			alert.setMessage("Please select an outlet");
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					if (routeAuto.getSelectedItem() == null) {
						routeAuto.requestFocus();
					} else {
						outletAuto.requestFocus();
					}
				}
			});
			alert.show();
			return;
		}
		ProgressDialog progressDialog = new ProgressDialog(LoadAddInvoiceActivity.this);
		progressDialog.setMessage("Loading items from local database");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(false);
		progressDialog.show();

		Intent selectItemsActivity = new Intent(LoadAddInvoiceActivity.this, ItemSelectActivity.class);
		selectItemsActivity.putExtra("outlet", outlet);
		startActivity(selectItemsActivity);
		timer.cancel();
		finish();

		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
}
