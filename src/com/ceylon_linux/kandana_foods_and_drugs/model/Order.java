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
	private int distributorId;

	public Order(long orderId, int outletId, int positionId, int routeId, int batteryLevel, long invoiceTime, double longitude, double latitude, ArrayList<OrderDetail> orderDetails, int distributorId) {
		this.orderId = orderId;
		this.outletId = outletId;
		this.positionId = positionId;
		this.routeId = routeId;
		this.batteryLevel = batteryLevel;
		this.invoiceTime = invoiceTime;
		this.longitude = longitude;
		this.latitude = latitude;
		this.orderDetails = orderDetails;
		this.distributorId = distributorId;
	}

	public Order(Outlet outlet, int positionId, int batteryLevel, long invoiceTime, double longitude, double latitude, ArrayList<OrderDetail> orderDetails, int distributorId) {
		this.outletId = outlet.getOutletId();
		this.positionId = positionId;
		this.outletDescription = outlet.getOutletName();
		this.routeId = outlet.getCityId();
		this.batteryLevel = batteryLevel;
		this.invoiceTime = invoiceTime;
		this.longitude = longitude;
		this.latitude = latitude;
		this.orderDetails = orderDetails;
		this.distributorId = distributorId;
	}

	public JSONObject getOrderAsJson() {
		HashMap<String, Object> orderJsonParams = new HashMap<String, Object>();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		simpleDateFormat.applyPattern("yyyy-MM-dd");
		Date invoiceDate = new Date(invoiceTime);
		HashMap<String, Object> invoiceParams = new HashMap<String, Object>();
		invoiceParams.put("outletId", outletId);
		invoiceParams.put("routeId", routeId);
		invoiceParams.put("invoiceDate", simpleDateFormat.format(invoiceDate));
		simpleDateFormat.applyPattern("HH:mm:ss");
		invoiceParams.put("invoiceTime", simpleDateFormat.format(invoiceDate));
		invoiceParams.put("longitude", longitude);
		invoiceParams.put("latitude", latitude);
		invoiceParams.put("batteryLevel", batteryLevel);
		invoiceParams.put("timestamp", invoiceTime);
		invoiceParams.put("distributorId", distributorId);

		JSONArray orderDetailsJsonArray = new JSONArray();
		for (OrderDetail orderDetail : getOrderDetails()) {
			orderDetailsJsonArray.put(orderDetail.getOrderDetailAsJson());
		}
		invoiceParams.put("invoiceItems", orderDetailsJsonArray);
		orderJsonParams.put("Invoice", new JSONObject(invoiceParams));
		Log.i("qwerty", new JSONObject(orderJsonParams).toString());
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

	public int getDistributorId() {
		return distributorId;
	}

	public void setDistributorId(int distributorId) {
		this.distributorId = distributorId;
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
