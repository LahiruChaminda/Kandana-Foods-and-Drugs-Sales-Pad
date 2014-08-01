/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jul 18, 2014, 3:27 PM
 */
package com.ceylon_linux.kandana_foods_and_drugs.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.ceylon_linux.kandana_foods_and_drugs.R;
import com.ceylon_linux.kandana_foods_and_drugs.controller.UserController;
import com.ceylon_linux.kandana_foods_and_drugs.model.Distributor;
import com.ceylon_linux.kandana_foods_and_drugs.model.Order;
import com.ceylon_linux.kandana_foods_and_drugs.model.OrderDetail;
import com.ceylon_linux.kandana_foods_and_drugs.model.Outlet;
import com.ceylon_linux.kandana_foods_and_drugs.util.BatteryUtility;
import com.ceylon_linux.kandana_foods_and_drugs.util.GpsReceiver;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class ItemSelectActivity extends FragmentActivity {

	private ViewPager viewPager;
	private ActionBar actionBar;
	private FragmentPagerAdapter fragmentPagerAdapter;
	private ArrayList<OrderDetail> orderDetails;
	private GpsReceiver gpsReceiver;
	private Thread GPS_CHECKER;
	private Location location;
	private Outlet outlet;
	private Button finishButton;
	private Handler handler;
	private ProgressDialog progressDialog;
	private Distributor distributor;

	private ArrayList<ItemSelectableFragment> itemSelectableFragments;

	ArrayList<OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	Distributor getDistributor() {
		return distributor;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_select_page);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		orderDetails = new ArrayList<OrderDetail>() {
			@Override
			public boolean add(OrderDetail object) {
				if (orderDetails.contains(object)) {
					orderDetails.remove(object);
				}
				return super.add(object);
			}
		};
		handler = new Handler();
		outlet = (Outlet) getIntent().getExtras().get("outlet");
		distributor = (Distributor) getIntent().getExtras().get("distributor");
		actionBar = getActionBar();
		fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public Fragment getItem(int position) {
				return itemSelectableFragments.get(position);
			}

			@Override
			public int getCount() {
				return itemSelectableFragments.size();
			}
		};

		itemSelectableFragments = new ArrayList<ItemSelectableFragment>();
		itemSelectableFragments.add(new SelectItemFragment1());
		itemSelectableFragments.add(new SelectItemFragment2());
		itemSelectableFragments.add(new SelectItemFragment3());
		itemSelectableFragments.add(new SelectedItemsFragment());

		viewPager.setAdapter(fragmentPagerAdapter);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});
		actionBar.setHomeButtonEnabled(false);

		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				viewPager.setCurrentItem(tab.getPosition());
				itemSelectableFragments.get(tab.getPosition()).updateUI();
			}

			public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
				// hide the given tab
			}

			public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
				// probably ignore this event
			}
		};

		addTab("Supplier wise", actionBar, tabListener);
		addTab("Category wise", actionBar, tabListener);
		addTab("All", actionBar, tabListener);
		addTab("Selected Items", actionBar, tabListener);

		finishButton = (Button) findViewById(R.id.finishButton);
		finishButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finishButtonClicked(view);
			}
		});
		gpsReceiver = GpsReceiver.getGpsReceiver(ItemSelectActivity.this);

		GPS_CHECKER = new Thread() {

			@Override
			public void run() {
				do {
					location = gpsReceiver.getLastKnownLocation();
				} while (location == null);
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(ItemSelectActivity.this, "GPS Location Received", Toast.LENGTH_LONG).show();
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
					}
				});
			}
		};
		GPS_CHECKER.start();
	}

	private void addTab(String heading, ActionBar actionBar, ActionBar.TabListener tabListener) {
		ActionBar.Tab tab = actionBar.newTab();
		tab.setText(heading);
		tab.setTabListener(tabListener);
		actionBar.addTab(tab);
	}

	private void finishButtonClicked(View view) {
		if (orderDetails.size() == 0) {
			final AlertDialog.Builder alert = new AlertDialog.Builder(ItemSelectActivity.this);
			alert.setTitle(R.string.app_name);
			alert.setMessage("Please select at least one item");
			alert.setPositiveButton("Ok", null);
			alert.show();
			return;
		}
		if ((location = gpsReceiver.getLastKnownLocation()) == null) {
			progressDialog = ProgressDialog.show(ItemSelectActivity.this, null, "Waiting for GPS...", false);
			if (GPS_CHECKER.getState() == Thread.State.TERMINATED) {
				GPS_CHECKER.start();
			}
			return;
		}
		Order order = new Order(outlet, UserController.getAuthorizedUser(ItemSelectActivity.this).getUserId(), BatteryUtility.getBatteryLevel(ItemSelectActivity.this), new Date().getTime(), location.getLongitude(), location.getLatitude(), orderDetails);
		Intent viewInvoiceActivity = new Intent(ItemSelectActivity.this, ViewInvoiceActivity.class);
		//viewInvoiceActivity.putExtra("order",order);
		ViewInvoiceActivity.order = order;
		startActivity(viewInvoiceActivity);
		finish();
	}

	@Override
	public void onBackPressed() {
		Intent homeActivity = new Intent(ItemSelectActivity.this, HomeActivity.class);
		startActivity(homeActivity);
		finish();
	}
}
