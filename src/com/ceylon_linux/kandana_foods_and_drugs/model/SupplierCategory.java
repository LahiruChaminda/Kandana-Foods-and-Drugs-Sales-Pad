/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jul 21, 2014, 1:27:42 PM
 */
package com.ceylon_linux.kandana_foods_and_drugs.model;

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
public class SupplierCategory implements Serializable {
	private int supplierCategoryId;
	private String supplierCategory;
	private ArrayList<Supplier> suppliers;

	public SupplierCategory(int supplierCategoryId, String supplierCategory, ArrayList<Supplier> suppliers) {
		this.supplierCategoryId = supplierCategoryId;
		this.supplierCategory = supplierCategory;
		this.suppliers = suppliers;
	}

	public static final SupplierCategory parseSupplierCategory(JSONObject jsonInstance) throws IOException, JSONException {
		if (jsonInstance == null) {
			return null;
		}
		HashSet<Supplier> suppliers = new HashSet<Supplier>();
		JSONArray supplierCollection = jsonInstance.getJSONArray("suppliers");
		for (int i = 0, SUPPLIER_COLLECTION_SIZE = supplierCollection.length(); i < SUPPLIER_COLLECTION_SIZE; i++) {
			Supplier supplier = Supplier.parseSupplier(supplierCollection.getJSONObject(i));
			if (supplier != null) {
				suppliers.add(supplier);
			}
		}
		return (suppliers.size() == 0) ? null : new SupplierCategory(
			jsonInstance.getInt("supplierCategoryId"),
			jsonInstance.getString("supplierCategory"),
			new ArrayList<Supplier>(suppliers)
		);
	}

	public int getSupplierCategoryId() {
		return supplierCategoryId;
	}

	public void setSupplierCategoryId(int supplierCategoryId) {
		this.supplierCategoryId = supplierCategoryId;
	}

	public String getSupplierCategory() {
		return supplierCategory;
	}

	public void setSupplierCategory(String supplierCategory) {
		this.supplierCategory = supplierCategory;
	}

	public ArrayList<Supplier> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(ArrayList<Supplier> suppliers) {
		this.suppliers = suppliers;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || SupplierCategory.class != o.getClass()) return false;
		SupplierCategory that = (SupplierCategory) o;
		if (supplierCategoryId != that.supplierCategoryId) return false;
		return true;
	}

	@Override
	public int hashCode() {
		return supplierCategoryId;
	}

	@Override
	public String toString() {
		return supplierCategory;
	}
}
