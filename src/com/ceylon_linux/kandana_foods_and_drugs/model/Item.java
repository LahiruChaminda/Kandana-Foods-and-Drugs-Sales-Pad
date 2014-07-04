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

/**
 * Item - Description of Item
 *
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class Item implements Serializable {

	private int itemId;
	private String itemCode;
	private String itemDescription;
	private double price;
	private boolean selected;
	private JSONArray freeIssueJsonArray;

	public Item(int itemId, String itemCode, String itemDescription, double price) {
		this.itemId = itemId;
		this.itemCode = itemCode;
		this.itemDescription = itemDescription;
		this.price = price;
	}

	public Item(int itemId, String itemCode, String itemDescription, double price, JSONArray freeIssueJsonArray) {
		this.itemId = itemId;
		this.itemCode = itemCode;
		this.itemDescription = itemDescription;
		this.price = price;
		this.freeIssueJsonArray = freeIssueJsonArray;
	}

	public static final Item parseItem(JSONObject itemJsonInstance) throws JSONException {
		if (itemJsonInstance == null) {
			return null;
		}
		return new Item(
			itemJsonInstance.getInt("itemId"),//int itemId
			itemJsonInstance.getString("itemCode"),//int itemCode
			itemJsonInstance.getString("itemName"),//itemDescription
			0, //unitPrice
			itemJsonInstance.getJSONArray("freeIssues")
		);
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
		return freeIssueJsonArray;
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

	@Override
	public int hashCode() {
		return itemId;
	}
}
