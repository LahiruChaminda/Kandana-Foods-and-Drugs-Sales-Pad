/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Aug 14, 2014, 12:18:41 PM
 */
package com.ceylon_linux.kandana_foods_and_drugs.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.ceylon_linux.kandana_foods_and_drugs.db.DbHandler;
import com.ceylon_linux.kandana_foods_and_drugs.db.SQLiteDatabaseHelper;
import com.ceylon_linux.kandana_foods_and_drugs.model.UnProductiveCall;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class UnProductiveCallController extends AbstractController {

	public static boolean saveUnProductiveCall(Context context, UnProductiveCall unProductiveCall) throws SQLException {
		SQLiteDatabaseHelper databaseInstance = SQLiteDatabaseHelper.getDatabaseInstance(context);
		try {
			SQLiteDatabase database = databaseInstance.getWritableDatabase();
			SQLiteStatement unproductiveCallStatement = database.compileStatement("insert into tbl_unproductive_call(outletId,batteryLevel,repId,reason,longitude,latitude,time) values(?,?,?,?,?,?,?)");
			DbHandler.performExecuteInsert(unproductiveCallStatement, new Object[]{
				unProductiveCall.getOutletId(),
				unProductiveCall.getBatteryLevel(),
				unProductiveCall.getRepId(),
				unProductiveCall.getReason(),
				unProductiveCall.getLongitude(),
				unProductiveCall.getLatitude(),
				unProductiveCall.getTimestamp()
			});
			return true;
		} finally {
			databaseInstance.close();
		}
	}

	public static ArrayList<UnProductiveCall> getUnProductiveCalls(Context context) throws SQLException {
		SQLiteDatabaseHelper databaseInstance = SQLiteDatabaseHelper.getDatabaseInstance(context);
		ArrayList<UnProductiveCall> unProductiveCalls = new ArrayList<UnProductiveCall>();
		try {
			SQLiteDatabase database = databaseInstance.getWritableDatabase();
			String sql = "select u.unProductiveCallId, u.outletId, o.outletName, u.reason, u.time, u.longitude, u.latitude, u.batteryLevel, u.repId, u.syncStatus from tbl_unproductive_call as u inner join tbl_outlet as o on o.outletId=u.outletId";
			Cursor cursor = DbHandler.performRawQuery(database, sql, null);
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				UnProductiveCall unProductiveCall = new UnProductiveCall(
					cursor.getInt(0),
					cursor.getInt(1),
					cursor.getString(2),
					cursor.getString(3),
					cursor.getLong(4),
					cursor.getDouble(5),
					cursor.getDouble(6),
					cursor.getInt(7),
					cursor.getInt(8)
				);
				unProductiveCalls.add(unProductiveCall);
			}
			return unProductiveCalls;
		} finally {
			databaseInstance.close();
		}
	}

	public static boolean syncUnProductiveCall(Context context, UnProductiveCall unProductiveCall) throws SQLException, IOException, JSONException {
		JSONObject response = getJsonObject(UnProductiveCallURLPack.SYNC_UN_PRODUCTIVE_CALL, UnProductiveCallURLPack.getUnProductiveCallParameters(unProductiveCall.getUnProductiveCallAsJson(), UserController.getAuthorizedUser(context).getUserId()), context);
		return response.getBoolean("response");
	}
}
