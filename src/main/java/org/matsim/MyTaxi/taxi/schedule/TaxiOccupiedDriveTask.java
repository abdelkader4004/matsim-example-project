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

import org.matsim.MyTaxi.taxi.passenger.TaxiRequest;
import org.matsim.contrib.dvrp.path.VrpPathWithTravelData;
import org.matsim.contrib.dvrp.schedule.DefaultDriveTask;

import static org.matsim.MyTaxi.taxi.schedule.TaxiTaskBaseType.OCCUPIED_DRIVE;

public class TaxiOccupiedDriveTask extends DefaultDriveTask {
	public static final TaxiTaskType TYPE = new TaxiTaskType(OCCUPIED_DRIVE);

	public TaxiOccupiedDriveTask(VrpPathWithTravelData path, TaxiRequest request) {
		super(TYPE, path);
		if (request.getFromLink() != path.getFromLink() && request.getToLink() != path.getToLink()) {
			throw new IllegalArgumentException();
		}
	}
}
