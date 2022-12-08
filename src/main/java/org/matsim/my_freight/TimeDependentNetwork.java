package org.matsim.my_freight;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.scenario.ScenarioUtils;

public class TimeDependentNetwork {
    public static void main(String[] args) {

        Config config= ConfigUtils.loadConfig("scenarios/equil/config.xml");
        config.controler().setLastIteration(0);
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        config.network().setTimeVariantNetwork(true);
        Scenario scenario= ScenarioUtils.loadScenario(config);
        Network network=scenario.getNetwork();

        NetworkChangeEvent networkChangeEvent=new NetworkChangeEvent(7.*3600);
        NetworkChangeEvent.ChangeType type=NetworkChangeEvent.ChangeType.FACTOR;
        double value=.5;
        NetworkChangeEvent.ChangeValue flowCapacityChange=new NetworkChangeEvent.ChangeValue(type,value);
        networkChangeEvent.setFlowCapacityChange(flowCapacityChange);

        for (Link link : network.getLinks().values()) {
            networkChangeEvent.addLink(link);
        }

        NetworkUtils.addNetworkChangeEvent(network,networkChangeEvent);
        Controler controler=new Controler(scenario);
        controler.run();
    }
}
