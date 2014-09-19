/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jul 24, 2014, 2:19:53 PM
 */
package com.ceylon_linux.kandana_foods_and_drugs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ceylon_linux.kandana_foods_and_drugs.R;
import com.ceylon_linux.kandana_foods_and_drugs.controller.ItemController;
import com.ceylon_linux.kandana_foods_and_drugs.controller.OrderController;
import com.ceylon_linux.kandana_foods_and_drugs.model.Distributor;
import com.ceylon_linux.kandana_foods_and_drugs.model.Order;
import com.ceylon_linux.kandana_foods_and_drugs.model.OrderDetail;
import com.ceylon_linux.kandana_foods_and_drugs.model.Outlet;
import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class ViewInvoiceActivity extends Activity {
	public static Order order;
	private TextView txtOutlet;
	private TextView txtDate;
	private TextView txtTime;
	private TextView txtInvoiceTotal;
	private TextView txtNoOfItems;
	private Button btnFinish;
	private Button btnCancel;
	private Button btnBack;
	private LinearLayout listView;
	private ArrayList<OrderDetail> orderDetails;
	private Distributor distributor;
	private Outlet outlet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_invoice_activity);
		initialize();

		outlet = (Outlet) getIntent().getExtras().get("outlet");
		distributor = (Distributor) getIntent().getExtras().get("distributor");

		orderDetails = order.getOrderDetails();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		simpleDateFormat.applyPattern("EEEE dd MMM, yyyy");
		txtDate.setText(simpleDateFormat.format(new Date(order.getInvoiceTime())));
		simpleDateFormat.applyPattern("HH:mm:ss");
		txtTime.setText(simpleDateFormat.format(new Date(order.getInvoiceTime())));
		txtOutlet.setText(order.getOutletDescription());
		txtNoOfItems.setText(String.valueOf(orderDetails.size()));
		double invoiceTotal = 0;
		for (OrderDetail orderDetail : orderDetails) {
			invoiceTotal += (orderDetail.getPrice() * orderDetail.getQuantity());
		}
		txtInvoiceTotal.setText(String.valueOf(invoiceTotal));
		for (OrderDetail orderDetail : orderDetails) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			View convertView = inflater.inflate(R.layout.category_sub_item, null);
			TextView txtItemDescription = (TextView) convertView.findViewById(R.id.txtItemDescription);
			TextView txtFreeIssue = (TextView) convertView.findViewById(R.id.txtFreeIssue);
			TextView txtQuantity = (TextView) convertView.findViewById(R.id.txtQuantity);
			TextView txtStock = (TextView) convertView.findViewById(R.id.txtStock);
			TextView txtPackSize = (TextView) convertView.findViewById(R.id.txtPackSize);

			txtStock.setText(orderDetail.getItem().getStock() + "");
			txtItemDescription.setText(orderDetail.getItemDescription());
			txtPackSize.setText(orderDetail.getItem().getPackSize());
			txtFreeIssue.setText(Integer.toString(orderDetail.getFreeIssue()));
			txtQuantity.setText(Integer.toString(orderDetail.getQuantity()));

			listView.addView(convertView);
		}
	}

	private void initialize() {
		txtOutlet = (TextView) findViewById(R.id.txtOutlet);
		txtDate = (TextView) findViewById(R.id.txtDate);
		txtTime = (TextView) findViewById(R.id.txtTime);
		txtInvoiceTotal = (TextView) findViewById(R.id.txtInvoiceTotal);
		txtNoOfItems = (TextView) findViewById(R.id.txtNoOfItems);
		listView = (LinearLayout) findViewById(R.id.listView);
		btnBack = (Button) findViewById(R.id.btnBack);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnFinish = (Button) findViewById(R.id.btnFinish);
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnBackClicked(v);
			}
		});
		btnFinish.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnFinishClicked(v);
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnCancelClicked(v);
			}
		});
	}

	@Override
	public void onBackPressed() {
		Intent itemSelectActivity = new Intent(ViewInvoiceActivity.this, ItemSelectActivity.class);
		itemSelectActivity.putExtra("editOrder", 1);
		itemSelectActivity.putExtra("outlet", outlet);
		itemSelectActivity.putExtra("distributor", distributor);
		startActivity(itemSelectActivity);
		finish();
	}

	private void btnCancelClicked(View v) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ViewInvoiceActivity.this);
		alertBuilder.setMessage("Are you sure you want to CANCEL this order?");
		alertBuilder.setTitle(R.string.msg_title);
		alertBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent homeActivity = new Intent(ViewInvoiceActivity.this, HomeActivity.class);
				startActivity(homeActivity);
				finish();
			}
		});
		alertBuilder.setNegativeButton("NO", null);
		alertBuilder.show();
	}

	private void btnFinishClicked(View v) {
		new AsyncTask<Order, Void, Boolean>() {
			ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progressDialog = new ProgressDialog(ViewInvoiceActivity.this);
				progressDialog.setMessage("Syncing Order");
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.show();
			}

			@Override
			protected Boolean doInBackground(Order... params) {
				Order order = params[0];
				try {
					return OrderController.syncOrder(ViewInvoiceActivity.this, order.getOrderAsJson());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return false;
			}

			@Override
			protected void onPostExecute(Boolean aBoolean) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				if (aBoolean) {
					Toast.makeText(ViewInvoiceActivity.this, "Order Synced Successfully", Toast.LENGTH_LONG).show();
				} else {
					OrderController.saveOrderToDb(ViewInvoiceActivity.this, order);
					Toast.makeText(ViewInvoiceActivity.this, "Order placed in local database", Toast.LENGTH_LONG).show();
				}
				ItemController.updateStock(ViewInvoiceActivity.this, orderDetails);
				//Free Up Unnecessary Memory
				SupplierWiseItemFragment.suppliers = null;
				CategoryWiseItemFragment.categories = null;
				UnArrangedItemFragment.items = null;
				order = null;
				System.gc();

				Intent homeActivity = new Intent(ViewInvoiceActivity.this, HomeActivity.class);
				startActivity(homeActivity);
				finish();
			}
		}.execute(order);
	}

	private void btnBackClicked(View v) {
		onBackPressed();
	}
}
