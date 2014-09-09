/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Sep 07, 2014, 9:27:30 AM
 */
package com.ceylon_linux.kandana_foods_and_drugs.util;

import com.squareup.otto.Bus;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class BusProvider {
	private final static Bus BUS = new Bus();

	private BusProvider() {
	}

	public static Bus getInstance() {
		return BUS;
	}
}
