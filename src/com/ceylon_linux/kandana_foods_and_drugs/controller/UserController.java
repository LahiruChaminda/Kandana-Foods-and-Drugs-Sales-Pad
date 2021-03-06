/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : May 13, 2014, 8:36:18 AM
 */

package com.ceylon_linux.kandana_foods_and_drugs.controller;

import android.content.Context;
import android.content.SharedPreferences;
import com.ceylon_linux.kandana_foods_and_drugs.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;

/**
 * UserController - Description of UserController
 *
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class UserController extends AbstractController {

	private UserController() {
	}

	public static User getAuthorizedUser(Context context) {
		SharedPreferences userData = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
		int userId;
		String name;
		String address;
		long loginTime;
		if ((loginTime = userData.getLong("loginTime", -1)) == -1) {
			return null;
		}
		Date lastLoginDate = new Date(loginTime);
		Date currentDate = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (!simpleDateFormat.format(lastLoginDate).equalsIgnoreCase(simpleDateFormat.format(currentDate))) {
			return null;
		}
		if ((userId = userData.getInt("userId", -1)) == -1) {
			return null;
		}
		if ((name = userData.getString("name", "")).isEmpty()) {
			return null;
		}
		if ((address = userData.getString("address", "")).isEmpty()) {
			return null;
		}
		return new User(userId, name, address, loginTime);
	}

	public static boolean setAuthorizedUser(Context context, User user) {
		SharedPreferences userData = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userData.edit();
		editor.putInt("userId", user.getUserId());
		editor.putString("name", user.getName());
		editor.putString("address", user.getAddress());
		editor.putLong("loginTime", user.getLoginTime());
		return editor.commit();
	}

	private static String getMD5HashVal(String strToBeEncrypted) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		Formatter formatter = new Formatter();
		try {
			String encryptedString;
			byte[] bytesToBeEncrypted;
			bytesToBeEncrypted = strToBeEncrypted.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] theDigest = md.digest(bytesToBeEncrypted);
			for (byte b : theDigest) {
				formatter.format("%02x", b);
			}
			encryptedString = formatter.toString().toLowerCase();
			return encryptedString;
		} finally {
			formatter.close();
		}
	}

	public static boolean clearAuthentication(Context context) {
		SharedPreferences userData = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userData.edit();
		editor.putInt("userId", -1);
		editor.putString("name", "");
		editor.putString("address", "");
		editor.putLong("loginTime", -1);
		return editor.commit();
	}

	public static User authenticate(Context context, String userName, String password) throws IOException, JSONException {
		JSONObject userJson = getJsonObject(UserURLPack.LOGIN, UserURLPack.getLoginParameters(userName, password), context);
		return User.parseUser(userJson);
	}

	public static ArrayList<String> getMessages(Context context) throws IOException, JSONException {
		ArrayList<String> messages = new ArrayList<String>();
		JSONObject json = getJsonObject(UserURLPack.MESSAGE_BROADCAST, UserURLPack.getMessageBroadcastParameters(getAuthorizedUser(context).getUserId()), context);
		if (json != null && json.getBoolean("result")) {
			JSONArray messageCollection = json.getJSONArray("message");
			for (int i = 0, MESSAGES_LENGTH = messageCollection.length(); i < MESSAGES_LENGTH; i++) {
				messages.add(messageCollection.getJSONObject(i).getString("m_message"));
			}
		}
		return messages;
	}

	public static RepTarget getTarget(Context context) throws IOException, JSONException, ParseException {
		JSONObject json = getJsonObject(UserURLPack.REP_TARGET, UserURLPack.getRepTargetParameters(getAuthorizedUser(context).getUserId()), context);
		return json != null && json.getBoolean("result") ? RepTarget.parseRepTarget((JSONObject) json.getJSONArray("target").get(0)) : null;
	}

	public static class RepTarget {
		private Date startDate;
		private Date endDate;
		private double targetAmount;
		private double archivedAmount;

		private RepTarget() {
		}

		private static RepTarget parseRepTarget(JSONObject repTargetJson) throws JSONException, ParseException {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			RepTarget repTarget = new RepTarget();
			repTarget.startDate = dateFormatter.parse(repTargetJson.getString("trg_fdate"));
			repTarget.endDate = dateFormatter.parse(repTargetJson.getString("trg_tdate"));
			repTarget.targetAmount = repTargetJson.getDouble("trg_amount");
			repTarget.archivedAmount = repTargetJson.getDouble("total");
			return repTarget;
		}

		public Date getStartDate() {
			return startDate;
		}

		public Date getEndDate() {
			return endDate;
		}

		public double getTargetAmount() {
			return targetAmount;
		}

		public double getArchivedAmount() {
			return archivedAmount;
		}
	}
}
