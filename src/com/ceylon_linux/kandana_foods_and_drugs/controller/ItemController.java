/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 10, 2014, 9:42:22 AM
 */

package com.ceylon_linux.kandana_foods_and_drugs.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.ceylon_linux.kandana_foods_and_drugs.db.DbHandler;
import com.ceylon_linux.kandana_foods_and_drugs.db.SQLiteDatabaseHelper;
import com.ceylon_linux.kandana_foods_and_drugs.model.Distributor;
import com.ceylon_linux.kandana_foods_and_drugs.model.Item;
import com.ceylon_linux.kandana_foods_and_drugs.model.Supplier;
import com.ceylon_linux.kandana_foods_and_drugs.model.SupplierCategory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class ItemController extends AbstractController {

	private ItemController() {
	}

	public static void downloadItems(Context context, int positionId) throws IOException, JSONException {
		JSONObject responseJson = getJsonObject(DistributorURLPack.GET_DISTRIBUTORS, DistributorURLPack.getCategoryParameters(positionId), context);
		JSONArray distributorJsonArray = responseJson.getJSONArray("distributor");
		ArrayList<Distributor> distributors = new ArrayList<Distributor>();
		for (int i = 0, DISTRIBUTOR_LENGTH = distributorJsonArray.length(); i < DISTRIBUTOR_LENGTH; i++) {
			Distributor distributor = Distributor.parseDistributor(distributorJsonArray.getJSONObject(i));
			if (distributor != null) {
				distributors.add(distributor);
			}
		}
		saveDistributorsToDb(distributors, context);
	}

	private static void saveDistributorsToDb(ArrayList<Distributor> distributors, Context context) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		try {
			database.beginTransaction();
			SQLiteStatement distributorStatement = database.compileStatement("insert into tbl_distributor( distributorId, distributorName) values (?,?)");
			SQLiteStatement supplierCategoryStatement = database.compileStatement("insert into tbl_supplier_category( supplierCategoryId, supplierCategory, distributorId) values (?,?,?)");
			SQLiteStatement supplierStatement = database.compileStatement("insert into tbl_supplier( supplierId, supplierCategoryId, supplierName, distributorId) values (?,?,?,?)");
			SQLiteStatement itemStatement = database.compileStatement("insert into tbl_item( itemId, supplierId, itemCode, itemDescription, price, supplierCategoryId, distributorId, packSize, stock) values (?,?,?,?,?,?,?,?,?)");
			SQLiteStatement freeIssueStatement = database.compileStatement("insert into tbl_free_issue_ratio (itemId, rangeMinimumQuantity, freeIssueQuantity) values(?,?,?)");
			for (Distributor distributor : distributors) {
				int distributorId;
				DbHandler.performExecuteInsert(distributorStatement, new Object[]{
					distributorId = distributor.getDistributorId(),
					distributor.getDistributorName()
				});
				for (SupplierCategory supplierCategory : distributor.getSupplierCategories()) {
					int supplierCategoryId;
					DbHandler.performExecuteInsert(supplierCategoryStatement, new Object[]{
						supplierCategoryId = supplierCategory.getSupplierCategoryId(),
						supplierCategory.getSupplierCategory(),
						distributorId
					});
					for (Supplier supplier : supplierCategory.getSuppliers()) {
						int supplierId;
						DbHandler.performExecuteInsert(supplierStatement, new Object[]{
							supplierId = supplier.getCategoryId(),
							supplierCategoryId,
							supplier.getCategoryDescription(),
							distributorId
						});
						for (Item item : supplier.getItems()) {
							int itemId = item.getItemId();
							Object[] itemParameters = {
								itemId,
								supplierId,
								item.getItemCode(),
								item.getItemDescription(),
								item.getPrice(),
								supplierCategoryId,
								distributorId,
								item.getPackSize(),
								item.getStock()
							};
							DbHandler.performExecuteInsert(itemStatement, itemParameters);
							JSONArray freeIssueJsonArray = item.getFreeIssueJsonArray();
							for (int i = 0, freeIssueLength = freeIssueJsonArray.length(); i < freeIssueLength; i++) {
								try {
									JSONObject freeIssue = freeIssueJsonArray.getJSONObject(i);
									int minimumQty = freeIssue.getInt("purchaseQty");
									int freeIssueQty = freeIssue.getInt("freeQty");
									DbHandler.performExecuteInsert(freeIssueStatement, new Object[]{
										itemId,
										minimumQty,
										freeIssueQty
									});
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
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

	public static ArrayList<SupplierCategory> loadSupplierCategoriesFromDb(Context context, int distributorId) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		HashSet<SupplierCategory> supplierCategories = new HashSet<SupplierCategory>();
		String supplierCategorySql = "select supplierCategoryId,supplierCategory from tbl_supplier_category where distributorId=?";
		String supplierSql = "select supplierId,supplierName from tbl_supplier where supplierCategoryId=?";
		String itemSql = "select itemId,itemCode,itemDescription,price,packSize,stock from tbl_item where supplierId=?";
		Cursor supplierCategoryCursor = DbHandler.performRawQuery(database, supplierCategorySql, new Object[]{distributorId});
		for (supplierCategoryCursor.moveToFirst(); !supplierCategoryCursor.isAfterLast(); supplierCategoryCursor.moveToNext()) {
			int supplierCategoryId;
			Cursor supplierCursor = DbHandler.performRawQuery(database, supplierSql, new Object[]{supplierCategoryId = supplierCategoryCursor.getInt(0)});
			HashSet<Supplier> suppliers = new HashSet<Supplier>();
			for (supplierCursor.moveToFirst(); !supplierCursor.isAfterLast(); supplierCursor.moveToNext()) {
				int supplierId;
				String supplierDescription = supplierCursor.getString(1);
				HashSet<Item> items = new HashSet<Item>();
				Cursor itemCursor = DbHandler.performRawQuery(database, itemSql, new Object[]{supplierId = supplierCursor.getInt(0)});
				for (itemCursor.moveToFirst(); !itemCursor.isAfterLast(); itemCursor.moveToNext()) {
					items.add(new Item(
						itemCursor.getInt(0),
						itemCursor.getString(1),
						itemCursor.getString(2),
						itemCursor.getDouble(3),
						itemCursor.getString(4),
						itemCursor.getInt(5)
					));
				}
				itemCursor.close();
				suppliers.add(new Supplier(supplierId, supplierDescription, new ArrayList<Item>(items)));
			}
			supplierCursor.close();
			String supplierCategoryDescription = supplierCategoryCursor.getString(1);
			SupplierCategory supplierCategory = new SupplierCategory(supplierCategoryId, supplierCategoryDescription, new ArrayList<Supplier>(suppliers));
			supplierCategories.add(supplierCategory);
		}
		supplierCategoryCursor.close();
		databaseHelper.close();
		return new ArrayList<SupplierCategory>(supplierCategories);
	}

	public static ArrayList<Supplier> loadSuppliersFromDb(Context context, int distributorId) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		String supplierSql = "select ts.supplierId, ts.supplierName from tbl_supplier as ts inner join tbl_supplier_category as tsc on ts.distributorId = tsc.distributorId where tsc.distributorId=?";
		String itemSql = "select itemId,itemCode,itemDescription,price,packSize,stock from tbl_item where supplierId=?";
		Cursor supplierCursor = DbHandler.performRawQuery(database, supplierSql, new Object[]{distributorId});
		HashSet<Supplier> suppliers = new HashSet<Supplier>();
		for (supplierCursor.moveToFirst(); !supplierCursor.isAfterLast(); supplierCursor.moveToNext()) {
			int supplierId;
			String supplierDescription = supplierCursor.getString(1);
			HashSet<Item> items = new HashSet<Item>();
			Cursor itemCursor = DbHandler.performRawQuery(database, itemSql, new Object[]{supplierId = supplierCursor.getInt(0)});
			for (itemCursor.moveToFirst(); !itemCursor.isAfterLast(); itemCursor.moveToNext()) {
				items.add(new Item(
					itemCursor.getInt(0),
					itemCursor.getString(1),
					itemCursor.getString(2),
					itemCursor.getDouble(3),
					itemCursor.getString(4),
					itemCursor.getInt(5)
				));
			}
			itemCursor.close();
			suppliers.add(new Supplier(supplierId, supplierDescription, new ArrayList<Item>(items)));
		}
		supplierCursor.close();
		databaseHelper.close();
		return new ArrayList<Supplier>(suppliers);
	}

	public static ArrayList<Item> loadItemsFromDb(Context context, int distributorId) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		String itemSql = "select ti.itemId,ti.itemCode,ti.itemDescription,ti.price,packSize,stock from tbl_item as ti where distributorId=?";
		HashSet<Item> items = new HashSet<Item>();
		Cursor itemCursor = DbHandler.performRawQuery(database, itemSql, new Object[]{distributorId});
		for (itemCursor.moveToFirst(); !itemCursor.isAfterLast(); itemCursor.moveToNext()) {
			items.add(new Item(
				itemCursor.getInt(0),
				itemCursor.getString(1),
				itemCursor.getString(2),
				itemCursor.getDouble(3),
				itemCursor.getString(4),
				itemCursor.getInt(5)
			));
		}
		itemCursor.close();
		databaseHelper.close();
		return new ArrayList<Item>(items);
	}

	public static ArrayList<Distributor> loadDistributorsFromDb(Context context) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		ArrayList<Distributor> distributors = new ArrayList<Distributor>();
		String distributorSql = "select distributorId,distributorName from tbl_distributor";
		Cursor distributorCursor = DbHandler.performRawQuery(database, distributorSql, null);
		for (distributorCursor.moveToFirst(); !distributorCursor.isAfterLast(); distributorCursor.moveToNext()) {
			distributors.add(new Distributor(
				distributorCursor.getInt(0),
				distributorCursor.getString(1)
			));
		}
		distributorCursor.close();
		databaseHelper.close();
		return distributors;
	}

}
