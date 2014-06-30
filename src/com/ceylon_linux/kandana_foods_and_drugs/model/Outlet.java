/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 7:26:46 PM
 */

package com.ceylon_linux.kandana_foods_and_drugs.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class Outlet implements Serializable {

	private int outletId;
	private int routeId;
	private String outletName;
	private String outletAddress;
	private int outletType;
	private double outletDiscount;

	public Outlet(int outletId, int routeId, String outletName, String outletAddress, int outletType, double outletDiscount) {
		this.outletId = outletId;
		this.routeId = routeId;
		this.outletName = outletName;
		this.outletAddress = outletAddress;
		this.outletType = outletType;
		this.outletDiscount = outletDiscount;
	}

	public final static Outlet parseOutlet(JSONObject outletJsonInstance) throws JSONException {
		if (outletJsonInstance == null) {
			return null;
		}
		return new Outlet(
			outletJsonInstance.getInt("outletId"),
			outletJsonInstance.getInt("routeId"),
			outletJsonInstance.getString("outletName"),
			outletJsonInstance.getString("outletAddress"),
			outletJsonInstance.getInt("outletType"),
			outletJsonInstance.getDouble("outletDiscount")
		);
	}

	public int getOutletId() {
		return outletId;
	}

	public void setOutletId(int outletId) {
		this.outletId = outletId;
	}

	public String getOutletName() {
		return outletName;
	}

	public void setOutletName(String outletName) {
		this.outletName = outletName;
	}

	public String getOutletAddress() {
		return outletAddress;
	}

	public void setOutletAddress(String outletAddress) {
		this.outletAddress = outletAddress;
	}

	public int getOutletType() {
		return outletType;
	}

	public void setOutletType(int outletType) {
		this.outletType = outletType;
	}

	public double getOutletDiscount() {
		return outletDiscount;
	}

	public void setOutletDiscount(double outletDiscount) {
		this.outletDiscount = outletDiscount;
	}

	public int getRouteId() {
		return routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}

	@Override
	public String toString() {
		return outletName;
	}
}
