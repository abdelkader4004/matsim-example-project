package org.matsim.dep_inj.WithMatsim;


import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.*;
import org.matsim.core.controler.corelisteners.ControlerDefaultCoreListenersModule;
import org.matsim.core.scenario.ScenarioByInstanceModule;
import org.matsim.core.scenario.ScenarioUtils;


public class ExtensionPoints {

    public static void main(String [] args){
        Config config= ConfigUtils.loadConfig("scenarios/equil/config.xml");
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        config.controler().setLastIteration(0);

        Scenario scenario= ScenarioUtils.loadScenario(config);

        Controler controler =  new Controler(scenario);
        controler.addOverridingModule(new AbstractModule() {
            @Override
            public void install() {
                //Adding unnamed things
                addEventHandlerBinding(); // to handle events like we did in basic  course
                addControlerListenerBinding(); // controlerListeners listen to : shutdown or iteration done... i.e. to do something between iterations
                addMobsimListenerBinding(); // do something between time steps

                //these replace existing modules (see video 4 in advanced course mn (40)
                bindScoringFunctionFactory(); //
                bindMobsim();
                bindLeastCostPathCalculatorFactory();

                //Adding named things (if we add something with same name so this is replaced)
//                these are more difficult, but if u think that they resolve your problem go to (matsim-code-examples)
//                addPlanStrategyBinding();
//                addTravelDisutilityFactoryBinding();
//                addTravelTimeBinding();
//                addPlanSelectorForRemovalBinding();
            }
        });
        controler.run();

    }

}
