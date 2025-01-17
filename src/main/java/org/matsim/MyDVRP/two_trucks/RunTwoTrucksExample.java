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

package org.matsim.MyDVRP.two_trucks;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

/**
 * @author michalm
 */
public final class RunTwoTrucksExample {
	public static void main(String args[]) throws MalformedURLException {

		new RunTwoTrucksExample().run();
	}
	public static void run() throws MalformedURLException {
		// load config
		Config config = ConfigUtils.loadConfig("examples/MyDvrp/two_trucks_config.xml", new DvrpConfigGroup(), new OTFVisConfigGroup());
		config.controler().setLastIteration(0);
		System.out.println(config.controler().getOutputDirectory());
		//System.exit(0);
		// load scenario
		Scenario scenario = ScenarioUtils.loadScenario(config);

		// setup controler
		Controler controler = new Controler(scenario);
		controler.addOverridingModule(new DvrpModule());
		controler.addOverridingModule(
				new TwoTrucksModule(ConfigGroup.getInputFileURL(config.getContext(),"two_trucks_vehicles.xml")));

		controler.configureQSimComponents(DvrpQSimComponents.activateModes(TransportMode.truck));


		if (false) {
			controler.addOverridingModule(new OTFVisLiveModule()); // OTFVis visualisation
		}

		// run simulation
		controler.run();
	}
}
