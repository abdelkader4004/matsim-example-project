/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2016 by the members listed in the COPYING,        *
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

import com.google.inject.Provider;
import org.matsim.MyTaxi.taxi.optimizer.assignment.AssignmentRequestInserter;
import org.matsim.MyTaxi.taxi.optimizer.assignment.AssignmentTaxiOptimizerParams;
import org.matsim.MyTaxi.taxi.optimizer.fifo.FifoRequestInserter;
import org.matsim.MyTaxi.taxi.optimizer.fifo.FifoTaxiOptimizerParams;
import org.matsim.MyTaxi.taxi.optimizer.rules.*;
import org.matsim.MyTaxi.taxi.optimizer.zonal.ZonalRequestInserter;
import org.matsim.MyTaxi.taxi.optimizer.zonal.ZonalTaxiOptimizerParams;
import org.matsim.MyTaxi.taxi.run.TaxiConfigGroup;
import org.matsim.MyTaxi.taxi.scheduler.TaxiScheduler;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.dvrp.fleet.Fleet;
import org.matsim.contrib.dvrp.schedule.ScheduleTimingUpdater;
import org.matsim.contrib.zone.SquareGridSystem;
import org.matsim.contrib.zone.ZonalSystem;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.mobsim.framework.MobsimTimer;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;

import java.net.URL;

public class DefaultTaxiOptimizerProvider implements Provider<TaxiOptimizer> {
	private final EventsManager eventsManager;
	private final TaxiConfigGroup taxiCfg;
	private final Fleet fleet;
	private final Network network;
	private final MobsimTimer timer;
	private final TravelTime travelTime;
	private final TravelDisutility travelDisutility;
	private final TaxiScheduler scheduler;
	private final URL context;
	private final ScheduleTimingUpdater scheduleTimingUpdater;

	public DefaultTaxiOptimizerProvider(EventsManager eventsManager, TaxiConfigGroup taxiCfg, Fleet fleet,
                                        Network network, MobsimTimer timer, TravelTime travelTime, TravelDisutility travelDisutility,
                                        TaxiScheduler scheduler, ScheduleTimingUpdater scheduleTimingUpdater, URL context) {
		this.eventsManager = eventsManager;
		this.taxiCfg = taxiCfg;
		this.fleet = fleet;
		this.network = network;
		this.timer = timer;
		this.travelTime = travelTime;
		this.travelDisutility = travelDisutility;
		this.scheduler = scheduler;
		this.scheduleTimingUpdater = scheduleTimingUpdater;
		this.context = context;
	}

	@Override
	public TaxiOptimizer get() {
		switch (taxiCfg.getTaxiOptimizerParams().getName()) {
			case AssignmentTaxiOptimizerParams.SET_NAME: {
				var requestInserter = new AssignmentRequestInserter(fleet, network, timer, travelTime, travelDisutility,
						scheduler, (AssignmentTaxiOptimizerParams)taxiCfg.getTaxiOptimizerParams());
				return new DefaultTaxiOptimizer(eventsManager, taxiCfg, fleet, scheduler, scheduleTimingUpdater,
						requestInserter);
			}

			case FifoTaxiOptimizerParams.SET_NAME: {
				var requestInserter = new FifoRequestInserter(network, fleet, timer, travelTime, travelDisutility,
						scheduler);
				return new DefaultTaxiOptimizer(eventsManager, taxiCfg, fleet, scheduler, scheduleTimingUpdater,
						requestInserter);
			}

			case RuleBasedTaxiOptimizerParams.SET_NAME: {
				var zonalRegisters = createZonalRegisters(
						((RuleBasedTaxiOptimizerParams)taxiCfg.getTaxiOptimizerParams()));
				var requestInserter = new RuleBasedRequestInserter(scheduler, timer, network, travelTime,
						travelDisutility, ((RuleBasedTaxiOptimizerParams)taxiCfg.getTaxiOptimizerParams()),
						zonalRegisters);
				return new RuleBasedTaxiOptimizer(eventsManager, taxiCfg, fleet, scheduler, scheduleTimingUpdater,
						zonalRegisters, requestInserter);
			}

			case ZonalTaxiOptimizerParams.SET_NAME: {
				var zonalRegisters = createZonalRegisters(
						((ZonalTaxiOptimizerParams)taxiCfg.getTaxiOptimizerParams()).getRuleBasedTaxiOptimizerParams());
				var requestInserter = new ZonalRequestInserter(fleet, scheduler, timer, network, travelTime,
						travelDisutility, ((ZonalTaxiOptimizerParams)taxiCfg.getTaxiOptimizerParams()), zonalRegisters,
						context);
				return new RuleBasedTaxiOptimizer(eventsManager, taxiCfg, fleet, scheduler, scheduleTimingUpdater,
						zonalRegisters, requestInserter);
			}

			default:
				throw new RuntimeException(
						"Unsupported taxi optimizer type: " + taxiCfg.getTaxiOptimizerParams().getName());
		}
	}

	private ZonalRegisters createZonalRegisters(RuleBasedTaxiOptimizerParams params) {
		ZonalSystem zonalSystem = new SquareGridSystem(network.getNodes().values(), params.getCellSize());
		IdleTaxiZonalRegistry idleTaxiRegistry = new IdleTaxiZonalRegistry(zonalSystem, scheduler.getScheduleInquiry());
		UnplannedRequestZonalRegistry unplannedRequestRegistry = new UnplannedRequestZonalRegistry(zonalSystem);
		return new ZonalRegisters(idleTaxiRegistry, unplannedRequestRegistry);
	}
}
