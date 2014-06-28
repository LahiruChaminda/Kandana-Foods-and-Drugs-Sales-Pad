/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 8:21:21 PM
 */

package com.ceylon_linux.lucky_lanka.model;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class OrderDetail {

	private int itemId;
	private String itemDescription;
	private int quantity;
	private double price;

	public OrderDetail(int itemId, String itemDescription, int quantity, double price) {
		this.itemId = itemId;
		this.itemDescription = itemDescription;
		this.quantity = quantity;
		this.price = price;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public JSONObject getOrderDetailAsJson() {
		HashMap<String, Object> orderDetailsParams = new HashMap<String, Object>();
		orderDetailsParams.put("id_item", itemId);
		orderDetailsParams.put("qty", quantity);
		orderDetailsParams.put("price", price);
		return new JSONObject(orderDetailsParams);
	}

	@Override
	public String toString() {
		return itemDescription;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		OrderDetail that = (OrderDetail) o;

		if (itemId != that.itemId) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return itemId;
	}
}
