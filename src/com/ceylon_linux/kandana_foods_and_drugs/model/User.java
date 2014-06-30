/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 13, 2014, 11:54:31 AM
 */

package com.ceylon_linux.kandana_foods_and_drugs.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class User implements Serializable {

	private int userId;
	private String name;
	private String address;
	private Long loginTime;
	private boolean validUser;

	private User(boolean validUser) {
		this.validUser = validUser;
	}

	public User(int userId) {
		this.userId = userId;
		this.validUser = true;
	}

	public User(int userId, String name, String address, Long loginTime) {
		this.userId = userId;
		this.name = name;
		this.address = address;
		this.loginTime = loginTime;
		this.validUser = true;
	}

	public static User parseUser(JSONObject userJsonInstance) throws JSONException {
		if (userJsonInstance == null) {
			return null;
		} else if (!userJsonInstance.getBoolean("result")) {
			return new User(false);
		}
		return new User(
			userJsonInstance.getInt("userId"),
			userJsonInstance.getString("name"),
			userJsonInstance.getString("postalAddress"),
			new Date().getTime()
		);
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Long loginTime) {
		this.loginTime = loginTime;
	}

	public boolean isValidUser() {
		return validUser;
	}
}
