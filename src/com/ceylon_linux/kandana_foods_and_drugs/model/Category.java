/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 12:27:01 PM
 */

package com.ceylon_linux.kandana_foods_and_drugs.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class Category implements Serializable {

	private ArrayList<Item> items;
	private String categoryDescription;
	private int categoryId;

	public Category(int categoryId, String categoryDescription, ArrayList<Item> items) {
		this.items = items;
		this.categoryDescription = categoryDescription;
		this.categoryId = categoryId;
	}

	public static final Category parseCategory(JSONObject categoryJsonInstance) throws JSONException {
		if (categoryJsonInstance == null) {
			return null;
		}
		HashSet<Item> items = new HashSet<Item>();
		JSONArray itemCollection = categoryJsonInstance.getJSONArray("product");
		for (int i = 0, ITEM_SIZE = itemCollection.length(); i < ITEM_SIZE; i++) {
			Item item = Item.parseItem(itemCollection.getJSONObject(i));
			if (item != null) {
				items.add(item);
			}
		}
		return (items.size() == 0) ? null : new Category(
			categoryJsonInstance.getInt("categoryId"),
			categoryJsonInstance.getString("categoryName"),
			new ArrayList<Item>(items)
		);
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}

	public String getCategoryDescription() {
		return categoryDescription;
	}

	public void setCategoryDescription(String categoryDescription) {
		this.categoryDescription = categoryDescription;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Category.class != o.getClass()) return false;

		Category category = (Category) o;

		if (categoryId != category.categoryId) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return categoryId;
	}

	@Override
	public String toString() {
		return categoryDescription;
	}
}
