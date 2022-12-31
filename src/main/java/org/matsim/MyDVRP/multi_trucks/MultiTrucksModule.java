/*
 * *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2018 by the members listed in the COPYING,        *
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
 * *********************************************************************** *
 */

package org.matsim.MyDVRP.multi_trucks;

import com.google.inject.Key;
import com.google.inject.name.Names;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.dvrp.fleet.FleetModule;
import org.matsim.contrib.dvrp.optimizer.VrpOptimizer;
import org.matsim.contrib.dvrp.router.DvrpModeRoutingNetworkModule;
import org.matsim.contrib.dvrp.run.AbstractDvrpModeModule;
import org.matsim.contrib.dvrp.run.AbstractDvrpModeQSimModule;
import org.matsim.contrib.dvrp.run.DvrpModes;
import org.matsim.contrib.dvrp.trafficmonitoring.DvrpTravelTimeModule;
import org.matsim.contrib.dvrp.vrpagent.VrpAgentLogic;
import org.matsim.contrib.dvrp.vrpagent.VrpAgentSourceQSimModule;
import org.matsim.core.router.util.TravelTime;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleUtils;

import java.net.URL;

/**
 * @author Michal Maciejewski (michalm)
 */
public class MultiTrucksModule extends AbstractDvrpModeModule {
	private final URL fleetSpecificationUrl;

	public MultiTrucksModule(URL fleetSpecificationUrl) {
		super(TransportMode.truck);
		this.fleetSpecificationUrl = fleetSpecificationUrl;
	}

	@Override
	public void install() {
		DvrpModes.registerDvrpMode(binder(), getMode());
		install(new DvrpModeRoutingNetworkModule(getMode(), false));
		bindModal(TravelTime.class).to(Key.get(TravelTime.class, Names.named(DvrpTravelTimeModule.DVRP_ESTIMATED)));


//		FleetSpecification fleetSpecification = new FleetSpecificationImpl();
//		new FleetReader(fleetSpecification).parse(fleetSpecificationUrl);
//		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//		System.out.println( fleetSpecification);
//		for (DvrpVehicleSpecification value : fleetSpecification.getVehicleSpecifications().values()) {
//			System.out.println("------------------------------------------------");
//			System.out.println( value);
//		}


		install(new FleetModule(getMode(), fleetSpecificationUrl, createTruckType()));

		installQSimModule(new AbstractDvrpModeQSimModule(getMode()) {
			@Override
			protected void configureQSim() {
				install(new VrpAgentSourceQSimModule(getMode()));
				addModalComponent(MultiTrucksRequestCreator.class);
				bindModal(VrpOptimizer.class).to(MultiTrucksOptimizer.class).asEagerSingleton();
				bindModal(VrpAgentLogic.DynActionCreator.class).to(MultiTrucksActionCreator.class).asEagerSingleton();
			}
		});
	}

	private static VehicleType createTruckType() {
		VehicleType truckType = VehicleUtils.getFactory().createVehicleType(Id.create("truckType", VehicleType.class));
		truckType.setLength(15.);
		truckType.setPcuEquivalents(2.5);
		truckType.getCapacity().setSeats(1);
		return truckType;
	}
}
