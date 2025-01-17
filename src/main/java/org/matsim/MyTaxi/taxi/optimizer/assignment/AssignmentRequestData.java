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

package org.matsim.MyTaxi.taxi.optimizer.assignment;

import com.google.common.collect.ImmutableList;
import org.matsim.MyTaxi.taxi.passenger.TaxiRequest;

class AssignmentRequestData extends AssignmentDestinationData<TaxiRequest> {

	static AssignmentRequestData create(double currentTime, double planningHorizon,
			Iterable<TaxiRequest> unplannedRequests) {
		double maxEarliestStart = currentTime + planningHorizon;
		ImmutableList.Builder<DestEntry<TaxiRequest>> builder = ImmutableList.builder();

		int idx = 0;
		int urgentReqCount = 0;
		for (TaxiRequest r : unplannedRequests) {
			double earliestStart = r.getEarliestStartTime();
			if (earliestStart > maxEarliestStart) {// beyond the planning horizon
				continue;
			}
			if (earliestStart <= currentTime) {
				urgentReqCount++;
			}
			builder.add(new DestEntry<>(idx++, r, r.getFromLink(), earliestStart));
		}

		return new AssignmentRequestData(builder.build(), urgentReqCount);
	}

	private final int urgentReqCount;

	private AssignmentRequestData(ImmutableList<DestEntry<TaxiRequest>> entries, int urgentReqCount) {
		super(entries);
		this.urgentReqCount = urgentReqCount;
	}

	int getUrgentReqCount() {
		return urgentReqCount;
	}
}
