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
import com.ceylon_linux.kandana_foods_and_drugs.model.Item;
import com.ceylon_linux.kandana_foods_and_drugs.model.Supplier;
import com.ceylon_linux.kandana_foods_and_drugs.model.SupplierCategory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class ItemController extends AbstractController {

	private ItemController() {
	}

	public static void downloadItems(Context context, int positionId) throws IOException, JSONException {
		JSONObject responseJson = getJsonObject(CategoryURLPack.GET_ITEMS_AND_CATEGORIES, CategoryURLPack.getCategoryParameters(positionId), context);
		JSONArray supplierCategoryJson = responseJson.getJSONArray("supplier_type");
		ArrayList<SupplierCategory> supplierCategories = new ArrayList<SupplierCategory>();
		for (int i = 0, SUPPLIER_CATEGORY_LENGTH = supplierCategoryJson.length(); i < SUPPLIER_CATEGORY_LENGTH; i++) {
			SupplierCategory supplierCategory = SupplierCategory.parseSupplierCategory(supplierCategoryJson.getJSONObject(i));
			if (supplierCategory != null) {
				supplierCategories.add(supplierCategory);
			}
		}
		saveSupplierCategoriesToDb(supplierCategories, context);
	}

	private static void saveSupplierCategoriesToDb(ArrayList<SupplierCategory> supplierCategories, Context context) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		try {
			database.beginTransaction();
			SQLiteStatement supplierCategoryStatement = database.compileStatement("replace into tbl_supplier_category(supplierCategoryId,supplierCategory) values (?,?)");
			SQLiteStatement supplierStatement = database.compileStatement("replace into tbl_supplier(supplierId,supplierCategoryId,supplierName) values (?,?,?)");
			SQLiteStatement itemStatement = database.compileStatement("replace into tbl_item(itemId,supplierId,itemCode,itemDescription, price) values (?,?,?,?,?)");
			SQLiteStatement freeIssueStatement = database.compileStatement("replace into tbl_free_issue_ratio (itemId, rangeMinimumQuantity,freeIssueQuantity) values(?,?,?)");
			for (SupplierCategory supplierCategory : supplierCategories) {
				DbHandler.performExecuteInsert(supplierCategoryStatement, new Object[]{
					supplierCategory.getSupplierCategoryId(),
					supplierCategory.getSupplierCategory()
				});
				for (Supplier supplier : supplierCategory.getSuppliers()) {
					DbHandler.performExecuteInsert(supplierStatement, new Object[]{
						supplier.getCategoryId(),
						supplierCategory.getSupplierCategoryId(),
						supplier.getCategoryDescription()
					});
					for (Item item : supplier.getItems()) {
						int itemId = item.getItemId();
						Object[] itemParameters = {
							itemId,
							supplier.getCategoryId(),
							item.getItemCode(),
							item.getItemDescription(),
							item.getPrice()
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
			database.setTransactionSuccessful();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			database.endTransaction();
			databaseHelper.close();
		}
	}

	public static ArrayList<SupplierCategory> loadItemsFromDb(Context context) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		ArrayList<SupplierCategory> supplierCategories = new ArrayList<SupplierCategory>();
		String supplierCategorySql = "select supplierCategoryId,supplierCategory from tbl_supplier_category";
		String supplierSql = "select supplierId,supplierName from tbl_supplier where supplierCategoryId=?";
		String itemSql = "select itemId,itemCode,itemDescription,price from tbl_item where supplierId=?";
		Cursor supplierCategoryCursor = DbHandler.performRawQuery(database, supplierCategorySql, null);
		for (supplierCategoryCursor.moveToFirst(); !supplierCategoryCursor.isAfterLast(); supplierCategoryCursor.moveToNext()) {
			int supplierCategoryId;
			Cursor supplierCursor = DbHandler.performRawQuery(database, supplierSql, new Object[]{supplierCategoryId = supplierCategoryCursor.getInt(0)});
			ArrayList<Supplier> suppliers = new ArrayList<Supplier>();
			for (supplierCursor.moveToFirst(); !supplierCursor.isAfterLast(); supplierCursor.moveToNext()) {
				int supplierId;
				String supplierDescription = supplierCursor.getString(1);
				ArrayList<Item> items = new ArrayList<Item>();
				Cursor itemCursor = DbHandler.performRawQuery(database, itemSql, new Object[]{supplierId = supplierCursor.getInt(0)});
				for (itemCursor.moveToFirst(); !itemCursor.isAfterLast(); itemCursor.moveToNext()) {
					items.add(new Item(
						itemCursor.getInt(0),
						itemCursor.getString(1),
						itemCursor.getString(2),
						itemCursor.getDouble(3)
					));
				}
				itemCursor.close();
				suppliers.add(new Supplier(supplierId, supplierDescription, items));
			}
			supplierCursor.close();
			String supplierCategoryDescription = supplierCategoryCursor.getString(1);
			SupplierCategory supplierCategory = new SupplierCategory(supplierCategoryId, supplierCategoryDescription, suppliers);
			supplierCategories.add(supplierCategory);
		}
		supplierCategoryCursor.close();
		databaseHelper.close();
		return supplierCategories;
	}

}
