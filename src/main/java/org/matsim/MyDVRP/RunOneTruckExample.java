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

package org.matsim.MyDVRP;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.dvrp.examples.onetaxi.OneTaxiModule;
import org.matsim.contrib.dvrp.examples.onetruck.OneTruckModule;
import org.matsim.contrib.dvrp.passenger.PassengerEngineQSimModule.PassengerEngineType;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpModule;
import org.matsim.contrib.dvrp.run.DvrpQSimComponents;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;

/**
 * @author michalm
 */
public final class RunOneTruckExample {
	public static void main(String args[]){

		new RunOneTruckExample().run();
	}
	public static void run() {
		// load config
		Config config = ConfigUtils.loadConfig("examples/dvrp-grid/one_truck_config.xml", new DvrpConfigGroup(), new OTFVisConfigGroup());
		config.controler().setLastIteration(0);
		System.out.println(config.controler().getOutputDirectory());
		//System.exit(0);
		// load scenario
		Scenario scenario = ScenarioUtils.loadScenario(config);

		// setup controler
		Controler controler = new Controler(scenario);
		controler.addOverridingModule(new DvrpModule());
		controler.addOverridingModule(new OneTruckModule(ConfigGroup.getInputFileURL(config.getContext(), "one_truck_vehicles.xml")));
		controler.configureQSimComponents(DvrpQSimComponents.activateModes(TransportMode.truck));


		if (false) {
			controler.addOverridingModule(new OTFVisLiveModule()); // OTFVis visualisation
		}

		// run simulation
		controler.run();
	}
}
