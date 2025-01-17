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

package org.matsim.MyDVRP.multi_trucks;

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

/**
 * @author michalm
 */
public final class RunMultiTrucksExample {
    public static void main(String args[]) throws MalformedURLException {

        new RunMultiTrucksExample().run();
    }

    public static void run() throws MalformedURLException {
        var  dvrpConfigGroup=new DvrpConfigGroup();
       // dvrpConfigGroup.setTravelTimeEstimationBeta(100);
        // load config
        Config config = ConfigUtils.loadConfig("examples/MyDvrp/multi_trucks_config.xml", dvrpConfigGroup, new OTFVisConfigGroup());

        config.controler().setLastIteration(0);
        System.out.println(config.controler().getOutputDirectory());
        //System.exit(0);
        // load scenario
        Scenario scenario = ScenarioUtils.loadScenario(config);

        // setup controler
        Controler controler = new Controler(scenario);
        controler.addOverridingModule(new DvrpModule());
        controler.addOverridingModule(
                new MultiTrucksModule(ConfigGroup.getInputFileURL(config.getContext(), "multi_trucks_vehicles.xml")));

        controler.configureQSimComponents(DvrpQSimComponents.activateModes(TransportMode.truck));


        if (1==0) {
            controler.addOverridingModule(new OTFVisLiveModule()); // OTFVis visualisation
        }
        //run thread
        var bc = new BufferConsumer();
        Thread thread = new Thread(bc);
        thread.start();
        // run simulation
        controler.run();
        bc.halte();
    }
}
