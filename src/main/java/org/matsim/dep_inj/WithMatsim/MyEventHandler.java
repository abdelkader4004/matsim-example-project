package org.matsim.dep_inj.WithMatsim;

import com.google.inject.Inject;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.mobsim.framework.Mobsim;

class MyEventHandler implements LinkLeaveEventHandler {
    private  MyHelperHandler MyHelperHandler;
    Scenario scenario;
    @Inject
    MyEventHandler(Scenario scenario){
        this.scenario=scenario;
        this.MyHelperHandler=new MyHelperHandler(scenario.getNetwork());
    }
    @Override
    public void handleEvent(LinkLeaveEvent event) {
        MyHelperHandler.help();

    }

    @Override
    public void reset(int iteration) {
        LinkLeaveEventHandler.super.reset(iteration);
    }

   class MyHelperHandler{
       private final Network network;

       public MyHelperHandler(Network network) {
           this.network=network;
       }
       public void help(){
           for (Link link : network.getLinks().values()) {
               System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH "+ link.getId());
           }

       }
   }
    
}
