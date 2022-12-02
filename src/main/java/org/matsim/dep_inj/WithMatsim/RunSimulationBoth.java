package org.matsim.dep_inj.WithMatsim;


import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.*;
import org.matsim.core.controler.corelisteners.ControlerDefaultCoreListenersModule;
import org.matsim.core.scenario.ScenarioByInstanceModule;
import org.matsim.core.scenario.ScenarioUtils;


public class RunSimulationBoth {

    public static void main(String [] args){
        Config config= ConfigUtils.loadConfig("scenarios/equil/config.xml");
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        config.controler().setLastIteration(0);
        Scenario scenario= ScenarioUtils.loadScenario(config);

//        com.google.inject.Module module= new AbstractModule() {
//            @Override
//            public void install() {
//                bind (Abc.class).to(AbcImpl.class);
//              //  bind(Helper.class).to (HelperImpl.class);
//                this.install(new NewControlerModule());
//                this.install(new ControlerDefaultCoreListenersModule());
//                this.install(new ControlerDefaultsModule());
//                this.install(new ScenarioByInstanceModule(scenario));
//
//                this.addEventHandlerBinding().toInstance(null);
//            }
//        };

        final AbstractModule module=new AbstractModule() {
            @Override
            public void install() {
                this.addEventHandlerBinding().toInstance(null);
            }
        };
        Controler controler=new Controler(scenario);
        controler.addOverridingModule(module);
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
