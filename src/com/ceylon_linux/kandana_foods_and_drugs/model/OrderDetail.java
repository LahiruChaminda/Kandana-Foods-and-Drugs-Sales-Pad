/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 8:21:21 PM
 */

package com.ceylon_linux.kandana_foods_and_drugs.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.ceylon_linux.kandana_foods_and_drugs.db.DbHandler;
import com.ceylon_linux.kandana_foods_and_drugs.db.SQLiteDatabaseHelper;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class OrderDetail implements Serializable {

	private int itemId;
	private String itemDescription;
	private int quantity;
	private int freeIssue;
	private double price;
	private int salableReturns;
	private int rp_id;
	private int stock;
	private Item item;

	public OrderDetail(int itemId, String itemDescription, int quantity, double price, int freeIssue, int salableReturns, int rp_id, int stock) {
		this.itemId = itemId;
		this.itemDescription = itemDescription;
		this.quantity = quantity;
		this.price = price;
		this.freeIssue = freeIssue;
		this.salableReturns = salableReturns;
		this.rp_id = rp_id;
		this.stock = stock;
	}

	public OrderDetail(Item item, int quantity, int freeIssue, int salableReturns, int rp_id, int stock) {
		this.item = item;
		this.itemId = item.getItemId();
		this.itemDescription = item.getItemDescription();
		this.quantity = quantity;
		this.price = item.getPrice();
		this.freeIssue = freeIssue;
		this.salableReturns = salableReturns;
		this.rp_id = rp_id;
		this.stock = stock;
	}

	public static OrderDetail getOrderDetail(Item item, int quantity, int salableReturns, Context context) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		String freeIssueSql = "select rangeMinimumQuantity,freeIssueQuantity from tbl_free_issue_ratio where itemId=? and rangeMinimumQuantity<=? order by rangeMinimumQuantity asc limit 1";
		Cursor freeIssueCursor = DbHandler.performRawQuery(database, freeIssueSql, new Object[]{item.getItemId(), quantity});
		int freeIssue = 0;
		for (freeIssueCursor.moveToFirst(); !freeIssueCursor.isAfterLast(); freeIssueCursor.moveToNext()) {
			int rangeMinimumQuantity = freeIssueCursor.getInt(0);
			int freeIssueQuantity = freeIssueCursor.getInt(1);
			if (rangeMinimumQuantity != 0) {
				freeIssue = (quantity / rangeMinimumQuantity) * freeIssueQuantity;
			}
		}
		return new OrderDetail(
			item,
			quantity,
			freeIssue,
			salableReturns,
			item.getRp_id(),
			item.getStock() - quantity
		);
	}

	public int getRp_id() {
		return rp_id;
	}

	public int getStock() {
		return stock;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
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

	public int getFreeIssue() {
		return freeIssue;
	}

	public void setFreeIssue(int freeIssue) {
		this.freeIssue = freeIssue;
	}

	public int getSalableReturns() {
		return salableReturns;
	}

	public void setSalableReturns(int salableReturns) {
		this.salableReturns = salableReturns;
	}

	public JSONObject getOrderDetailAsJson() {
		HashMap<String, Object> orderDetailsParams = new HashMap<String, Object>();
		orderDetailsParams.put("itemId", itemId);
		orderDetailsParams.put("qty", quantity);
		orderDetailsParams.put("price", price);
		orderDetailsParams.put("freeIssue", freeIssue);
		orderDetailsParams.put("salableReturns", salableReturns);
		orderDetailsParams.put("rpId", rp_id);
		orderDetailsParams.put("balanceQty", stock);
		return new JSONObject(orderDetailsParams);
	}

	@Override
	public String toString() {
		return itemDescription;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || o.getClass() != OrderDetail.class) {
			return false;
		}
		OrderDetail that = (OrderDetail) o;
		if (itemId != that.itemId) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return itemId;
	}
}
