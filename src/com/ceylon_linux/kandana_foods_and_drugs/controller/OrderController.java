/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 17, 2014, 4:35:16 PM
 */

package com.ceylon_linux.kandana_foods_and_drugs.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.ceylon_linux.kandana_foods_and_drugs.db.DbHandler;
import com.ceylon_linux.kandana_foods_and_drugs.db.SQLiteDatabaseHelper;
import com.ceylon_linux.kandana_foods_and_drugs.model.Order;
import com.ceylon_linux.kandana_foods_and_drugs.model.OrderDetail;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class OrderController extends AbstractController {

	private OrderController() {
	}

	public static boolean saveOrderToDb(Context context, Order order) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		try {
			database.beginTransaction();
			String orderInsertSQL = "insert into tbl_order(outletId, routeId, positionId, invoiceTime, total, batteryLevel, longitude, latitude, distributorId) values(?,?,?,?,?,?,?,?,?)";
			String orderDetailInsertSQL = "insert into tbl_order_detail(orderId, itemId, price, discount, quantity, freeQuantity, returnQuantity, replaceQuantity, sampleQuantity, rp_id, stock) values(?,?,?,?,?,?,?,?,?,?,?)";
			long orderId = DbHandler.performExecuteInsert(database, orderInsertSQL, new Object[]{
				order.getOutletId(),
				order.getRouteId(),
				order.getPositionId(),
				order.getInvoiceTime(),
				0,//total
				order.getBatteryLevel(),
				order.getLongitude(),
				order.getLatitude(),
				order.getDistributorId()
			});
			SQLiteStatement orderDetailInsertStatement = database.compileStatement(orderDetailInsertSQL);
			for (OrderDetail orderDetail : order.getOrderDetails()) {
				//orderId, itemId, price, discount, quantity, freeQuantity, returnQuantity, replaceQuantity, sampleQuantity, rp_id, stock
				DbHandler.performExecuteInsert(orderDetailInsertStatement, new Object[]{
					orderId,
					orderDetail.getItemId(),
					orderDetail.getPrice(),
					0,
					orderDetail.getQuantity(),
					orderDetail.getFreeIssue(),
					0,
					0,
					0,
					orderDetail.getRp_id(),
					orderDetail.getStock()
				});
//				Log.i("read-write", "orderId=" + orderId + " itemId=" + orderDetail.getItemId() + " price=" + orderDetail.getPrice() + " discount=" + 0 + " quantity=" + orderDetail.getQuantity() + " freeQuantity=" + orderDetail.getFreeIssue() + " returnQuantity=" + 0 + " replaceQuantity=" + 0 + " sampleQuantity=" + 0 + " rp_id=" + orderDetail.getRp_id() + " stock=" + orderDetail.getStock());
			}
			database.setTransactionSuccessful();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			database.endTransaction();
		}
		return true;
	}

	public static boolean syncOrder(Context context, JSONObject orderJson) throws IOException, JSONException {
		JSONObject responseJson = getJsonObject(OrderURLPack.INSERT_ORDER, OrderURLPack.getInsertOrderParameters(orderJson, UserController.getAuthorizedUser(context).getUserId()), context);
		return (responseJson != null) && responseJson.getBoolean("result");
	}

	public static boolean syncUnSyncedOrders(Context context) throws IOException, JSONException {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		try {
			String orderSelectSql = "select tbo.orderId, tbo.outletId, tbo.routeId, tbo.positionId, tbo.invoiceTime, tbo.total, tbo.batteryLevel, tbo.longitude, tbo.latitude, tbt.outletType, tbo.distributorId from tbl_order as tbo inner join tbl_outlet as tbt on tbt.outletId=tbo.outletId";
			String orderDetailSelectSql = "select distinct tod.itemId, tod.price, tod.discount, tod.quantity, tod.freeQuantity, ti.itemDescription, tod.returnQuantity, tod.replaceQuantity, tod.sampleQuantity, tod.rp_id, tod.stock from tbl_order_detail as tod inner join tbl_item as ti on ti.itemId=tod.itemId where orderId=?";
			Cursor orderCursor = DbHandler.performRawQuery(database, orderSelectSql, null);
			ArrayList<Order> orders = new ArrayList<Order>();
			for (orderCursor.moveToFirst(); !orderCursor.isAfterLast(); orderCursor.moveToNext()) {
				long orderId = orderCursor.getLong(0);
				int outletId = orderCursor.getInt(1);
				int routeId = orderCursor.getInt(2);
				int positionId = orderCursor.getInt(3);
				long invoiceTime = orderCursor.getLong(4);
//				double total = orderCursor.getDouble(5);
				int batteryLevel = orderCursor.getInt(6);
				double longitude = orderCursor.getDouble(7);
				double latitude = orderCursor.getDouble(8);
				int distributorId = orderCursor.getInt(10);
				ArrayList<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
				Cursor orderDetailsCursor = DbHandler.performRawQuery(database, orderDetailSelectSql, new Object[]{orderId});
				for (orderDetailsCursor.moveToFirst(); !orderDetailsCursor.isAfterLast(); orderDetailsCursor.moveToNext()) {
					int itemId = orderDetailsCursor.getInt(0);
					double price = orderDetailsCursor.getDouble(1);
					int quantity = orderDetailsCursor.getInt(3);
					int freeQuantity = orderDetailsCursor.getInt(4);
					String itemDescription = orderDetailsCursor.getString(5);
					int returnQuantity = orderDetailsCursor.getInt(6);
					int rp_id = orderDetailsCursor.getInt(9);
					int stock = orderDetailsCursor.getInt(10);
					OrderDetail orderDetail = new OrderDetail(itemId, itemDescription, quantity, price, freeQuantity, returnQuantity, rp_id, stock);
//					Log.i("read", "itemid=" + itemId + " description=" + itemDescription + " qty=" + quantity + " price=" + price + " free=" + freeQuantity + " ret=" + returnQuantity + " rp=" + rp_id + " stock=" + stock);
					orderDetails.add(orderDetail);
				}
				orderDetailsCursor.close();
				Order order = new Order(orderId, outletId, positionId, routeId, batteryLevel, invoiceTime, longitude, latitude, orderDetails, distributorId);
				orders.add(order);
			}
			orderCursor.close();
			String deleteQuery = "delete from tbl_order where orderId=?";
			SQLiteStatement deleteStatement = database.compileStatement(deleteQuery);
			for (Order order : orders) {
				boolean response = syncOrder(context, order.getOrderAsJson());
				if (response) {
					DbHandler.performExecuteUpdateDelete(deleteStatement, new Object[]{order.getOrderId()});
				} else {
					return false;
				}
			}
		} finally {
			databaseHelper.close();
		}
		return true;
	}

}
