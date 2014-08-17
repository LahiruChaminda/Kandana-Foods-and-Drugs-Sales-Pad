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
import com.ceylon_linux.kandana_foods_and_drugs.model.*;
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

	public static void downloadItems(Context context, int userId) throws IOException, JSONException {
		JSONObject responseJson = getJsonObject(DistributorURLPack.GET_DISTRIBUTORS, null, context);
		JSONArray distributorJsonArray = responseJson.getJSONArray("distributor");
		ArrayList<Distributor> distributors = new ArrayList<Distributor>();
		for (int i = 0, DISTRIBUTOR_LENGTH = distributorJsonArray.length(); i < DISTRIBUTOR_LENGTH; i++) {
			Distributor distributor = Distributor.parseDistributor(distributorJsonArray.getJSONObject(i));
			if (distributor != null) {
				distributors.add(distributor);
			}
		}
		for (Distributor distributor : distributors) {
			JSONObject distributorJsonInstance = getJsonObject(DistributorURLPack.GET_SUPPLIERS, DistributorURLPack.getSuppliersParameters(userId), context);
			if (distributorJsonInstance.getBoolean("result")) {
				JSONArray supplierJsonCollection = distributorJsonInstance.getJSONArray("supplier");
				HashSet<Supplier> suppliers = new HashSet<Supplier>();
				for (int i = 0, SUPPLIER_LENGTH = supplierJsonCollection.length(); i < SUPPLIER_LENGTH; i++) {
					Supplier supplier = Supplier.parseSupplier(supplierJsonCollection.getJSONObject(i));
					if (supplier != null) {
						suppliers.add(supplier);
					}
				}
				for (Supplier supplier : suppliers) {
					JSONObject categoryJsonInstance = getJsonObject(DistributorURLPack.GET_CATEGORIES, DistributorURLPack.getCategoryParameters(supplier.getSupplierId()), context);
					HashSet<Category> categories = new HashSet<Category>();
					if (categoryJsonInstance.getBoolean("result")) {
						JSONArray categoryJsonCollection = categoryJsonInstance.getJSONArray("category");
						for (int i = 0, CATEGORY_SIZE = categoryJsonCollection.length(); i < CATEGORY_SIZE; i++) {
							Category category = Category.parseCategory(categoryJsonCollection.getJSONObject(i));
							if (category != null) {
								categories.add(category);
							}
						}
						supplier.setCategories(new ArrayList<Category>(categories));
					}
					for (Category category : categories) {
						JSONObject itemJsonInstance = getJsonObject(DistributorURLPack.GET_ITEMS, DistributorURLPack.getProductsParameters(category.getCategoryId(), distributor.getDistributorId()), context);
						HashSet<Item> items = new HashSet<Item>();
						if (itemJsonInstance.getBoolean("result")) {
							JSONArray itemCollection = itemJsonInstance.getJSONArray("product");
							for (int i = 0, ITEM_SIZE = itemCollection.length(); i < ITEM_SIZE; i++) {
								Item item = Item.parseItem(itemCollection.getJSONObject(i));
								if (item != null) {
									items.add(item);
								}
							}
							category.setItems(new ArrayList<Item>(items));
						}
					}
				}
				distributor.setSupplierCategories(new ArrayList<Supplier>(suppliers));
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
			SQLiteStatement supplierStatement = database.compileStatement("insert into tbl_supplier( supplierId, supplierName, distributorId) values (?,?,?)");
			SQLiteStatement categoryStatement = database.compileStatement("insert into tbl_category( categoryId, supplierId, categoryName, distributorId) values (?,?,?,?)");
			SQLiteStatement itemStatement = database.compileStatement("insert into tbl_item( itemId, supplierId, itemCode, itemDescription, price, categoryId, distributorId, packSize, stock) values (?,?,?,?,?,?,?,?,?)");
			SQLiteStatement freeIssueStatement = database.compileStatement("insert into tbl_free_issue_ratio (itemId, rangeMinimumQuantity, freeIssueQuantity) values(?,?,?)");
			for (Distributor distributor : distributors) {
				int distributorId;
				DbHandler.performExecuteInsert(distributorStatement, new Object[]{
					distributorId = distributor.getDistributorId(),
					distributor.getDistributorName()
				});
				for (Supplier supplier : distributor.getSupplierCategories()) {
					int supplierId;
					DbHandler.performExecuteInsert(supplierStatement, new Object[]{
						supplierId = supplier.getSupplierId(),
						supplier.getSupplierName(),
						distributorId
					});
					for (Category category : supplier.getCategories()) {
						int categoryId;
						DbHandler.performExecuteInsert(categoryStatement, new Object[]{
							categoryId = category.getCategoryId(),
							supplierId,
							category.getCategoryDescription(),
							distributorId
						});
						for (Item item : category.getItems()) {
							int itemId;
							DbHandler.performExecuteInsert(itemStatement, new Object[]{
								itemId = item.getItemId(),
								supplierId,
								item.getItemCode(),
								item.getItemDescription(),
								item.getPrice(),
								categoryId,
								distributorId,
								item.getPackSize(),
								item.getStock()
							});
							JSONArray freeIssueJsonArray = item.getFreeIssueJsonArray();
							for (int i = 0, FREE_ISSUE_LENGTH = freeIssueJsonArray.length(); i < FREE_ISSUE_LENGTH; i++) {
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

	public static ArrayList<Supplier> loadSuppliersFromDb(Context context, int distributorId) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		HashSet<Supplier> supplierCategories = new HashSet<Supplier>();
		String supplierSql = "select supplierId,supplierName from tbl_supplier where distributorId=? order by supplierName asc";
		String categorySql = "select categoryId,categoryName from tbl_category where supplierId=? and distributorId=? order by categoryName asc";
		String itemSql = "select itemId,itemCode,itemDescription,price,packSize,stock from tbl_item where supplierId=? and distributorId=? and categoryId=? order by itemDescription asc";
		Cursor supplierCursor = DbHandler.performRawQuery(database, supplierSql, new Object[]{distributorId});
		for (supplierCursor.moveToFirst(); !supplierCursor.isAfterLast(); supplierCursor.moveToNext()) {
			int supplierId;
			Cursor categoryCursor = DbHandler.performRawQuery(database, categorySql, new Object[]{supplierId = supplierCursor.getInt(0), distributorId});
			HashSet<Category> categories = new HashSet<Category>();
			for (categoryCursor.moveToFirst(); !categoryCursor.isAfterLast(); categoryCursor.moveToNext()) {
				int categoryId;
				String categoryDescription = categoryCursor.getString(1);
				HashSet<Item> items = new HashSet<Item>();
				Cursor itemCursor = DbHandler.performRawQuery(database, itemSql, new Object[]{supplierId, distributorId, categoryId = categoryCursor.getInt(0)});
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
				categories.add(new Category(categoryId, categoryDescription, new ArrayList<Item>(items)));
			}
			categoryCursor.close();
			String supplierDescription = supplierCursor.getString(1);
			Supplier supplier = new Supplier(supplierId, supplierDescription, new ArrayList<Category>(categories));
			supplierCategories.add(supplier);
		}
		supplierCursor.close();
		databaseHelper.close();
		return new ArrayList<Supplier>(supplierCategories);
	}

	public static ArrayList<Category> loadCategoriesFromDb(Context context, int distributorId) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		String categorySql = "select distinct tc.categoryId, tc.categoryName from tbl_category as tc inner join tbl_supplier as ts on tc.distributorId = ts.distributorId where ts.distributorId=? order by tc.categoryName asc";
		String itemSql = "select itemId,itemCode,itemDescription,price,packSize,stock from tbl_item where categoryId=? and distributorId=? order by itemDescription asc";
		Cursor categoryCursor = DbHandler.performRawQuery(database, categorySql, new Object[]{distributorId});
		HashSet<Category> categories = new HashSet<Category>();
		for (categoryCursor.moveToFirst(); !categoryCursor.isAfterLast(); categoryCursor.moveToNext()) {
			int categoryId = categoryCursor.getInt(0);
			String categoryDescription = categoryCursor.getString(1);
			HashSet<Item> items = new HashSet<Item>();
			Cursor itemCursor = DbHandler.performRawQuery(database, itemSql, new Object[]{categoryId, distributorId});
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
			categories.add(new Category(categoryId, categoryDescription, new ArrayList<Item>(items)));
		}
		categoryCursor.close();
		databaseHelper.close();
		return new ArrayList<Category>(categories);
	}

	public static ArrayList<Item> loadItemsFromDb(Context context, int distributorId) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		String itemSql = "select ti.itemId,ti.itemCode,ti.itemDescription,ti.price,packSize,stock from tbl_item as ti where distributorId=? order by ti.itemDescription asc";
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
		String distributorSql = "select distributorId,distributorName from tbl_distributor order by distributorName asc";
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

	public static void updateStock(Context context, ArrayList<OrderDetail> orderDetails) {
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		SQLiteStatement statement = database.compileStatement("update tbl_item set stock=(stock - ?) where itemId = ?");
		for (OrderDetail orderDetail : orderDetails) {
			DbHandler.performExecute(statement, new Object[]{
				orderDetail.getQuantity(),
				orderDetail.getItemId()
			});
		}
		statement.close();
		database.close();
	}

}
