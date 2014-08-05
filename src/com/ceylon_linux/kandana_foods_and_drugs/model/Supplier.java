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
public class Supplier implements Serializable {
	private int supplierId;
	private String supplierName;
	private ArrayList<Category> categories;

	public Supplier(int supplierCategoryId, String supplierName, ArrayList<Category> categories) {
		this.supplierId = supplierCategoryId;
		this.supplierName = supplierName;
		this.categories = categories;
	}

	public static final Supplier parseSupplier(JSONObject jsonInstance) throws IOException, JSONException {
		if (jsonInstance == null) {
			return null;
		}
		HashSet<Category> categories = new HashSet<Category>();
		JSONArray categoryJsonCollection = jsonInstance.getJSONArray("category");
		for (int i = 0, CATEGORY_SIZE = categoryJsonCollection.length(); i < CATEGORY_SIZE; i++) {
			Category category = Category.parseCategory(categoryJsonCollection.getJSONObject(i));
			if (category != null) {
				categories.add(category);
			}
		}
		return (categories.size() == 0) ? null : new Supplier(
			jsonInstance.getInt("supplierId"),
			jsonInstance.getString("supplierName"),
			new ArrayList<Category>(categories)
		);
	}

	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public ArrayList<Category> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<Category> categories) {
		this.categories = categories;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Supplier.class != o.getClass()) return false;
		Supplier that = (Supplier) o;
		if (supplierId != that.supplierId) return false;
		return true;
	}

	@Override
	public int hashCode() {
		return supplierId;
	}

	@Override
	public String toString() {
		return supplierName;
	}
}
