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

package org.matsim.onetruck;

//import static org.matsim.contrib.dvrp.examples.onetruck.OneTruckOptimizer.OneTruckTaskType;

import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.examples.onetruck.OneTruckRequest;
import org.matsim.contrib.dvrp.schedule.DefaultStayTask;

/**
 * @author michalm
 */
public class OneTruckServeTask extends DefaultStayTask {
	private final org.matsim.contrib.dvrp.examples.onetruck.OneTruckRequest request;

	public OneTruckServeTask(OneTruckOptimizer.OneTruckTaskType taskType, double beginTime, double endTime, Link link,
							 org.matsim.contrib.dvrp.examples.onetruck.OneTruckRequest request) {
		super(taskType, beginTime, endTime, link);
		this.request = request;
	}

	public OneTruckRequest getRequest() {
		return request;
	}
}
