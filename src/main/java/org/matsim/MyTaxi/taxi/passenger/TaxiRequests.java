/*
 * *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2019 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** *
 */

package org.matsim.MyTaxi.taxi.passenger;

import org.matsim.contrib.dvrp.optimizer.Request;
import org.matsim.MyTaxi.taxi.passenger.TaxiRequest.TaxiRequestStatus;

import java.util.stream.Stream;

public class TaxiRequests {
	public static long countRequestsWithStatus(Stream<? extends Request> requests, TaxiRequestStatus status) {
		return requests.filter(r -> ((TaxiRequest)r).getStatus() == status).count();
	}
}
