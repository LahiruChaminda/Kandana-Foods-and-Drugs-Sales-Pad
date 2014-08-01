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
import java.util.HashSet;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class Distributor implements Serializable {
	private int distributorId;
	private String distributorName;
	private ArrayList<SupplierCategory> supplierCategories;

	public Distributor(int distributorId, String distributorName, ArrayList<SupplierCategory> supplierCategories) {
		this.distributorId = distributorId;
		this.distributorName = distributorName;
		this.supplierCategories = supplierCategories;
	}

	public Distributor(int distributorId, String distributorName) {
		this.distributorId = distributorId;
		this.distributorName = distributorName;
	}

	public static final Distributor parseDistributor(JSONObject distributorJsonInstance) throws JSONException, IOException {
		JSONArray supplierCategoryJson = distributorJsonInstance.getJSONArray("supplier_type");
		int distributorId = distributorJsonInstance.getInt("u_id");
		String distributorName = distributorJsonInstance.getString("u_name");
		HashSet<SupplierCategory> supplierCategories = new HashSet<SupplierCategory>();
		for (int i = 0, SUPPLIER_CATEGORY_LENGTH = supplierCategoryJson.length(); i < SUPPLIER_CATEGORY_LENGTH; i++) {
			SupplierCategory supplierCategory = SupplierCategory.parseSupplierCategory(supplierCategoryJson.getJSONObject(i));
			if (supplierCategory != null) {
				supplierCategories.add(supplierCategory);
			}
		}
		return (supplierCategories.size() == 0) ? null : new Distributor(distributorId, distributorName, new ArrayList<SupplierCategory>(supplierCategories));
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

	public ArrayList<SupplierCategory> getSupplierCategories() {
		return supplierCategories;
	}

	public void setSupplierCategories(ArrayList<SupplierCategory> supplierCategories) {
		this.supplierCategories = supplierCategories;
	}

	public ArrayList<SupplierCategory> getSupplierCategories(Context context) {
		return supplierCategories = ItemController.loadSupplierCategoriesFromDb(context, distributorId);
	}

	@Override
	public String toString() {
		return distributorName;
	}
}
