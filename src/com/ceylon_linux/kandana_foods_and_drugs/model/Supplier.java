/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jul 21, 2014, 1:27:42 PM
 */
package com.ceylon_linux.kandana_foods_and_drugs.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class Supplier implements Serializable, Comparable {
	private int supplierId;
	private String supplierName;
	private ArrayList<Category> categories;

	public Supplier(int supplierCategoryId, String supplierName, ArrayList<Category> categories) {
		this.supplierId = supplierCategoryId;
		this.supplierName = supplierName;
		this.categories = categories;
		if (this.categories != null) {
			Collections.sort(this.categories);
		}
	}

	public static final Supplier parseSupplier(JSONObject jsonInstance) throws IOException, JSONException {
		if (jsonInstance == null) {
			return null;
		}
		return new Supplier(
			jsonInstance.getInt("supplierId"),
			jsonInstance.getString("supplierName"),
			null
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
		Collections.sort(this.categories);
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

	@Override
	public int compareTo(Object another) {
		Supplier anotherItem = (Supplier) another;
		return supplierName.compareTo(anotherItem.getSupplierName());
	}
}
