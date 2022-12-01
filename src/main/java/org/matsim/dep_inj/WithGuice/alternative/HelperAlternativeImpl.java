package org.matsim.dep_inj.WithGuice.alternative;

import org.matsim.dep_inj.WithGuice.base.Helper;

public class HelperAlternativeImpl implements Helper {
    public void help() {
        System.out.println(this.getClass().getSimpleName() + " is Alternative helping");
    }
}
