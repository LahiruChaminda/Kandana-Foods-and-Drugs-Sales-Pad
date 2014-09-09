/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jul 23, 2014, 3:13:48 PM
 */
package com.ceylon_linux.kandana_foods_and_drugs.activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ceylon_linux.kandana_foods_and_drugs.R;
import com.ceylon_linux.kandana_foods_and_drugs.model.OrderDetail;

import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class SelectedItemsFragment extends ItemSelectableFragment {

	private ListView itemList;
	private EditText inputSearch;
	private MyListAdapter listAdapter;
	private ArrayList<OrderDetail> orderDetails;

	@Override
	public void updateUI() {
		if (listAdapter != null) {
			listAdapter.notifyDataSetChanged();
			itemList.setAdapter(listAdapter);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		orderDetails = ((ItemSelectActivity) getActivity()).getOrderDetails();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.selected_items_page, null);
		initialize(rootView);
		if (listAdapter != null) {
			listAdapter.notifyDataSetChanged();
			itemList.setAdapter(listAdapter);
		}
		itemList.setAdapter(listAdapter = new MyListAdapter());
		return rootView;
	}

	private void itemListClicked(int childPosition) {
		final Dialog dialog = new Dialog(getActivity());
		dialog.setCanceledOnTouchOutside(false);
		dialog.setTitle("Please Insert Quantity");
		dialog.setContentView(R.layout.quantity_insert_page);
		Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
		TextView txtItemDescription = (TextView) dialog.findViewById(R.id.txtItemDescription);
		final EditText inputRequestedQuantity = (EditText) dialog.findViewById(R.id.inputRequestedQuantity);
		final EditText inputSalableReturnQuantity = (EditText) dialog.findViewById(R.id.inputSalableReturnQuantity);
		final OrderDetail orderDetail = orderDetails.get(childPosition);
		final TextView txtFreeQuantity = (TextView) dialog.findViewById(R.id.txtFreeQuantity);
		Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
		txtItemDescription.setText(orderDetail.getItemDescription());
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
				OrderDetail newOrderDetail = OrderDetail.getOrderDetail(orderDetail.getItem(), requestedQuantity, salableReturnQuantity, getActivity());
				txtFreeQuantity.setText(String.valueOf(newOrderDetail.getFreeIssue()));
			}
		});
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String requestedQuantityString = inputRequestedQuantity.getText().toString();
				String salableReturnQuantityString = inputSalableReturnQuantity.getText().toString();
				int salableReturnQuantity = Integer.parseInt((salableReturnQuantityString.isEmpty()) ? "0" : salableReturnQuantityString);
				int requestedQuantity = Integer.parseInt((requestedQuantityString.isEmpty()) ? "0" : requestedQuantityString);
				OrderDetail newOrderDetail = OrderDetail.getOrderDetail(orderDetail.getItem(), requestedQuantity, salableReturnQuantity, getActivity());
				if (newOrderDetail != null) {
					orderDetails.add(newOrderDetail);
					listAdapter.notifyDataSetChanged();
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

	// <editor-fold defaultstate="collapsed" desc="Initialize">
	private void initialize(View rootView) {
		itemList = (ListView) rootView.findViewById(R.id.itemList);
		inputSearch = (EditText) rootView.findViewById(R.id.inputSearch);
	}
	// </editor-fold>

	private static class ChildViewHolder {
		TextView txtItemDescription;
		ImageButton editButton;
		TextView txtQuantity;
		TextView txtFreeIssue;
		ImageButton removeButton;
	}

	private class MyListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return orderDetails.size();
		}

		@Override
		public OrderDetail getItem(int position) {
			return orderDetails.get(position);
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
		public View getView(final int position, View view, ViewGroup parent) {
			ChildViewHolder childViewHolder;
			if (view == null) {
				LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				view = layoutInflater.inflate(R.layout.selected_order_detail, null);
				childViewHolder = new ChildViewHolder();
				childViewHolder.txtItemDescription = (TextView) view.findViewById(R.id.txtItemDescription);
				childViewHolder.txtFreeIssue = (TextView) view.findViewById(R.id.txtFreeIssue);
				childViewHolder.editButton = (ImageButton) view.findViewById(R.id.editButton);
				childViewHolder.txtQuantity = (TextView) view.findViewById(R.id.txtQuantity);
				childViewHolder.removeButton = (ImageButton) view.findViewById(R.id.deleteButton);
				view.setTag(childViewHolder);
			} else {
				childViewHolder = (ChildViewHolder) view.getTag();
			}
			final OrderDetail orderDetail = getItem(position);
			childViewHolder.txtItemDescription.setText(orderDetail.getItemDescription());
			childViewHolder.txtFreeIssue.setText(Integer.toString(orderDetail.getFreeIssue()));
			childViewHolder.txtQuantity.setText(Integer.toString(orderDetail.getQuantity()));
			childViewHolder.editButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListClicked(position);
				}
			});
			childViewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					orderDetails.remove(orderDetail);
					listAdapter.notifyDataSetChanged();
				}
			});
			view.setBackgroundColor((position % 2 == 0) ? Color.parseColor("#E6E6E6") : Color.parseColor("#FFFFFF"));
			return view;
		}
	}
}
