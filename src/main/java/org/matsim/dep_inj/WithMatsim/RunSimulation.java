package org.matsim.dep_inj.WithMatsim;


import org.matsim.analysis.*;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.*;
import org.matsim.core.controler.corelisteners.ControlerDefaultCoreListenersModule;

import org.matsim.core.events.EventsManagerModule;
import org.matsim.core.mobsim.DefaultMobsimModule;
import org.matsim.core.population.VspPlansCleanerModule;
import org.matsim.core.replanning.StrategyManagerModule;
import org.matsim.core.replanning.annealing.ReplanningAnnealer;
import org.matsim.core.router.TripRouterModule;
import org.matsim.core.router.costcalculators.TravelDisutilityModule;
import org.matsim.core.scenario.ScenarioByInstanceModule;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.scoring.functions.CharyparNagelScoringFunctionModule;
import org.matsim.core.trafficmonitoring.TravelTimeCalculatorModule;
import org.matsim.core.utils.timing.TimeInterpretationModule;
import org.matsim.counts.CountsModule;
import org.matsim.guice.DependencyGraphModule;
import org.matsim.pt.counts.PtCountsModule;
import org.matsim.vis.snapshotwriters.SnapshotWritersModule;


public class RunSimulation {

    public static void main(String [] args){
        Config config= ConfigUtils.loadConfig("scenarios/equil/config.xml");
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        config.controler().setLastIteration(0);

        Scenario scenario= ScenarioUtils.loadScenario(config);
        com.google.inject.Module module= new AbstractModule() {
            @Override
            public void install() {
                bind (Abc.class).to(AbcImpl.class);
              //  bind(Helper.class).to (HelperImpl.class);
                this.install(new NewControlerModule());
                this.install(new ControlerDefaultCoreListenersModule());


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

                this.install(new ControlerDefaultsModule());
                this.install(new ScenarioByInstanceModule(scenario));
            }
        };
        com.google.inject.Injector  injector= Injector.createInjector(config,module);
        Abc abc=injector.getInstance(Abc.class);
        abc.doSomeThing();
        ControlerI controler = injector.getInstance( ControlerI.class );
        controler.run();

    }
interface Abc{
        void doSomeThing();

    }

    private static class AbcImpl implements Abc{
       // @Inject Helper helper;
        @Override
        public void doSomeThing() {
            System.out.println("AbcImpl Called");
           // helper.help();
        }

    }
/*    interface Helper{

        void help();
    }

    private static class HelperImpl implements Helper{


        @Override
        public void help() {
            System.out.println("HelperImpl Called");
        }
    }*/
}
