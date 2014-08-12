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
import com.ceylon_linux.kandana_foods_and_drugs.model.Category;
import com.ceylon_linux.kandana_foods_and_drugs.model.Item;
import com.ceylon_linux.kandana_foods_and_drugs.model.OrderDetail;
import com.ceylon_linux.kandana_foods_and_drugs.model.Supplier;

import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class SelectItemFragment1 extends ItemSelectableFragment {

	public static ArrayList<Supplier> suppliers;
	private ExpandableListView itemList;
	private Spinner supplerSpinner;
	private EditText inputSearch;
	private ImageButton btnClear;
	private ArrayList<Category> categories = new ArrayList<Category>();
	private ArrayList<Category> fixedCategories;
	private ArrayList<OrderDetail> orderDetails;

	private MyExpandableListAdapter myExpandableListAdapter;

	@Override
	public void updateUI() {
		if (myExpandableListAdapter != null) {
			myExpandableListAdapter.notifyDataSetChanged();
			itemList.setAdapter(myExpandableListAdapter);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ItemSelectActivity itemSelectActivity = (ItemSelectActivity) getActivity();
		orderDetails = itemSelectActivity.getOrderDetails();
		fixedCategories = (ArrayList<Category>) categories.clone();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.select_items_page_method_one, null);
		initialize(rootView);
		itemList.setAdapter(myExpandableListAdapter = new MyExpandableListAdapter());
		itemList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
				return itemListOnChildClicked(expandableListView, view, groupPosition, childPosition, id);
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
				myExpandableListAdapter.getFilter().filter(inputSearch.getText());
			}
		});
		supplerSpinner.setAdapter(new ArrayAdapter<Supplier>(getActivity(), android.R.layout.simple_list_item_1, suppliers));
		supplerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Supplier supplier = (Supplier) parent.getAdapter().getItem(position);
				categories.clear();
				fixedCategories.clear();
				categories.addAll(supplier.getCategories());
				fixedCategories.addAll(supplier.getCategories());
				myExpandableListAdapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		return rootView;
	}

	private boolean itemListOnChildClicked(ExpandableListView expandableListView, View view, final int groupPosition, int childPosition, long id) {
		final Dialog dialog = new Dialog(getActivity());
		dialog.setCanceledOnTouchOutside(false);
		dialog.setTitle("Please Insert Quantity");
		dialog.setContentView(R.layout.quantity_insert_page);
		Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
		TextView txtItemDescription = (TextView) dialog.findViewById(R.id.txtItemDescription);
		final EditText inputRequestedQuantity = (EditText) dialog.findViewById(R.id.inputRequestedQuantity);
		final EditText inputSalableReturnQuantity = (EditText) dialog.findViewById(R.id.inputRequestedQuantity);
		final Item item = categories.get(groupPosition).getItems().get(childPosition);
		final TextView txtFreeQuantity = (TextView) dialog.findViewById(R.id.txtFreeQuantity);
		TextView txtUnitPrice = (TextView) dialog.findViewById(R.id.txtUnitPrice);
		txtUnitPrice.setText(item.getPrice() + "");
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
					if (item.getFIXED_STOCK() > (orderDetail.getQuantity() + orderDetail.getFreeIssue())) {
						orderDetails.add(orderDetail);
						item.setStock(item.getFIXED_STOCK() - orderDetail.getQuantity() + orderDetail.getFreeIssue());
						myExpandableListAdapter.notifyDataSetChanged();
					} else {
						Toast.makeText(getActivity(), "Out of Quantity", Toast.LENGTH_LONG).show();
					}
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
		return true;
	}

	// <editor-fold defaultstate="collapsed" desc="Initialize">
	private void initialize(View rootView) {
		itemList = (ExpandableListView) rootView.findViewById(R.id.itemList);
		inputSearch = (EditText) rootView.findViewById(R.id.inputSearch);
		supplerSpinner = (Spinner) rootView.findViewById(R.id.supplierSpinner);
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
		childViewHolder.txtPackSize.setText(item.getPackSize());
		for (OrderDetail orderDetail : orderDetails) {
			if (orderDetail.getItemId() == item.getItemId()) {
				childViewHolder.txtFreeIssue.setText(Integer.toString(orderDetail.getFreeIssue()));
				childViewHolder.txtQuantity.setText(Integer.toString(orderDetail.getQuantity()));
				childViewHolder.txtStock.setText(item.getFIXED_STOCK() - (orderDetail.getFreeIssue() + orderDetail.getQuantity()) + "");
				childViewHolder.imageView.setBackgroundResource(R.drawable.right);
				return childViewHolder;
			}
		}
		childViewHolder.txtStock.setText(item.getFIXED_STOCK() + "");
		childViewHolder.txtFreeIssue.setText("0");
		childViewHolder.txtQuantity.setText("0");
		childViewHolder.imageView.setBackgroundDrawable(null);
		return childViewHolder;
	}

	private static class GroupViewHolder {

		TextView txtCategory;
	}

	private static class ChildViewHolder {

		TextView txtItemDescription;
		ImageView imageView;
		TextView txtQuantity;
		TextView txtFreeIssue;
		TextView txtPackSize;
		TextView txtStock;
	}

	private class MyExpandableListAdapter extends BaseExpandableListAdapter implements Filterable {
		MyFilter myFilter;

		@Override
		public int getGroupCount() {
			return categories.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return categories.get(groupPosition).getItems().size();
		}

		@Override
		public Category getGroup(int groupPosition) {
			return categories.get(groupPosition);
		}

		@Override
		public Item getChild(int groupPosition, int childPosition) {
			return categories.get(groupPosition).getItems().get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
			GroupViewHolder groupViewHolder;
			if (view == null) {
				LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				view = layoutInflater.inflate(R.layout.category_item_view, null);
				groupViewHolder = new GroupViewHolder();
				groupViewHolder.txtCategory = (TextView) view.findViewById(R.id.txtCategory);
				view.setTag(groupViewHolder);
			} else {
				groupViewHolder = (GroupViewHolder) view.getTag();
			}
			groupViewHolder.txtCategory.setText(getGroup(groupPosition).getCategoryDescription());
			return view;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
			ChildViewHolder childViewHolder;
			if (view == null) {
				LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				view = layoutInflater.inflate(R.layout.category_sub_item, null);
				childViewHolder = new ChildViewHolder();
				childViewHolder.txtItemDescription = (TextView) view.findViewById(R.id.txtItemDescription);
				childViewHolder.txtFreeIssue = (TextView) view.findViewById(R.id.txtFreeIssue);
				childViewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);
				childViewHolder.txtQuantity = (TextView) view.findViewById(R.id.txtQuantity);
				childViewHolder.txtPackSize = (TextView) view.findViewById(R.id.txtPackSize);
				childViewHolder.txtStock = (TextView) view.findViewById(R.id.txtStock);
				view.setTag(childViewHolder);
			} else {
				childViewHolder = (ChildViewHolder) view.getTag();
			}
			Item item = getChild(groupPosition, childPosition);
			view.setBackgroundColor((childPosition % 2 == 0) ? Color.parseColor("#E6E6E6") : Color.parseColor("#FFFFFF"));
			if (item.getFIXED_STOCK() == 0) {
				view.setBackgroundColor(Color.parseColor("#FF0000"));
			}
			updateView(childViewHolder, item);
			return view;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
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
				String searchTerm;
				FilterResults result = new FilterResults();
				ArrayList<Category> filteredCategories = new ArrayList<Category>();
				if (constraint != null && !(searchTerm = constraint.toString().toLowerCase()).isEmpty()) {
					for (Category category : fixedCategories) {
						ArrayList<Item> items = new ArrayList<Item>();
						for (Item item : category.getItems()) {
							if (item.getItemDescription().toLowerCase().startsWith(searchTerm)) {
								items.add(item);
							}
						}
						if (items.size() != 0) {
							filteredCategories.add(new Category(category.getCategoryId(), category.getCategoryDescription(), items));
						}
					}
				} else {
					filteredCategories = fixedCategories;
				}
				result.count = filteredCategories.size();
				result.values = filteredCategories;
				return result;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				categories.clear();
				categories.addAll((ArrayList<Category>) results.values);
				notifyDataSetChanged();
			}
		}
	}
}
