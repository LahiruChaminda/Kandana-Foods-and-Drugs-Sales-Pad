/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : May 9, 2014, 10:10:54 PM
 */

package com.ceylon_linux.kandana_foods_and_drugs.util;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import com.squareup.otto.Produce;

/**
 * GpsReceiver - Receive and Provide GPS locations
 *
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class LocationProviderService extends Service {

	private final long MINIMUM_TIME_DIFFERENCE = 0;
	private final float MINIMUM_DISTANCE_CHANGE = 0;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Location lastKnownLocation;

	@Override
	public void onCreate() {
		super.onCreate();
		//initializing
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				long time = location.getTime();
				long currentTimeMillis = System.currentTimeMillis();
				long timeDifference = Math.abs(time - currentTimeMillis);
				if (timeDifference > AlarmManager.INTERVAL_HALF_HOUR) {
					BusProvider.getInstance().post(lastKnownLocation = location);
				}
			}

			@Override
			public void onProviderDisabled(String provider) {
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}
		};

		//request location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_DIFFERENCE, MINIMUM_DISTANCE_CHANGE, locationListener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MINIMUM_TIME_DIFFERENCE, MINIMUM_DISTANCE_CHANGE, locationListener);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		locationManager.removeUpdates(locationListener);
		super.onDestroy();
	}

	@Produce
	public Location broadcastLocation() {
		return lastKnownLocation;
	}
}
