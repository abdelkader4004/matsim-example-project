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

import com.google.common.base.Preconditions;
import org.matsim.contrib.dvrp.path.VrpPathWithTravelData;
import org.matsim.contrib.dvrp.schedule.DefaultDriveTask;

import static org.matsim.MyTaxi.taxi.schedule.TaxiTaskBaseType.EMPTY_DRIVE;

public class TaxiEmptyDriveTask extends DefaultDriveTask {
	public static final TaxiTaskType TYPE = new TaxiTaskType(EMPTY_DRIVE);

	public TaxiEmptyDriveTask(VrpPathWithTravelData path, TaxiTaskType taskType) {
		super(taskType, path);
		Preconditions.checkArgument(taskType.getBaseType().get() == EMPTY_DRIVE);
	}
}
