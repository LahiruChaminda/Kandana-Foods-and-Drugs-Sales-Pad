/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jul 15, 2014, 8:37 AM
 */
package com.ceylon_linux.kandana_foods_and_drugs.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class District implements Serializable, Comparable {
	private int districtId;
	private String districtName;
	private ArrayList<Route> routes;

	public District(int districtId, String districtName, ArrayList<Route> routes) {
		this.districtId = districtId;
		this.districtName = districtName;
		this.routes = routes;
	}

	public static final District parseDistrict(JSONObject districtJsonInstance) throws JSONException {
		ArrayList<Route> routes = new ArrayList<Route>();
		JSONArray routeCollection = districtJsonInstance.getJSONArray("routes");
		for (int i = 0, ROUTE_COUNT = routeCollection.length(); i < ROUTE_COUNT; i++) {
			Route route = Route.parseRoute(routeCollection.getJSONObject(i));
			if (route != null) {
				routes.add(route);
			}
		}
		return routes.size() == 0 ? null : new District(
			districtJsonInstance.getInt("dis_id"),
			districtJsonInstance.getString("dis_name"),
			routes
		);
	}

	public int getDistrictId() {
		return districtId;
	}

	public void setDistrictId(int districtId) {
		this.districtId = districtId;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public ArrayList<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(ArrayList<Route> routes) {
		this.routes = routes;
	}

	@Override
	public String toString() {
		return districtName;
	}

	@Override
	public int compareTo(Object another) {
		District anotherOutlet = (District) another;
		return districtName.compareTo(anotherOutlet.getDistrictName());
	}
}
