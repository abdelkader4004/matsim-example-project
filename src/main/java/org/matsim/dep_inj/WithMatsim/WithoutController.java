package org.matsim.dep_inj.WithMatsim;


import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.*;
import org.matsim.core.controler.corelisteners.ControlerDefaultCoreListenersModule;
import org.matsim.core.router.TripRouter;
import org.matsim.core.scenario.ScenarioByInstanceModule;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.facilities.FacilitiesUtils;

import java.util.List;


public class WithoutController  /*i.e. Use MATSim As a Library */ {

    public static void main(String [] args){
        Config config= ConfigUtils.loadConfig("scenarios/equil/config.xml");
        config.plansCalcRoute().setRoutingRandomness(0.);
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        config.controler().setLastIteration(0);

        Scenario scenario= ScenarioUtils.loadScenario(config);
        com.google.inject.Module module= new AbstractModule() {
            @Override
            public void install() {

                this.install(new NewControlerModule());
                this.install(new ControlerDefaultCoreListenersModule());
                this.install(new ControlerDefaultsModule());
                this.install(new ScenarioByInstanceModule(scenario));

//                this.install(new EventsManagerModule());
//                this.install(new DefaultMobsimModule());
//                this.install(new TravelTimeCalculatorModule());
//                this.install(new TravelDisutilityModule());
//                this.install(new CharyparNagelScoringFunctionModule());
//                this.install(new TripRouterModule());
//                this.install(new StrategyManagerModule());
//                this.install(new TimeInterpretationModule());
//                if (this.getConfig().replanningAnnealer().isActivateAnnealingModule()) {
//                    this.addControlerListenerBinding().to(ReplanningAnnealer.class);
//                }
//
//                this.install(new LinkStatsModule());
//                this.install(new VolumesAnalyzerModule());
//                this.install(new LegHistogramModule());
//                this.install(new LegTimesModule());
//                this.install(new IterationTravelStatsModule());
//                this.install(new ScoreStatsModule());
//                this.install(new ModeStatsModule());
//                this.install(new CountsModule());
//                this.install(new PtCountsModule());
//                this.install(new VspPlansCleanerModule());
//                this.install(new SnapshotWritersModule());
//                this.install(new DependencyGraphModule());


            }
        };
        com.google.inject.Injector  injector= Injector.createInjector(config,module);
        TripRouter tripRouter=injector.getInstance(TripRouter.class);
        Link fromLink=scenario.getNetwork().getLinks().get(Id.createLinkId("1"));
        Link toLink=scenario.getNetwork().getLinks().get(Id.createLinkId("23"));
        List<? extends PlanElement> result = tripRouter.calcRoute(TransportMode.car, FacilitiesUtils.wrapLink(fromLink),
                FacilitiesUtils.wrapLink(toLink), 0, null, null);
        for (PlanElement planElement : result) {
            System.out.println("°°° "+planElement+ " ***");
            if(planElement instanceof Leg){
                System.out.println(((Leg) planElement).getRoute().getRouteDescription());
            }

        }

    }

}
