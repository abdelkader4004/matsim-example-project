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

package org.matsim.MyTaxi.taxi.schedule.reconstruct;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.ActivityStartEvent;
import org.matsim.api.core.v01.events.handler.ActivityEndEventHandler;
import org.matsim.api.core.v01.events.handler.ActivityStartEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.dvrp.vrpagent.VrpAgentLogic;

import java.util.HashMap;
import java.util.Map;

public class StayRecorder implements ActivityStartEventHandler, ActivityEndEventHandler {
	static class Stay {
		final Link link;
		final String activityType;
		final double startTime;
		double endTime;

		private Stay(Link link, String activityType, double startTime) {
			this.link = link;
			this.activityType = activityType;
			this.startTime = startTime;
		}
	}

	private final Map<Id<Person>, Stay> stays = new HashMap<>();
	private final ScheduleReconstructor reconstructor;

	StayRecorder(ScheduleReconstructor reconstructor) {
		this.reconstructor = reconstructor;
	}

	boolean hasOngoingStays() {
		return !stays.isEmpty();
	}

	@Override
	public void handleEvent(ActivityStartEvent event) {
		Id<Person> personId = event.getPersonId();
		ScheduleBuilder builder = reconstructor.scheduleBuilders.get(personId);

		if (builder != null) {
			if (event.getActType().equals(VrpAgentLogic.AFTER_SCHEDULE_ACTIVITY_TYPE)) {
				builder.endSchedule(event.getTime());
			} else {
				Link link = reconstructor.links.get(event.getLinkId());
				stays.put(personId, new Stay(link, event.getActType(), event.getTime()));
			}
		}
	}

	@Override
	public void handleEvent(ActivityEndEvent event) {
		Id<Person> personId = event.getPersonId();

		Stay stay = stays.remove(personId);
		if (stay != null) {
			stay.endTime = event.getTime();
			reconstructor.scheduleBuilders.get(personId).addStay(stay);
		} else if (event.getActType().equals(VrpAgentLogic.BEFORE_SCHEDULE_ACTIVITY_TYPE)) {
			Link startLink = reconstructor.links.get(event.getLinkId());
			reconstructor.scheduleBuilders.put(personId, new ScheduleBuilder(personId, startLink, event.getTime()));
		}
	}

	@Override
	public void reset(int iteration) {
	}
}
