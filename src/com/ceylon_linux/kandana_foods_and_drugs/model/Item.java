/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 7, 2014, 11:06:25 AM
 */

package com.ceylon_linux.kandana_foods_and_drugs.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.NumberFormat;

/**
 * Item - Description of Item
 *
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class Item implements Serializable, Comparable {

	private transient static final NumberFormat NUMBER_FORMATTER = NumberFormat.getInstance();

	static {
		if (NUMBER_FORMATTER != null) {
			NUMBER_FORMATTER.setGroupingUsed(false);
			NUMBER_FORMATTER.setMaximumFractionDigits(2);
			NUMBER_FORMATTER.setMinimumFractionDigits(2);
		}
	}

	private final int FIXED_STOCK;
	private int itemId;
	private String itemCode;
	private String itemDescription;
	private double price;
	private String packSize;
	private int stock;
	private boolean selected;
	private String freeIssueJsonArrayString;
	private int rp_id;
	private int supplierId;
	private int categoryId;

	public Item(int itemId, String itemCode, String itemDescription, double price, String packSize, int stock, int rp_id) {
		this.itemId = itemId;
		this.itemCode = itemCode;
		this.itemDescription = itemDescription;
		this.price = price;
		this.packSize = packSize;
		this.stock = stock;
		this.FIXED_STOCK = stock;
		this.rp_id = rp_id;
	}

	public Item(int itemId, String itemCode, String itemDescription, double price, JSONArray freeIssueJsonArray, String packSize, int stock, int rp_id) {
		this.itemId = itemId;
		this.itemCode = itemCode;
		this.itemDescription = itemDescription;
		this.price = price;
		this.freeIssueJsonArrayString = freeIssueJsonArray.toString();
		this.packSize = packSize;
		this.stock = stock;
		this.FIXED_STOCK = stock;
		this.rp_id = rp_id;
	}

	public static final Item parseItem(JSONObject itemJsonInstance) throws JSONException {
		if (itemJsonInstance == null) {
			return null;
		}
		double price = itemJsonInstance.getDouble("price");
		price = Double.parseDouble(NUMBER_FORMATTER.format(price));
		Item item = new Item(
			itemJsonInstance.getInt("itemId"),//int itemId
			itemJsonInstance.getString("itemCode"),//int itemCode
			itemJsonInstance.getString("itemName"),//itemDescription
			price, //unitPrice
			itemJsonInstance.getJSONArray("freeIssues"),
			itemJsonInstance.getString("packSize"),
			itemJsonInstance.getInt("stock"),
			itemJsonInstance.getInt("rp_id")
		);
		item.setSupplierId(itemJsonInstance.getInt("supp_id"));
		item.setCategoryId(itemJsonInstance.getInt("cat_id"));
		return item;
	}

	public int getRp_id() {
		return rp_id;
	}

	public void setRp_id(int rp_id) {
		this.rp_id = rp_id;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public JSONArray getFreeIssueJsonArray() {
		try {
			return new JSONArray(freeIssueJsonArrayString);
		} catch (JSONException e) {
			e.printStackTrace();
			return new JSONArray();
		}
	}

	@Override
	public String toString() {
		return itemDescription;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		Item item = (Item) o;
		if (itemId != item.itemId) return false;
		return true;
	}

	public String getPackSize() {
		return packSize;
	}

	public void setPackSize(String packSize) {
		this.packSize = packSize;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public int getFIXED_STOCK() {
		return FIXED_STOCK;
	}

	@Override
	public int hashCode() {
		return itemId;
	}

	@Override
	public int compareTo(Object another) {
		Item anotherItem = (Item) another;
		return itemDescription.compareTo(anotherItem.getItemDescription());
	}

	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
}
