/* *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2017 by the members listed in the COPYING,        *
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

package org.matsim.MyTaxi.taxi.optimizer.fifo;

import org.matsim.MyTaxi.taxi.optimizer.BestDispatchFinder;
import org.matsim.MyTaxi.taxi.optimizer.UnplannedRequestInserter;
import org.matsim.MyTaxi.taxi.passenger.TaxiRequest;
import org.matsim.MyTaxi.taxi.scheduler.TaxiScheduler;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.dvrp.fleet.Fleet;
import org.matsim.core.mobsim.framework.MobsimTimer;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;

import java.util.Collection;

/**
 * @author michalm
 */
public class FifoRequestInserter implements UnplannedRequestInserter {
	private final Fleet fleet;
	private final TaxiScheduler scheduler;
	private final BestDispatchFinder dispatchFinder;

	public FifoRequestInserter(Network network, Fleet fleet, MobsimTimer timer, TravelTime travelTime,
			TravelDisutility travelDisutility, TaxiScheduler scheduler) {
		this.fleet = fleet;
		this.scheduler = scheduler;
		dispatchFinder = new BestDispatchFinder(scheduler.getScheduleInquiry(), network, timer, travelTime,
				travelDisutility);
	}

	@Override
	public void scheduleUnplannedRequests(Collection<TaxiRequest> unplannedRequests) {
		new FifoSchedulingProblem(fleet, scheduler, dispatchFinder).scheduleUnplannedRequests(unplannedRequests);
	}
}
