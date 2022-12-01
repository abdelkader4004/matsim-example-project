package org.matsim.dep_inj.WithGuice.base;

import com.google.inject.Inject;

public class SimulationDefaultImpl implements Simulation {
    @Inject
    private Helper helper;
    @Inject
    private Helper helper2;

    //        SimulationDefaultImpl( Helper helper ){
//            this.helper = helper;
//        }
    public void doStep() {
        System.out.println("entering " + this.getClass().getSimpleName());
        helper.help();
        System.out.println("leaving " + this.getClass().getSimpleName());
    }
}
