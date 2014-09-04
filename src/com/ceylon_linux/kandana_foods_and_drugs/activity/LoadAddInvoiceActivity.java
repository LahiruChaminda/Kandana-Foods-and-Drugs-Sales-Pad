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
import android.view.View;
import android.widget.*;
import com.ceylon_linux.kandana_foods_and_drugs.R;
import com.ceylon_linux.kandana_foods_and_drugs.controller.ItemController;
import com.ceylon_linux.kandana_foods_and_drugs.controller.OutletController;
import com.ceylon_linux.kandana_foods_and_drugs.model.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class LoadAddInvoiceActivity extends Activity {

	private final ArrayList<District> districts = new ArrayList<District>();
	private final ArrayList<Route> routes = new ArrayList<Route>();
	private final ArrayList<Outlet> outlets = new ArrayList<Outlet>();
	private ArrayAdapter<District> districtAdapter;
	private ArrayAdapter<Route> routeAdapter;
	private ArrayAdapter<Outlet> outletAdapter;
	private ArrayAdapter<Distributor> distributorAdapter;
	private Button btnNext;
	private ImageButton btnClear;
	private TextView txtDate;
	private TextView txtTime;
	private Spinner districtAuto;
	private Spinner routeAuto;
	private Spinner distributorAuto;
	private AutoCompleteTextView outletAuto;
	private Handler handler;
	private Timer timer;
	private Outlet outlet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load_add_invoice_page);
		initialize();

		districts.clear();
		districts.addAll(OutletController.loadDistrictsFromDb(LoadAddInvoiceActivity.this));

		districtAdapter = new ArrayAdapter<District>(LoadAddInvoiceActivity.this, android.R.layout.simple_list_item_1, districts);
		districtAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
		districtAuto.setAdapter(districtAdapter);

		routeAdapter = new ArrayAdapter<Route>(LoadAddInvoiceActivity.this, android.R.layout.simple_list_item_1, routes);
		routeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
		routeAuto.setAdapter(routeAdapter);

		outletAdapter = new ArrayAdapter<Outlet>(LoadAddInvoiceActivity.this, android.R.layout.simple_list_item_1, outlets);
		outletAuto.setAdapter(outletAdapter);

		distributorAdapter = new ArrayAdapter<Distributor>(LoadAddInvoiceActivity.this, android.R.layout.simple_list_item_1, ItemController.loadDistributorsFromDb(LoadAddInvoiceActivity.this));
		distributorAuto.setAdapter(distributorAdapter);

		btnClear = (ImageButton) findViewById(R.id.btnClear);
		btnClear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnClearClicked(v);
			}
		});

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

	private void btnClearClicked(View view) {
		outletAuto.setText("");
		outlet = null;
	}

	// <editor-fold defaultstate="collapsed" desc="Initialize">
	private void initialize() {
		btnNext = (Button) findViewById(R.id.btnNext);
		txtDate = (TextView) findViewById(R.id.txtDate);
		txtTime = (TextView) findViewById(R.id.txtTime);
		districtAuto = (Spinner) findViewById(R.id.districtAuto);
		routeAuto = (Spinner) findViewById(R.id.routeAuto);
		distributorAuto = (Spinner) findViewById(R.id.distributorAuto);
		outletAuto = (AutoCompleteTextView) findViewById(R.id.outletAuto);
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
		outletAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				outletAutoItemClicked(parent, view, position, id);
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

		outlets.clear();
		outletAdapter.notifyDataSetChanged();
		outletAuto.setAdapter(outletAdapter);
		outletAuto.setText("");
		outlet = null;

		routeAuto.requestFocus();
	}

	private void routeAutoItemClicked(AdapterView<?> adapterView, View view, int position, long id) {
		Route route = (Route) adapterView.getAdapter().getItem(position);
		outlets.clear();
		outletAuto.setText("");
		for (City city : route.getCities()) {
			outlets.addAll(city.getOutlets());
		}
		Collections.sort(outlets);
		outletAdapter.notifyDataSetChanged();
		outletAdapter.clear();
		outletAdapter.addAll(outlets);
		outletAdapter.notifyDataSetInvalidated();
		outletAuto.requestFocus();
		outlet = null;
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
		new Thread() {
			private ProgressDialog progressDialog;
			private Handler handler = new Handler();

			@Override
			public void run() {

				handler.post(new Runnable() {
					@Override
					public void run() {
						progressDialog = ProgressDialog.show(LoadAddInvoiceActivity.this, null, "Loading Items...");
					}
				});
				int distributorId = ((Distributor) distributorAuto.getSelectedItem()).getDistributorId();
				SupplierWiseItemFragment.suppliers = ItemController.loadSuppliersFromDb(LoadAddInvoiceActivity.this, distributorId);
				CategoryWiseItemFragment.categories = ItemController.loadCategoriesFromDb(LoadAddInvoiceActivity.this, distributorId);
				UnArrangedItemFragment.items = ItemController.loadItemsFromDb(LoadAddInvoiceActivity.this, distributorId);
				handler.post(new Runnable() {
					@Override
					public void run() {
						progressDialog.dismiss();
						Intent selectItemsActivity = new Intent(LoadAddInvoiceActivity.this, ItemSelectActivity.class);
						selectItemsActivity.putExtra("outlet", outlet);
						selectItemsActivity.putExtra("distributor", (Distributor) distributorAuto.getSelectedItem());
						startActivity(selectItemsActivity);
						timer.cancel();
						finish();
					}
				});
			}
		}.start();
	}
}
