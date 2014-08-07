/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jul 28, 2014, 12:44:34 PM
 */
package com.ceylon_linux.kandana_foods_and_drugs.model;

import android.content.Context;
import com.ceylon_linux.kandana_foods_and_drugs.controller.ItemController;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class Distributor implements Serializable {
	private int distributorId;
	private String distributorName;
	private ArrayList<Supplier> suppliers;

	public Distributor(int distributorId, String distributorName, ArrayList<Supplier> supplierCategories) {
		this.distributorId = distributorId;
		this.distributorName = distributorName;
		this.suppliers = supplierCategories;
		Collections.sort(this.suppliers);
	}

	public Distributor(int distributorId, String distributorName) {
		this.distributorId = distributorId;
		this.distributorName = distributorName;
	}

	public static final Distributor parseDistributor(JSONObject distributorJsonInstance) throws JSONException, IOException {
		JSONArray supplierJsonCollection = distributorJsonInstance.getJSONArray("supplier");
		int distributorId = distributorJsonInstance.getInt("u_id");
		String distributorName = distributorJsonInstance.getString("u_name");
		HashSet<Supplier> suppliers = new HashSet<Supplier>();
		for (int i = 0, SUPPLIER_LENGTH = supplierJsonCollection.length(); i < SUPPLIER_LENGTH; i++) {
			Supplier supplier = Supplier.parseSupplier(supplierJsonCollection.getJSONObject(i));
			if (supplier != null) {
				suppliers.add(supplier);
			}
		}
		return (suppliers.size() == 0) ? null : new Distributor(distributorId, distributorName, new ArrayList<Supplier>(suppliers));
	}

	public int getDistributorId() {
		return distributorId;
	}

	public void setDistributorId(int distributorId) {
		this.distributorId = distributorId;
	}

	public String getDistributorName() {
		return distributorName;
	}

	public void setDistributorName(String distributorName) {
		this.distributorName = distributorName;
	}

	public ArrayList<Supplier> getSupplierCategories() {
		return suppliers;
	}

	public void setSupplierCategories(ArrayList<Supplier> supplierCategories) {
		this.suppliers = supplierCategories;
		Collections.sort(this.suppliers);
	}

	public ArrayList<Supplier> getSupplierCategories(Context context) {
		Collections.sort(this.suppliers = ItemController.loadSuppliersFromDb(context, distributorId));
		return suppliers;
	}

	@Override
	public String toString() {
		return distributorName;
	}
}
