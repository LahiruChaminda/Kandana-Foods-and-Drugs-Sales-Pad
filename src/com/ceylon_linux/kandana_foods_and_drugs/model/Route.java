/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 13, 2014, 3:46:01 PM
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
public class Route implements Serializable, Comparable {

	private int routeId;
	private String routeName;
	private ArrayList<City> cities;

	public Route(int routeId, String routeName) {
		this.routeId = routeId;
		this.routeName = routeName;
	}

	public Route(int routeId, String routeName, ArrayList<City> cities) {
		this.routeId = routeId;
		this.routeName = routeName;
		this.cities = cities;
	}

	public static final Route parseRoute(JSONObject routeJsonInstance) throws JSONException {
		ArrayList<City> cities = new ArrayList<City>();
		JSONArray cityCollection = routeJsonInstance.getJSONArray("cities");
		for (int i = 0, CITY_COUNT = cityCollection.length(); i < CITY_COUNT; i++) {
			City city = (City) City.parseCity(cityCollection.getJSONObject(i));
			if (city != null) {
				cities.add(city);
			}
		}
		return cities.size() == 0 ? null : new Route(
			routeJsonInstance.getInt("ar_id"),
			routeJsonInstance.getString("ar_name"),
			cities
		);
	}

	public int getRouteId() {
		return routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public ArrayList<City> getCities() {
		return cities;
	}

	public void setCities(ArrayList<City> cities) {
		this.cities = cities;
	}

	@Override
	public String toString() {
		return routeName;
	}

	@Override
	public int compareTo(Object another) {
		Route anotherRoute = (Route) another;
		return routeName.compareTo(anotherRoute.getRouteName());
	}
}
