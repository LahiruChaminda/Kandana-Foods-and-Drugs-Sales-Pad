/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2013, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Mar 9, 2014, 5:15:40 PM
 */
package com.ceylon_linux.kandana_foods_and_drugs.model;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class UnProductiveCall implements Serializable {

	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
	private int unProductiveCallId;
	private int outletId;
	private int repId;
	private int batteryLevel;
	private String outletName;
	private String reason;
	private long timestamp;
	private double longitude;
	private double latitude;
	private boolean synced;

	public UnProductiveCall(int outletId, String reason, long timestamp, double longitude, double latitude, int batteryLevel, int repId) {
		this.outletId = outletId;
		this.reason = reason;
		this.timestamp = timestamp;
		this.longitude = longitude;
		this.latitude = latitude;
		this.batteryLevel = batteryLevel;
		this.repId = repId;
	}

	public UnProductiveCall(int unProductiveCallId, int outletId, String outletName, String reason, long timestamp, double longitude, double latitude, int batteryLevel, int repId) {
		this.unProductiveCallId = unProductiveCallId;
		this.outletId = outletId;
		this.outletName = outletName;
		this.reason = reason;
		this.timestamp = timestamp;
		this.longitude = longitude;
		this.latitude = latitude;
		this.batteryLevel = batteryLevel;
		this.repId = repId;
	}

	public boolean isSynced() {
		return synced;
	}

	public void setSyncStatus(boolean synced) {
		this.synced = synced;
	}

	/**
	 * @return the outletId
	 */
	public int getOutletId() {
		return outletId;
	}

	/**
	 * @param outletId the outletId to set
	 */
	public void setOutletId(int outletId) {
		this.outletId = outletId;
	}

	/**
	 * @return the outletName
	 */
	public String getOutletName() {
		return outletName;
	}

	/**
	 * @param outletName the outletName to set
	 */
	public void setOutletName(String outletName) {
		this.outletName = outletName;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getUnProductiveCallId() {
		return unProductiveCallId;
	}

	public void setUnProductiveCallId(int unProductiveCallId) {
		this.unProductiveCallId = unProductiveCallId;
	}

	@Override
	public String toString() {
		return outletName;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public JSONObject getUnProductiveCallAsJson() {
		HashMap<String, Object> unProductiveCallJson = new HashMap<String, Object>();
		unProductiveCallJson.put("shopcode", outletId);
		unProductiveCallJson.put("remarks", reason);
		Date date = new Date(timestamp);
		unProductiveCallJson.put("date", dateFormatter.format(date));
		unProductiveCallJson.put("time", timeFormatter.format(date));
		unProductiveCallJson.put("gpslng", longitude);
		unProductiveCallJson.put("gpslat", latitude);
		unProductiveCallJson.put("repId", repId);
		unProductiveCallJson.put("blevel", batteryLevel);
		return new JSONObject(unProductiveCallJson);
	}

	public int getRepId() {
		return repId;
	}

	public void setRepId(int repId) {
		this.repId = repId;
	}

	public int getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(int batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

}
