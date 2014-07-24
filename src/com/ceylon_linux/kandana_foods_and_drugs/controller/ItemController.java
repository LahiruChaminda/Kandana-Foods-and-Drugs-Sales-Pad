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
		ArrayList<Supplier> suppliers = new ArrayList<Supplier>();
		for (SupplierCategory supplierCategory : supplierCategories) {
			suppliers.addAll(supplierCategory.getSuppliers());
		}
		saveSupplierCategoriesToDb(suppliers, context);
	}

	private static void saveSupplierCategoriesToDb(ArrayList<Supplier> categories, Context context) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		try {
			database.beginTransaction();
			SQLiteStatement categoryStatement = database.compileStatement("replace into tbl_category(categoryId,categoryDescription) values (?,?)");
			SQLiteStatement itemStatement = database.compileStatement("replace into tbl_item(itemId,categoryId,itemCode,itemDescription, price) values (?,?,?,?,?)");
			SQLiteStatement freeIssueStatement = database.compileStatement("replace into tbl_free_issue_ratio (itemId, rangeMinimumQuantity,freeIssueQuantity) values(?,?,?)");
			for (Supplier supplier : categories) {
				Object[] categoryParameters = {
					supplier.getCategoryId(),
					supplier.getCategoryDescription()
				};
				DbHandler.performExecuteInsert(categoryStatement, categoryParameters);
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
			database.setTransactionSuccessful();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			database.endTransaction();
			databaseHelper.close();
		}
	}

	public static ArrayList<Supplier> loadItemsFromDb(Context context) throws IOException, JSONException {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		ArrayList<Supplier> categories = new ArrayList<Supplier>();
		String categorySql = "select categoryId,categoryDescription from tbl_category";
		String itemSql = "select itemId,itemCode,itemDescription,price from tbl_item where categoryId=?";
		Cursor categoryCursor = DbHandler.performRawQuery(database, categorySql, null);
		for (categoryCursor.moveToFirst(); !categoryCursor.isAfterLast(); categoryCursor.moveToNext()) {
			int categoryId = categoryCursor.getInt(0);
			String categoryDescription = categoryCursor.getString(1);
			ArrayList<Item> items = new ArrayList<Item>();
			Cursor itemCursor = DbHandler.performRawQuery(database, itemSql, new Object[]{categoryId});
			for (itemCursor.moveToFirst(); !itemCursor.isAfterLast(); itemCursor.moveToNext()) {
				items.add(new Item(
					itemCursor.getInt(0),
					itemCursor.getString(1),
					itemCursor.getString(2),
					itemCursor.getDouble(3)
				));
			}
			itemCursor.close();
			categories.add(new Supplier(categoryId, categoryDescription, items));
		}
		categoryCursor.close();
		databaseHelper.close();
		return categories;
	}

}
