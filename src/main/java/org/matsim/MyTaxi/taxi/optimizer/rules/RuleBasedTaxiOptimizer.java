/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
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

package org.matsim.MyTaxi.taxi.optimizer.rules;

import org.matsim.MyTaxi.taxi.optimizer.DefaultTaxiOptimizer;
import org.matsim.MyTaxi.taxi.optimizer.UnplannedRequestInserter;
import org.matsim.MyTaxi.taxi.passenger.TaxiRequest;
import org.matsim.MyTaxi.taxi.run.TaxiConfigGroup;
import org.matsim.MyTaxi.taxi.schedule.TaxiStayTask;
import org.matsim.MyTaxi.taxi.scheduler.TaxiScheduler;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.fleet.Fleet;
import org.matsim.contrib.dvrp.optimizer.Request;
import org.matsim.contrib.dvrp.schedule.Schedule;
import org.matsim.contrib.dvrp.schedule.Schedule.ScheduleStatus;
import org.matsim.contrib.dvrp.schedule.ScheduleTimingUpdater;
import org.matsim.contrib.dvrp.schedule.Schedules;
import org.matsim.contrib.dvrp.schedule.Task;
import org.matsim.core.api.experimental.events.EventsManager;

import static org.matsim.MyTaxi.taxi.schedule.TaxiTaskBaseType.STAY;

/**
 * @author michalm
 */
public class RuleBasedTaxiOptimizer extends DefaultTaxiOptimizer {

	private final TaxiScheduler scheduler;
	private final IdleTaxiZonalRegistry idleTaxiRegistry;
	private final UnplannedRequestZonalRegistry unplannedRequestRegistry;

	public RuleBasedTaxiOptimizer(EventsManager eventsManager, TaxiConfigGroup taxiCfg, Fleet fleet,
                                  TaxiScheduler scheduler, ScheduleTimingUpdater scheduleTimingUpdater, ZonalRegisters zonalRegisters,
                                  UnplannedRequestInserter requestInserter) {
		super(eventsManager, taxiCfg, fleet, scheduler, scheduleTimingUpdater, requestInserter);

		this.scheduler = scheduler;
		this.idleTaxiRegistry = zonalRegisters.idleTaxiRegistry;
		this.unplannedRequestRegistry = zonalRegisters.unplannedRequestRegistry;

		if (taxiCfg.isVehicleDiversion()) {
			// hmmmm, change into warning?? or even allow it (e.g. for empty taxi relocaton)??
			throw new RuntimeException("Diversion is not supported by RuleBasedTaxiOptimizer");
		}
	}

	@Override
	public void requestSubmitted(Request request) {
		super.requestSubmitted(request);
		unplannedRequestRegistry.addRequest((TaxiRequest)request);
	}

	@Override
	public void nextTask(DvrpVehicle vehicle) {
		super.nextTask(vehicle);

		Schedule schedule = vehicle.getSchedule();
		if (schedule.getStatus() == ScheduleStatus.COMPLETED) {
			TaxiStayTask lastTask = (TaxiStayTask)Schedules.getLastTask(schedule);
			if (lastTask.getBeginTime() < vehicle.getServiceEndTime()) {
				idleTaxiRegistry.removeVehicle(vehicle);
			}
		} else if (scheduler.getScheduleInquiry().isIdle(vehicle)) {
			idleTaxiRegistry.addVehicle(vehicle);
		} else {
			if (schedule.getCurrentTask().getTaskIdx() != 0) {// not first task
				Task previousTask = Schedules.getPreviousTask(schedule);
				if (STAY.isBaseTypeOf(previousTask)) {
					idleTaxiRegistry.removeVehicle(vehicle);
				}
			}
		}
	}

	@Override
	protected boolean doReoptimizeAfterNextTask(Task newCurrentTask) {
		return STAY.isBaseTypeOf(newCurrentTask);
	}
}
