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

	private static final String webServiceURL = "http://123.231.15.146/andr_manager/";

	protected WebServiceURL() {
	}

	protected static final class DistributorURLPack {

		public static final HashMap<String, Object> getSuppliersParameters(int userId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("userId", userId);
			return parameters;
		}

		public static final HashMap<String, Object> getCategoryParameters(int supplierId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("supplierId", supplierId);
			return parameters;
		}

		public static final HashMap<String, Object> getProductsParameters(int categoryId, int distributorId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("categoryId", categoryId);
			parameters.put("disId", distributorId);
			return parameters;
		}

		public static final String GET_DISTRIBUTORS = webServiceURL + "getDistributors.php";
		public static final String GET_SUPPLIERS = webServiceURL + "getSuppliers";
		public static final String GET_CATEGORIES = webServiceURL + "getItemCategory";
		public static final String GET_ITEMS = webServiceURL + "getProducts";
	}

	protected static final class UserURLPack {

		public static final HashMap<String, Object> getLoginParameters(String userName, String password) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("username", userName);
			parameters.put("password", password);
			return parameters;
		}

		public static final HashMap<String, Object> getMessageBroadcastParameters(int userId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("userId", userId);
			return parameters;
		}

		public static final HashMap<String, Object> getRepTargetParameters(int userId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("userId", userId);
			return parameters;
		}

		public static final String LOGIN = webServiceURL + "login";
		public static final String MESSAGE_BROADCAST = webServiceURL + "getMessages";
		public static final String REP_TARGET = webServiceURL + "getTarget";

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

	protected static final class UnProductiveCallURLPack {

		public static final HashMap<String, Object> getUnProductiveCallParameters(JSONObject unProductiveCallJson, int userId) {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("unproductiveCall", unProductiveCallJson);
			parameters.put("userId", userId);
			return parameters;
		}

		public static final String SYNC_UN_PRODUCTIVE_CALL = webServiceURL + "insertInvoiceDetails";
	}
}
