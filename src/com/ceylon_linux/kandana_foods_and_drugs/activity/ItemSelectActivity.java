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
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import com.ceylon_linux.kandana_foods_and_drugs.R;
import com.ceylon_linux.kandana_foods_and_drugs.controller.UserController;
import com.ceylon_linux.kandana_foods_and_drugs.model.Distributor;
import com.ceylon_linux.kandana_foods_and_drugs.model.Order;
import com.ceylon_linux.kandana_foods_and_drugs.model.OrderDetail;
import com.ceylon_linux.kandana_foods_and_drugs.model.Outlet;
import com.ceylon_linux.kandana_foods_and_drugs.util.BatteryUtility;
import com.ceylon_linux.kandana_foods_and_drugs.util.BusProvider;
import com.ceylon_linux.kandana_foods_and_drugs.util.LocationProviderService;
import com.squareup.otto.Subscribe;

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
	private volatile Location location;
	private Outlet outlet;
	private Button finishButton;
	private ProgressDialog progressDialog;
	private Distributor distributor;
	private boolean isBoundWithLocationProviderService;
	private ServiceConnection serviceConnection;

	private ArrayList<ItemSelectableFragment> itemSelectableFragments;

	ArrayList<OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_select_page);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		if (getIntent().hasExtra("editOrder")) {
			orderDetails = ViewInvoiceActivity.order.getOrderDetails();
		} else {
			orderDetails = new ArrayList<OrderDetail>() {
				@Override
				public boolean add(OrderDetail object) {
					if (orderDetails.contains(object)) {
						orderDetails.remove(object);
					}
					return super.add(object);
				}
			};
		}
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
		itemSelectableFragments.add(new SupplierWiseItemFragment());
		itemSelectableFragments.add(new CategoryWiseItemFragment());
		itemSelectableFragments.add(new UnArrangedItemFragment());
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
	}

	@Override
	protected void onResume() {
		super.onResume();
		BusProvider.getInstance().register(ItemSelectActivity.this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		BusProvider.getInstance().unregister(ItemSelectActivity.this);
	}

	private void addTab(String heading, ActionBar actionBar, ActionBar.TabListener tabListener) {
		ActionBar.Tab tab = actionBar.newTab();
		tab.setText(heading);
		tab.setTabListener(tabListener);
		actionBar.addTab(tab);
	}

	private void finishButtonClicked(View view) {
		synchronized (ItemSelectActivity.this) {
			if (orderDetails.size() == 0) {
				final AlertDialog.Builder alert = new AlertDialog.Builder(ItemSelectActivity.this);
				alert.setTitle(R.string.app_name);
				alert.setMessage("Please select at least one item");
				alert.setPositiveButton("Ok", null);
				alert.show();
				return;
			}
			if (location == null) {
				progressDialog = ProgressDialog.show(ItemSelectActivity.this, null, "Waiting for GPS…", false);
				bindLocationProviderService();
				return;
			}
			Order order = new Order(outlet, UserController.getAuthorizedUser(ItemSelectActivity.this).getUserId(), BatteryUtility.getBatteryLevel(ItemSelectActivity.this), new Date().getTime(), location.getLongitude(), location.getLatitude(), orderDetails, distributor.getDistributorId());
			Intent viewInvoiceActivity = new Intent(ItemSelectActivity.this, ViewInvoiceActivity.class);
			viewInvoiceActivity.putExtra("outlet", outlet);
			viewInvoiceActivity.putExtra("distributor", distributor);
			ViewInvoiceActivity.order = order;
			startActivity(viewInvoiceActivity);
			finish();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		bindLocationProviderService();
	}

	@Override
	protected void onStop() {
		if (isBoundWithLocationProviderService) {
			unbindService(serviceConnection);
		}
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		Intent homeActivity = new Intent(ItemSelectActivity.this, HomeActivity.class);
		startActivity(homeActivity);
		finish();
	}

	@Subscribe
	public void onLocationUpdateReceived(Location location) {
		synchronized (ItemSelectActivity.this) {
			this.location = location;
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}
	}

	private void bindLocationProviderService() {
		Intent locationProviderService = new Intent(ItemSelectActivity.this, LocationProviderService.class);
		bindService(
			locationProviderService,
			serviceConnection = new ServiceConnection() {

				@Override
				public void onServiceConnected(ComponentName className, IBinder service) {
					LocationProviderService.LocationBinder binder = (LocationProviderService.LocationBinder) service;
					location = binder.getLastKnownLocation();
					isBoundWithLocationProviderService = true;
				}

				@Override
				public void onServiceDisconnected(ComponentName arg0) {
					isBoundWithLocationProviderService = false;
				}
			},
			BIND_AUTO_CREATE
		);
	}
}
