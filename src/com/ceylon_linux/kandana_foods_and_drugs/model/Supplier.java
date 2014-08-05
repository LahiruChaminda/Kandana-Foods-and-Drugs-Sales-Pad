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
	private int supplierCategoryId;
	private String supplierCategory;
	private ArrayList<Category> categories;

	public Supplier(int supplierCategoryId, String supplierCategory, ArrayList<Category> categories) {
		this.supplierCategoryId = supplierCategoryId;
		this.supplierCategory = supplierCategory;
		this.categories = categories;
	}

	public static final Supplier parseSupplier(JSONObject jsonInstance) throws IOException, JSONException {
		if (jsonInstance == null) {
			return null;
		}
		HashSet<Category> categories = new HashSet<Category>();
		JSONArray categoryCollection = jsonInstance.getJSONArray("category");
		for (int i = 0, CATEGORY_COLLECTION_SIZE = categoryCollection.length(); i < CATEGORY_COLLECTION_SIZE; i++) {
			Category category = Category.parseCategory(categoryCollection.getJSONObject(i));
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
