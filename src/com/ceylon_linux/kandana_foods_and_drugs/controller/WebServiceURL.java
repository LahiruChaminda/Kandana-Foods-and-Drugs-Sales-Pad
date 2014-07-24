/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : May 9, 2014, 10:23:39 PM
 */

package com.ceylon_linux.kandana_foods_and_drugs.controller;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * WebServiceURL - Holds web service URL(s)
 *
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
abstract class WebServiceURL {

	private static final String webServiceURL = "http://gateway.ceylonlinux.com/KADANA/andr_manager/";

	protected WebServiceURL() {
	}

	protected static final class CategoryURLPack {

		public static final HashMap<String, Object> getCategoryParameters(int positionId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("userId", positionId);
			return parameters;
		}

		public static final HashMap<String, Object> getFreeIssueParameters() {
			//HashMap<String, Object> parameters = new HashMap<String, Object>();
			//parameters.put("userId", positionId);
			return null;
		}

		public static final String GET_ITEMS_AND_CATEGORIES = webServiceURL + "getProducts2";
		public static final String GET_FREE_ISSUE_RATIOS = webServiceURL + "getFreeItem";
	}

	protected static final class UserURLPack {

		public static final HashMap<String, Object> getLoginParameters(String userName, String password) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("username", userName);
			parameters.put("password", password);
			return parameters;
		}

		public static final String LOGIN = webServiceURL + "login";

	}

	protected static final class OutletURLPack {

		public static final HashMap<String, Object> getOutletParameters(int positionId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("userId", positionId);
			return parameters;
		}

		public static final String GET_OUTLETS = webServiceURL + "getRouteAndOutlets";
	}

	protected static final class OrderURLPack {

		public static final HashMap<String, Object> getInsertOrderParameters(JSONObject orderJson, int userId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("jsonString", orderJson);
			parameters.put("userId", userId);
			return parameters;
		}

		public static final String INSERT_ORDER = webServiceURL + "insertInvoiceDetails";
	}
}
