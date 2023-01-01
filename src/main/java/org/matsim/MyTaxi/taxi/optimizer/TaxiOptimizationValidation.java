/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,        *
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
 * *********************************************************************** */

package org.matsim.MyTaxi.taxi.optimizer;

import org.matsim.MyTaxi.taxi.passenger.TaxiRequests;
import org.matsim.MyTaxi.taxi.scheduler.TaxiScheduleInquiry;
import org.matsim.contrib.dvrp.fleet.Fleet;
import org.matsim.contrib.dvrp.optimizer.Request;
import org.matsim.MyTaxi.taxi.passenger.TaxiRequest.TaxiRequestStatus;

import java.util.stream.Stream;

public class TaxiOptimizationValidation {
	public static void assertNoUnplannedRequestsWhenIdleVehicles(TaxiScheduleInquiry taxiScheduleInquiry, Fleet fleet,
                                                                 Stream<? extends Request> requests) {
		if (fleet.getVehicles().values().stream().anyMatch(taxiScheduleInquiry::isIdle)
				&& TaxiRequests.countRequestsWithStatus(requests, TaxiRequestStatus.UNPLANNED) > 0) {
			throw new IllegalStateException("idle vehicles and unplanned requests");
		}
	}
}
