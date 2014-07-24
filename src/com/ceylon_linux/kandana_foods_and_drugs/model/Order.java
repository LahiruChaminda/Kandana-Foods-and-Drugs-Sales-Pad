/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 8:20:35 PM
 */

package com.ceylon_linux.kandana_foods_and_drugs.model;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class Order implements Serializable {

	private long orderId;
	private int outletId;
	private String outletDescription;
	private int positionId;
	private int routeId;
	private long invoiceTime;
	private double longitude;
	private double latitude;
	private int batteryLevel;
	private ArrayList<OrderDetail> orderDetails;

	public Order(int outletId, int positionId, int routeId, int batteryLevel, long invoiceTime, double longitude, double latitude, ArrayList<OrderDetail> orderDetails) {
		this.outletId = outletId;
		this.positionId = positionId;
		this.routeId = routeId;
		this.batteryLevel = batteryLevel;
		this.invoiceTime = invoiceTime;
		this.longitude = longitude;
		this.latitude = latitude;
		this.orderDetails = orderDetails;
	}

	public Order(long orderId, int outletId, int positionId, int routeId, int batteryLevel, long invoiceTime, double longitude, double latitude, ArrayList<OrderDetail> orderDetails) {
		this.orderId = orderId;
		this.outletId = outletId;
		this.positionId = positionId;
		this.routeId = routeId;
		this.batteryLevel = batteryLevel;
		this.invoiceTime = invoiceTime;
		this.longitude = longitude;
		this.latitude = latitude;
		this.orderDetails = orderDetails;
	}

	public Order(long orderId, int outletId, String outletDescription, int positionId, int routeId, long invoiceTime, double longitude, double latitude, int batteryLevel, ArrayList<OrderDetail> orderDetails) {
		this.orderId = orderId;
		this.outletDescription = outletDescription;
		this.outletId = outletId;
		this.positionId = positionId;
		this.routeId = routeId;
		this.batteryLevel = batteryLevel;
		this.invoiceTime = invoiceTime;
		this.longitude = longitude;
		this.latitude = latitude;
		this.orderDetails = orderDetails;
	}

	public JSONObject getOrderAsJson() {
		HashMap<String, Object> orderJsonParams = new HashMap<String, Object>();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		simpleDateFormat.applyPattern("yyyy-MM-dd");
		Date invoiceDate = new Date(getInvoiceTime());
		HashMap<String, Object> invoiceParams = new HashMap<String, Object>();
		invoiceParams.put("outletId", getOutletId());
		invoiceParams.put("routeId", getRouteId());
		invoiceParams.put("invoiceDate", simpleDateFormat.format(invoiceDate));
		simpleDateFormat.applyPattern("HH:mm:ss");
		invoiceParams.put("invoiceTime", simpleDateFormat.format(invoiceDate));
		invoiceParams.put("longitude", getLongitude());
		invoiceParams.put("latitude", getLatitude());
		invoiceParams.put("batteryLevel", getBatteryLevel());

		JSONArray orderDetailsJsonArray = new JSONArray();
		for (OrderDetail orderDetail : getOrderDetails()) {
			orderDetailsJsonArray.put(orderDetail.getOrderDetailAsJson());
		}
		invoiceParams.put("invoiceItems", orderDetailsJsonArray);
		orderJsonParams.put("Invoice", new JSONObject(invoiceParams));
		Log.i("response count", getOrderDetails().size() + "");
		Log.i("response json", new JSONObject(orderJsonParams).toString());
		return new JSONObject(orderJsonParams);
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public int getOutletId() {
		return outletId;
	}

	public void setOutletId(int outletId) {
		this.outletId = outletId;
	}

	public String getOutletDescription() {
		return outletDescription;
	}

	public void setOutletDescription(String outletDescription) {
		this.outletDescription = outletDescription;
	}

	public int getPositionId() {
		return positionId;
	}

	public void setPositionId(int positionId) {
		this.positionId = positionId;
	}

	public int getRouteId() {
		return routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}

	public long getInvoiceTime() {
		return invoiceTime;
	}

	public void setInvoiceTime(long invoiceTime) {
		this.invoiceTime = invoiceTime;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public int getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(int batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	public ArrayList<OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(ArrayList<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}

	@Override
	public String toString() {
		return outletDescription;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || o.getClass() != Order.class) return false;

		Order order = (Order) o;

		if (orderId != order.orderId) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return (int) (orderId ^ (orderId >>> 32));
	}
}
