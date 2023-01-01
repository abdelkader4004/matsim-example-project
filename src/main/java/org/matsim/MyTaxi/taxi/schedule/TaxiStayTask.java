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

package org.matsim.MyTaxi.taxi.schedule;

import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.schedule.DefaultStayTask;

import static org.matsim.MyTaxi.taxi.schedule.TaxiTaskBaseType.STAY;

public class TaxiStayTask extends DefaultStayTask {
	public static final TaxiTaskType TYPE = new TaxiTaskType(STAY);

	public TaxiStayTask(double beginTime, double endTime, Link link) {
		super(TYPE, beginTime, endTime, link);
	}
}
