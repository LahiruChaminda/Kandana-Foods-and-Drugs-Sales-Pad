/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jul 15, 2014, 8:39 AM
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
public class City implements Serializable, Comparable {
	private int cityId;
	private String cityName;
	private ArrayList<Outlet> outlets;

	public City(int cityId, String cityName, ArrayList<Outlet> outlets) {
		this.cityId = cityId;
		this.cityName = cityName;
		this.outlets = outlets;
	}

	public static final City parseCity(JSONObject cityJsonInstance) throws JSONException {
		ArrayList<Outlet> outlets = new ArrayList<Outlet>();
		JSONArray outletCollection = cityJsonInstance.getJSONArray("outlets");
		int cityId = cityJsonInstance.getInt("r_id");
		for (int i = 0, OUTLET_COUNT = outletCollection.length(); i < OUTLET_COUNT; i++) {
			Outlet outlet = Outlet.parseOutlet(outletCollection.getJSONObject(i), cityId);
			if (outlet != null) {
				outlets.add(outlet);
			}
		}
		return outlets.size() == 0 ? null : new City(
			cityId,
			cityJsonInstance.getString("r_name"),
			outlets
		);
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public ArrayList<Outlet> getOutlets() {
		return outlets;
	}

	public void setOutlets(ArrayList<Outlet> outlets) {
		this.outlets = outlets;
	}

	@Override
	public String toString() {
		return cityName;
	}

	@Override
	public int compareTo(Object another) {
		City anotherOutlet = (City) another;
		return cityName.compareTo(anotherOutlet.getCityName());
	}
}
