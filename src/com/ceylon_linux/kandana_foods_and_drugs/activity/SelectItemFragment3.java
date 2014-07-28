/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 12:20:23 PM
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
import com.ceylon_linux.kandana_foods_and_drugs.controller.ItemController;
import com.ceylon_linux.kandana_foods_and_drugs.model.Item;
import com.ceylon_linux.kandana_foods_and_drugs.model.OrderDetail;
import com.ceylon_linux.kandana_foods_and_drugs.model.Supplier;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class SelectItemFragment3 extends ItemSelectableFragment {

	private ListView itemList;
	private EditText inputSearch;
	private ImageButton btnClear;
	private ArrayList<Item> items = new ArrayList<Item>();
	private ArrayList<Item> fixedItems;
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
		try {
			ArrayList<Supplier> categories = ItemController.loadItemsFromDb(getActivity());
			for (Supplier supplier : categories) {
				items.addAll(supplier.getItems());
			}
			Collections.sort(items);
			fixedItems = (ArrayList<Item>) items.clone();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.select_items_page_method_three, null);
		initialize(rootView);
		if (listAdapter != null) {
			listAdapter.notifyDataSetChanged();
			itemList.setAdapter(listAdapter);
		}
		itemList.setAdapter(listAdapter = new MyListAdapter());
		itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int childPosition, long id) {
				itemListItemClicked(parent, view, childPosition, id);
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

		return rootView;
	}

	private void itemListItemClicked(AdapterView<?> parent, View view, int childPosition, long id) {
		final Dialog dialog = new Dialog(getActivity());
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
				OrderDetail orderDetail = OrderDetail.getOrderDetail(item, requestedQuantity, salableReturnQuantity, getActivity());
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
				OrderDetail orderDetail = OrderDetail.getOrderDetail(item, requestedQuantity, salableReturnQuantity, getActivity());
				if (orderDetail != null) {
					orderDetails.add(orderDetail);
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
		btnClear = (ImageButton) rootView.findViewById(R.id.btnClear);
		btnClear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnClearPressed(v);
			}
		});
	}
	// </editor-fold>

	private void btnClearPressed(View view) {
		inputSearch.setText("");
	}

	private ChildViewHolder updateView(ChildViewHolder childViewHolder, Item item) {
		childViewHolder.txtItemDescription.setText(item.getItemDescription());
		for (OrderDetail orderDetail : orderDetails) {
			if (orderDetail.getItemId() == item.getItemId()) {
				childViewHolder.txtFreeIssue.setText(Integer.toString(orderDetail.getFreeIssue()));
				childViewHolder.txtQuantity.setText(Integer.toString(orderDetail.getQuantity()));
				childViewHolder.imageView.setBackgroundResource(R.drawable.right);
				return childViewHolder;
			}
		}
		childViewHolder.txtFreeIssue.setText("0");
		childViewHolder.txtQuantity.setText("0");
		childViewHolder.imageView.setBackgroundDrawable(null);
		return childViewHolder;
	}

	private static class ChildViewHolder {

		TextView txtItemDescription;
		ImageView imageView;
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
				LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				view = layoutInflater.inflate(R.layout.category_sub_item, null);
				childViewHolder = new ChildViewHolder();
				childViewHolder.txtItemDescription = (TextView) view.findViewById(R.id.txtItemDescription);
				childViewHolder.txtFreeIssue = (TextView) view.findViewById(R.id.txtFreeIssue);
				childViewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);
				childViewHolder.txtQuantity = (TextView) view.findViewById(R.id.txtQuantity);
				view.setTag(childViewHolder);
			} else {
				childViewHolder = (ChildViewHolder) view.getTag();
			}
			Item item = getItem(position);
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
				String searchTerm = constraint.toString().toLowerCase();
				FilterResults result = new FilterResults();
				ArrayList<Item> filteredItems = new ArrayList<Item>();
				if (constraint != null && constraint.toString().length() > 0) {
					for (Item item : fixedItems) {
						if (item.getItemDescription().toLowerCase().startsWith(searchTerm)) {
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
