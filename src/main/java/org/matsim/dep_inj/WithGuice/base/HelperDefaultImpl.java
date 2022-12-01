package org.matsim.dep_inj.WithGuice.base;

public class HelperDefaultImpl implements Helper {
    public void help() {
        System.out.println(this.getClass().getSimpleName() + " is helping");
    }
}
