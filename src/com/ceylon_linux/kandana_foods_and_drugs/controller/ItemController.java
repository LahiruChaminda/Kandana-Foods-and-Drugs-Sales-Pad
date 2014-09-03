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
		SQLiteDatabaseHelper databaseHelper = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		try {
			database.beginTransaction();

			SQLiteStatement distributorStatement = database.compileStatement("insert into tbl_distributor(distributorId,distributorName) values (?,?)");
			SQLiteStatement supplierStatement = database.compileStatement("insert into tbl_supplier( supplierId, supplierName, distributorId) values (?,?,?)");
			SQLiteStatement categoryStatement = database.compileStatement("insert into tbl_category( categoryId, supplierId, categoryName, distributorId) values (?,?,?,?)");
			SQLiteStatement itemStatement = database.compileStatement("insert into tbl_item( itemId, supplierId, itemCode, itemDescription, price, categoryId, distributorId, packSize, stock, rp_id) values (?,?,?,?,?,?,?,?,?,?)");
			SQLiteStatement freeIssueStatement = database.compileStatement("insert into tbl_free_issue_ratio (itemId, rangeMinimumQuantity, freeIssueQuantity) values(?,?,?)");

			//download distributors
			JSONObject distributorResponseJson = getJsonObject(DistributorURLPack.GET_DISTRIBUTORS, null, context);
			JSONArray distributorJsonCollection = distributorResponseJson.getJSONArray("distributor");
			ArrayList<Distributor> distributors = new ArrayList<Distributor>();
			for (int i = 0, DISTRIBUTOR_JSON_COLLECTION = distributorJsonCollection.length(); i < DISTRIBUTOR_JSON_COLLECTION; i++) {
				Distributor distributor = Distributor.parseDistributor(distributorJsonCollection.getJSONObject(i));
				distributors.add(distributor);
				saveDistributor(distributor, distributorStatement);
			}

			//download suppliers
			JSONObject supplierResponseJson = getJsonObject(DistributorURLPack.GET_SUPPLIERS, DistributorURLPack.getSuppliersParameters(userId), context);
			JSONArray supplierJsonCollection = supplierResponseJson.getJSONArray("supplier");
			for (Distributor distributor : distributors) {
				for (int i = 0, SUPPLIER_JSON_COLLECTION = supplierJsonCollection.length(); i < SUPPLIER_JSON_COLLECTION; i++) {
					Supplier supplier = Supplier.parseSupplier(supplierJsonCollection.getJSONObject(i));
					saveSupplierToDb(supplier, supplierStatement, distributor.getDistributorId());
				}
			}

			//download item categories
			JSONObject categoryResponseJson = getJsonObject(DistributorURLPack.GET_ITEM_CATEGORIES, DistributorURLPack.getItemCategoryParameters(userId), context);
			JSONArray categoryJsonCollection = categoryResponseJson.getJSONArray("category");
			for (Distributor distributor : distributors) {
				for (int i = 0, CATEGORY_JSON_COLLECTION = categoryJsonCollection.length(); i < CATEGORY_JSON_COLLECTION; i++) {
					Category category = Category.parseCategory(categoryJsonCollection.getJSONObject(i));
					saveCategoryToDb(category, distributor.getDistributorId(), categoryStatement);//0 is distributorId
				}
			}

			//download products
			for (Distributor distributor : distributors) {
				JSONObject productResponseJson = getJsonObject(DistributorURLPack.GET_PRODUCTS, DistributorURLPack.getProductsParameters(distributor.getDistributorId()), context);
				JSONArray productJsonCollection = productResponseJson.getJSONArray("product");
				for (int i = 0, PRODUCT_JSON_COLLECTION = productJsonCollection.length(); i < PRODUCT_JSON_COLLECTION; i++) {
					Item item = Item.parseItem(productJsonCollection.getJSONObject(i));
					saveItemsToDb(item, distributor.getDistributorId(), itemStatement, freeIssueStatement);
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
		String supplierSql = "select distinct supplierId,supplierName from tbl_supplier where distributorId=? order by supplierName asc";
		String categorySql = "select distinct categoryId,categoryName from tbl_category where supplierId=? and distributorId=? order by categoryName asc";
		String itemSql = "select distinct itemId,itemCode,itemDescription,price,packSize,stock, rp_id from tbl_item where supplierId=? and distributorId=? and categoryId=? order by itemDescription asc";
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
						itemCursor.getInt(5),
						itemCursor.getInt(6)
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
		String itemSql = "select distinct itemId,itemCode,itemDescription,price,packSize,stock,rp_id from tbl_item where categoryId=? and distributorId=? order by itemDescription asc";
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
					itemCursor.getInt(5),
					itemCursor.getInt(6)
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
		String itemSql = "select distinct ti.itemId,ti.itemCode,ti.itemDescription,ti.price,packSize,stock,rp_id from tbl_item as ti where distributorId=? order by ti.itemDescription asc";
		HashSet<Item> items = new HashSet<Item>();
		Cursor itemCursor = DbHandler.performRawQuery(database, itemSql, new Object[]{distributorId});
		for (itemCursor.moveToFirst(); !itemCursor.isAfterLast(); itemCursor.moveToNext()) {
			items.add(new Item(
				itemCursor.getInt(0),
				itemCursor.getString(1),
				itemCursor.getString(2),
				itemCursor.getDouble(3),
				itemCursor.getString(4),
				itemCursor.getInt(5),
				itemCursor.getInt(6)
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
		String distributorSql = "select distinct distributorId,distributorName from tbl_distributor order by distributorName asc";
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

	public static void saveDistributor(Distributor distributor, SQLiteStatement distributorStatement) {
		DbHandler.performExecuteInsert(distributorStatement, new Object[]{
			distributor.getDistributorId(),
			distributor.getDistributorName()
		});
	}

	public static void saveSupplierToDb(Supplier supplier, SQLiteStatement supplierStatement, int distributorId) {
		DbHandler.performExecuteInsert(supplierStatement, new Object[]{
			supplier.getSupplierId(),
			supplier.getSupplierName(),
			distributorId
		});
	}

	public static void saveCategoryToDb(Category category, int distributorId, SQLiteStatement categoryStatement) {
		DbHandler.performExecuteInsert(categoryStatement, new Object[]{
			category.getCategoryId(),
			category.getSupplierId(),
			category.getCategoryDescription(),
			distributorId
		});
	}

	public static void saveItemsToDb(Item item, int distributorId, SQLiteStatement itemStatement, SQLiteStatement freeIssueStatement) {
		int itemId;
		DbHandler.performExecuteInsert(itemStatement, new Object[]{
			itemId = item.getItemId(),
			item.getSupplierId(),
			item.getItemCode(),
			item.getItemDescription(),
			item.getPrice(),
			item.getCategoryId(),
			distributorId,
			item.getPackSize(),
			item.getStock(),
			item.getRp_id()
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
