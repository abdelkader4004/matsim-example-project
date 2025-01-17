/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,        *
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

package org.matsim.MyDVRP.random;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.dynagent.run.DynActivityEngine;
import org.matsim.contrib.dynagent.run.DynQSimConfigConsistencyChecker;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.QSimConfigGroup.SnapshotStyle;
import org.matsim.core.config.groups.QSimConfigGroup.StarttimeInterpretation;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.mobsim.qsim.AbstractQSimModule;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;
import org.matsim.vis.otfvis.OTFVisConfigGroup.ColoringScheme;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class RunRandomDynAgentExample {
	public static void main(String args[]) throws URISyntaxException, MalformedURLException {

		new org.matsim.MyDVRP.random.RunRandomDynAgentExample().run(
		new URI("file:///C:/Users/abd/IdeaProjects/matsim-example-project/scenarios/equil/config.xml").toURL(),
		"C:/Users/abd/IdeaProjects/matsim-example-project/scenarios/equil/network.xml",false);
	}
	public static void run(URL context, String networkFile, boolean otfvis) {
		OTFVisConfigGroup otfvisConfig = new OTFVisConfigGroup();
		otfvisConfig.setColoringScheme(ColoringScheme.byId);
		otfvisConfig.setDrawNonMovingItems(true);

		Config config = ConfigUtils.createConfig(otfvisConfig);
		config.setContext(context);
		config.qsim().setSimStarttimeInterpretation(StarttimeInterpretation.onlyUseStarttime);
		config.qsim().setSnapshotStyle(SnapshotStyle.queue);
		config.network().setInputFile(networkFile);
		config.controler().setOutputDirectory("./test/output/random_dyn_agent/");
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		config.controler().setLastIteration(0);
		config.addConfigConsistencyChecker(new DynQSimConfigConsistencyChecker());

		Scenario scenario = ScenarioUtils.loadScenario(config);

		Controler controler = new Controler(scenario);
		controler.addOverridingQSimModule(new AbstractQSimModule() {
			@Override
			public void configureQSim() {
				addQSimComponentBinding(DynActivityEngine.COMPONENT_NAME).to(DynActivityEngine.class);
				addQSimComponentBinding("DynAgentSource").to(RandomDynAgentSource.class);
			}
		});
		controler.configureQSimComponents(components -> {
			components.addNamedComponent(DynActivityEngine.COMPONENT_NAME);
			components.addNamedComponent("DynAgentSource");
		});

		if (otfvis) {
			controler.addOverridingModule(new OTFVisLiveModule());
		}

		controler.run();
	}
}
