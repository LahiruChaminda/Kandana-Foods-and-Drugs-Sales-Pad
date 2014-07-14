/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 12:20:23 PM
 */

package com.ceylon_linux.kandana_foods_and_drugs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ceylon_linux.kandana_foods_and_drugs.R;
import com.ceylon_linux.kandana_foods_and_drugs.controller.ItemController;
import com.ceylon_linux.kandana_foods_and_drugs.controller.OrderController;
import com.ceylon_linux.kandana_foods_and_drugs.controller.UserController;
import com.ceylon_linux.kandana_foods_and_drugs.model.*;
import com.ceylon_linux.kandana_foods_and_drugs.util.BatteryUtility;
import com.ceylon_linux.kandana_foods_and_drugs.util.GpsReceiver;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class SelectItemActivity extends Activity {

	private ListView itemList;
	private EditText inputSearch;
	private Button finishButton;
	private Outlet outlet;
	private ArrayList<Item> items = new ArrayList<Item>();
	private ArrayList<Item> fixedItems;
	private ArrayList<OrderDetail> orderDetails;
	private GpsReceiver gpsReceiver;
	private Thread GPS_CHECKER;
	private Location location;
	private MyListAdapter listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_items_page);
		initialize();
		outlet = (Outlet) getIntent().getExtras().get("outlet");
		try {
			ArrayList<Category> categories = ItemController.loadItemsFromDb(this);
			for (Category category : categories) {
				items.addAll(category.getItems());
			}
			Collections.sort(items);
			fixedItems = (ArrayList<Item>) items.clone();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		itemList.setAdapter(listAdapter = new MyListAdapter());
		itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int childPosition, long id) {
				final Dialog dialog = new Dialog(SelectItemActivity.this);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setTitle("Please Insert Quantity");
				dialog.setContentView(R.layout.quantity_insert_page);
				Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
				TextView txtItemDescription = (TextView) dialog.findViewById(R.id.txtItemDescription);
				final EditText inputRequestedQuantity = (EditText) dialog.findViewById(R.id.inputRequestedQuantity);
				final EditText inputSalableReturnQuantity = (EditText) dialog.findViewById(R.id.inputRequestedQuantity);
				final Item item = items.get(childPosition);
				final TextView txtFreeQuantity = (TextView) dialog.findViewById(R.id.txtFreeQuantity);
				Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
				txtItemDescription.setText(item.getItemDescription());
				inputRequestedQuantity.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
					}

					@Override
					public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
					}

					@Override
					public void afterTextChanged(Editable editable) {
						String requestedQuantityString = inputRequestedQuantity.getText().toString();
						String salableReturnQuantityString = inputSalableReturnQuantity.getText().toString();
						int salableReturnQuantity = Integer.parseInt((salableReturnQuantityString.isEmpty()) ? "0" : salableReturnQuantityString);
						int requestedQuantity = Integer.parseInt((requestedQuantityString.isEmpty()) ? "0" : requestedQuantityString);
						OrderDetail orderDetail = OrderDetail.getOrderDetail(item, requestedQuantity, salableReturnQuantity, SelectItemActivity.this);
						txtFreeQuantity.setText(String.valueOf(orderDetail.getFreeIssue()));
					}
				});
				btnOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						String requestedQuantityString = inputRequestedQuantity.getText().toString();
						String salableReturnQuantityString = inputSalableReturnQuantity.getText().toString();
						int salableReturnQuantity = Integer.parseInt((salableReturnQuantityString.isEmpty()) ? "0" : salableReturnQuantityString);
						int requestedQuantity = Integer.parseInt((requestedQuantityString.isEmpty()) ? "0" : requestedQuantityString);
						OrderDetail orderDetail = OrderDetail.getOrderDetail(item, requestedQuantity, salableReturnQuantity, SelectItemActivity.this);
						if (orderDetail != null) {
							orderDetails.add(orderDetail);
							item.setSelected(true);
						}
						dialog.dismiss();
					}
				});
				btnCancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});
		inputSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				listAdapter.getFilter().filter(inputSearch.getText());
			}
		});
		finishButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finishButtonClicked(view);
			}
		});
		finishButton.setEnabled(false);
		gpsReceiver = GpsReceiver.getGpsReceiver(SelectItemActivity.this);
		GPS_CHECKER = new Thread() {
			private Handler handler = new Handler();

			@Override
			public void run() {
				do {
					location = gpsReceiver.getLastKnownLocation();
				} while (location == null);
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(SelectItemActivity.this, "GPS Location Received", Toast.LENGTH_LONG).show();
						finishButton.setEnabled(true);
					}
				});
			}
		};
		Toast.makeText(SelectItemActivity.this, fixedItems.size() + " items loaded", Toast.LENGTH_LONG).show();
	}

	// <editor-fold defaultstate="collapsed" desc="Initialize">
	private void initialize() {
		itemList = (ListView) findViewById(R.id.itemList);
		finishButton = (Button) findViewById(R.id.finishButton);
		inputSearch = (EditText) findViewById(R.id.inputSearch);
		orderDetails = new ArrayList<OrderDetail>();
	}

	private void finishButtonClicked(View view) {
		if (orderDetails.size() == 0) {
			final AlertDialog.Builder alert = new AlertDialog.Builder(SelectItemActivity.this);
			alert.setTitle(R.string.app_name);
			alert.setMessage("Please select at least one item");
			alert.setPositiveButton("Ok", null);
			alert.show();
			return;
		}
		if ((location = gpsReceiver.getLastKnownLocation()) == null) {
			if (GPS_CHECKER.getState() == Thread.State.TERMINATED) {
				finishButton.setEnabled(false);
				GPS_CHECKER.start();
			}
			return;
		}
		final Order order = new Order(outlet.getOutletId(), UserController.getAuthorizedUser(SelectItemActivity.this).getUserId(), outlet.getRouteId(), BatteryUtility.getBatteryLevel(SelectItemActivity.this), new Date().getTime(), location.getLongitude(), location.getLatitude(), orderDetails);
		new AsyncTask<Order, Void, Boolean>() {
			ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progressDialog = new ProgressDialog(SelectItemActivity.this);
				progressDialog.setMessage("Syncing Order");
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.show();
			}

			@Override
			protected Boolean doInBackground(Order... params) {
				Order order = params[0];
				try {
					return OrderController.syncOrder(SelectItemActivity.this, order.getOrderAsJson());
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
					Toast.makeText(SelectItemActivity.this, "Order Synced Successfully", Toast.LENGTH_LONG).show();
				} else {
					OrderController.saveOrderToDb(SelectItemActivity.this, order);
					Toast.makeText(SelectItemActivity.this, "Order placed in local database", Toast.LENGTH_LONG).show();
				}
				Intent homeActivity = new Intent(SelectItemActivity.this, HomeActivity.class);
				startActivity(homeActivity);
				finish();
			}
		}.execute(order);
	}
	// </editor-fold>

	@Override
	public void onBackPressed() {
		Intent loadAddInvoiceActivity = new Intent(SelectItemActivity.this, LoadAddInvoiceActivity.class);
		startActivity(loadAddInvoiceActivity);
		finish();
	}

	private ChildViewHolder updateView(ChildViewHolder childViewHolder, Item item) {
		for (OrderDetail orderDetail : orderDetails) {
			if (orderDetail.getItemId() == item.getItemId()) {
				childViewHolder.txtFreeIssue.setText(Integer.toString(orderDetail.getFreeIssue()));
				childViewHolder.txtQuantity.setText(Integer.toString(orderDetail.getQuantity()));
				return childViewHolder;
			}
		}
		childViewHolder.txtFreeIssue.setText("0");
		childViewHolder.txtQuantity.setText("0");
		return childViewHolder;
	}

	private static class ChildViewHolder {

		TextView txtItemDescription;
		CheckBox checkBox;
		TextView txtQuantity;
		TextView txtFreeIssue;
	}

	private class MyListAdapter extends BaseAdapter implements Filterable {
		MyFilter myFilter;

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Item getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ChildViewHolder childViewHolder;
			if (view == null) {
				LayoutInflater layoutInflater = (LayoutInflater) SelectItemActivity.this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				view = layoutInflater.inflate(R.layout.category_sub_item, null);
				childViewHolder = new ChildViewHolder();
				childViewHolder.txtItemDescription = (TextView) view.findViewById(R.id.txtItemDescription);
				childViewHolder.txtFreeIssue = (TextView) view.findViewById(R.id.txtFreeIssue);
				childViewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
				childViewHolder.txtQuantity = (TextView) view.findViewById(R.id.txtQuantity);
				view.setTag(childViewHolder);
			} else {
				childViewHolder = (ChildViewHolder) view.getTag();
			}
			Item item = getItem(position);
			childViewHolder.txtItemDescription.setText(item.getItemDescription());
			childViewHolder.checkBox.setChecked(item.isSelected());
			view.setBackgroundColor((position % 2 == 0) ? Color.parseColor("#E6E6E6") : Color.parseColor("#FFFFFF"));
			updateView(childViewHolder, item);
			return view;
		}

		@Override
		public Filter getFilter() {
			if (myFilter == null) {
				myFilter = new MyFilter();
			}
			return myFilter;
		}

		private class MyFilter extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				constraint.toString().toLowerCase();
				FilterResults result = new FilterResults();
				ArrayList<Item> filteredItems = new ArrayList<Item>();
				if (constraint != null && constraint.toString().length() > 0) {
					for (Item item : fixedItems) {
						if (item.getItemDescription().toLowerCase().contains(constraint)) {
							filteredItems.add(item);
						}
					}
				} else {
					filteredItems = fixedItems;
				}
				result.count = filteredItems.size();
				result.values = filteredItems;
				return result;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				items = (ArrayList<Item>) results.values;
				notifyDataSetChanged();
			}
		}
	}
}
