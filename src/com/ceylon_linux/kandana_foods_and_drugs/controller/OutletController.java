/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 13, 2014, 2:34:21 PM
 */

package com.ceylon_linux.kandana_foods_and_drugs.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.ceylon_linux.kandana_foods_and_drugs.db.DbHandler;
import com.ceylon_linux.kandana_foods_and_drugs.db.SQLiteDatabaseHelper;
import com.ceylon_linux.kandana_foods_and_drugs.model.City;
import com.ceylon_linux.kandana_foods_and_drugs.model.District;
import com.ceylon_linux.kandana_foods_and_drugs.model.Outlet;
import com.ceylon_linux.kandana_foods_and_drugs.model.Route;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class OutletController extends AbstractController {

	private OutletController() {
	}

	public static void downloadOutlets(Context context, int positionId) throws IOException, JSONException {
		JSONObject responseJson = getJsonObject(OutletURLPack.GET_OUTLETS, OutletURLPack.getOutletParameters(positionId), context);
		if (responseJson.getBoolean("result")) {
			JSONArray districtCollection = responseJson.getJSONArray("districts");
			ArrayList<District> districts = new ArrayList<District>();
			for (int i = 0, DISTRICT_LENGTH = districtCollection.length(); i < DISTRICT_LENGTH; i++) {
				District district = District.parseDistrict(districtCollection.getJSONObject(i));
				if (district != null) {
					districts.add(district);
				}
			}
			saveOutletsToDb(districts, context);
		}
	}

	private static void saveOutletsToDb(ArrayList<District> districts, Context context) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		String districtSql = "replace into tbl_district(districtId, districtName) values (?,?)";
		String routeSql = "replace into tbl_route(routeId, districtId, routeName) values (?,?,?)";
		String citySql = "replace into tbl_city(cityId, routeId, cityName) values (?,?,?)";
		String outletSql = "replace into tbl_outlet(outletId, cityId, outletName, outletAddress, outletType, outletDiscount) values (?,?,?,?,?,?)";
		try {
			database.beginTransaction();
			for (District district : districts) {
				DbHandler.performExecuteInsert(database, districtSql, new Object[]{
					district.getDistrictId(),
					district.getDistrictName()
				});
				for (Route route : district.getRoutes()) {
					DbHandler.performExecuteInsert(database, routeSql, new Object[]{
						route.getRouteId(),
						district.getDistrictId(),
						route.getRouteName()
					});
					for (City city : route.getCities()) {
						DbHandler.performExecuteInsert(database, citySql, new Object[]{
							city.getCityId(),
							route.getRouteId(),
							city.getCityName()
						});
						for (Outlet outlet : city.getOutlets()) {
							DbHandler.performExecuteInsert(database, outletSql, new Object[]{
								outlet.getOutletId(),
								outlet.getCityId(),
								outlet.getOutletName(),
								outlet.getOutletAddress(),
								outlet.getOutletType(),
								outlet.getOutletDiscount()
							});
						}
					}
				}
			}
			database.setTransactionSuccessful();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			database.endTransaction();
			databaseHelper.close();
		}
	}

	public static ArrayList<District> loadDistrictsFromDb(Context context) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		String districtQuery = "select districtId, districtName from tbl_district";
		String routeQuery = "select routeId, routeName from tbl_route where districtId=?";
		String cityQuery = "select cityId, cityName from tbl_city where routeId=?";
		String outletQuery = "select outletId, cityId, outletName, outletAddress, outletType, outletDiscount from tbl_outlet where cityId=?";

		Cursor districtCursor = DbHandler.performRawQuery(database, districtQuery, null);
		ArrayList<District> districts = new ArrayList<District>();
		for (districtCursor.moveToFirst(); !districtCursor.isAfterLast(); districtCursor.moveToNext()) {
			int districtId = districtCursor.getInt(0);
			String districtName = districtCursor.getString(1);

			Cursor routeCursor = DbHandler.performRawQuery(database, routeQuery, new Object[]{districtId});
			ArrayList<Route> routes = new ArrayList<Route>();
			for (routeCursor.moveToFirst(); !routeCursor.isAfterLast(); routeCursor.moveToNext()) {
				int routeId = routeCursor.getInt(0);
				String routeName = routeCursor.getString(1);
				Cursor cityCursor = DbHandler.performRawQuery(database, cityQuery, new Object[]{routeId});
				ArrayList<City> cities = new ArrayList<City>();
				for (cityCursor.moveToFirst(); !cityCursor.isAfterLast(); cityCursor.moveToNext()) {
					int cityId = cityCursor.getInt(0);
					String cityName = cityCursor.getString(1);
					Cursor outletCursor = DbHandler.performRawQuery(database, outletQuery, new Object[]{cityId});
					ArrayList<Outlet> outlets = new ArrayList<Outlet>();
					for (outletCursor.moveToFirst(); !outletCursor.isAfterLast(); outletCursor.moveToNext()) {
						outlets.add(new Outlet(
							outletCursor.getInt(0),
							outletCursor.getInt(1),
							outletCursor.getString(2),
							outletCursor.getString(3),
							outletCursor.getInt(4),
							outletCursor.getDouble(5)
						));
					}
					Collections.sort(outlets);
					outletCursor.close();
					cities.add(new City(cityId, cityName, outlets));
				}
				Collections.sort(cities);
				routes.add(new Route(routeId, routeName, cities));
			}
			Collections.sort(routes);
			routeCursor.close();
			districts.add(new District(districtId, districtName, routes));
		}
		Collections.sort(districts);
		districtCursor.close();
		databaseHelper.close();
		return districts;
	}
}
