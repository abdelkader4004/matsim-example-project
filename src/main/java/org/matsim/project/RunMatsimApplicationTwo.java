/* *********************************************************************** *
 * project: org.matsim.*												   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
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
package org.matsim.project;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.application.MATSimApplication;
import org.matsim.core.config.Config;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import picocli.CommandLine;

/**
 * @author nagel
 *
 */
@CommandLine.Command( header = ":: MyScenario ::", version = "1.0")
@MATSimApplication.Prepare({ExampleCommandeTwo.class})
public class RunMatsimApplicationTwo extends MATSimApplication {
	@CommandLine.Option(names = "--speed-reduction",description = "Hello ",defaultValue = ".5")
	private double speedReduction;


	public RunMatsimApplicationTwo() {
		super("scenarios/equil/config.xml");
	}

	public static void main(String[] args) {
		MATSimApplication.run(RunMatsimApplicationTwo.class, args);
	}

	@Override
	protected Config prepareConfig(Config config) {

		config.controler().setOverwriteFileSetting( OverwriteFileSetting.deleteDirectoryIfExists );

		// possibly modify config here

		// ---

		return config;
	}

	@Override
	protected void prepareScenario(Scenario scenario) {

		// possibly modify scenario here
		for (Link link : scenario.getNetwork().getLinks().values()) {
			if(link.getFreespeed()<=50/3.6){
				link.setFreespeed(speedReduction*link.getFreespeed());

			}
		}
		// ---

	}

	@Override
	protected void prepareControler(Controler controler) {

		// possibly modify controler here

//		controler.addOverridingModule( new OTFVisLiveModule() ) ;


		// ---
	}
}
