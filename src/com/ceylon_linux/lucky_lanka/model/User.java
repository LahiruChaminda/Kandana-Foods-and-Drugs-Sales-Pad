/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 13, 2014, 11:54:31 AM
 */

package com.ceylon_linux.lucky_lanka.model;

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

	public User(int userId) {
		this.userId = userId;
	}

	public User(int userId, String name, String address, Long loginTime) {
		this.userId = userId;
		this.name = name;
		this.address = address;
		this.loginTime = loginTime;
	}

	public static User parseUser(JSONObject userJsonInstance) throws JSONException {
		if (userJsonInstance == null || !userJsonInstance.getBoolean("result")) {
			return null;
		}
		return new User(
			userJsonInstance.getInt("userId"),
			userJsonInstance.getString("name"),
			userJsonInstance.getString("postal_address"),
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
}
