package org.matsim.dep_inj.WithGuice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import org.matsim.dep_inj.WithGuice.alternative.HelperAlternativeImpl;
import org.matsim.dep_inj.WithGuice.base.Helper;
import org.matsim.dep_inj.WithGuice.base.HelperDefaultImpl;
import org.matsim.dep_inj.WithGuice.base.Simulation;
import org.matsim.dep_inj.WithGuice.base.SimulationDefaultImpl;

class RunSimulation {
    public static void main(String[] args) {

//        var module=new AbstractModule() {
//            @Override
//            protected void configure(){
//                bind(Simulation.class).to (SimulationDefaultImpl.class);
//                bind(Helper.class).to (HelperAlternativeImpl.class);
//            }
//        };
        Module module=new AbstractModule() {
            @Override
            protected void configure() {
                bind( Simulation.class).to(SimulationDefaultImpl.class);
                bind (Helper.class).to(HelperDefaultImpl.class);
            }
        };
        var injector=   Guice.createInjector(module);
        var simulation=  injector.getInstance(Simulation.class);
        simulation.doStep();
    }

}
/*
    class RunSimulation {
        public static void main(String[] args) {
            Helper helper = new HelperDefaultImpl();
            Simulation simulation = new SimulationDefaultImpl(helper);
// ^^^^^^^
// (this is where the dependency on Helper is injected!)
            simulation.doStep();
        }
        interface Helper {
            void help();
        }
        interface Simulation {
            void doStep();
        }
        static class HelperDefaultImpl implements Helper {
            public void help() {
                System.out.println(this.getClass().getSimpleName() + " is helping");
            }
        }
static class SimulationDefaultImpl implements Simulation {
    private final Helper helper;
    SimulationDefaultImpl( Helper helper ){
        this.helper = helper;
    }
    public void doStep() {
        System.out.println( "entering " + this.getClass().getSimpleName());
        helper.help();
        System.out.println( "leaving " + this.getClass().getSimpleName());
    }
}
}*/
