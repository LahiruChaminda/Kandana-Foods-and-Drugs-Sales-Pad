/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jul 24, 2014, 2:19:53 PM
 */
package com.ceylon_linux.kandana_foods_and_drugs.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.ceylon_linux.kandana_foods_and_drugs.R;
import com.ceylon_linux.kandana_foods_and_drugs.controller.OrderController;
import com.ceylon_linux.kandana_foods_and_drugs.model.Order;
import com.ceylon_linux.kandana_foods_and_drugs.model.OrderDetail;
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
	private TextView txtOutlet;
	private TextView txtDate;
	private TextView txtTime;
	private TextView txtInvoiceTotal;
	private TextView txtNoOfItems;
	private ListView listView;
	private Button btnFinish;
	private Button btnCancel;
	private Button btnBack;
	private Order order;
	private ArrayList<OrderDetail> orderDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_invoice_activity);
		initialize();
		orderDetails = order.getOrderDetails();
		order = (Order) getIntent().getSerializableExtra("order");
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
		/*listView.setAdapter(new BaseAdapter() {
			@Override
			public int getCount() {
				return orderDetails.size();
			}

			@Override
			public Object getItem(int position) {
				return orderDetails.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				return null;
			}
		});*/
	}

	private void initialize() {
		txtOutlet = (TextView) findViewById(R.id.txtOutlet);
		txtDate = (TextView) findViewById(R.id.txtDate);
		txtTime = (TextView) findViewById(R.id.txtTime);
		txtInvoiceTotal = (TextView) findViewById(R.id.txtInvoiceTotal);
		txtNoOfItems = (TextView) findViewById(R.id.txtNoOfItems);
		listView = (ListView) findViewById(R.id.listView);
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
		itemSelectActivity.putExtra("order", order);
	}

	private void btnCancelClicked(View v) {
		Intent homeActivity = new Intent(ViewInvoiceActivity.this, HomeActivity.class);
		startActivity(homeActivity);
		finish();
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
