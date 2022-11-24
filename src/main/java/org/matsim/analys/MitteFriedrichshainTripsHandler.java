package org.matsim.analys;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;
import org.matsim.api.core.v01.population.Person;

import java.util.HashMap;
import java.util.Map;

public class MitteFriedrichshainTripsHandler  implements PersonDepartureEventHandler, PersonArrivalEventHandler {

    private final Map<Id<Person>, Double> departureTimeByPerson = new HashMap<>();

    @Override
    public void handleEvent(PersonArrivalEvent event) {
       // event.
        //System.out.println("PersonArrivalEvent -- time: " + event.getTime() + " -- linkId: " + event.getLinkId() + " -- personId: " + event.getPersonId());
        //double departureTime = departureTimeByPerson.get(event.getPersonId());
        //double travelTime = event.getTime() - departureTime;
        //System.out.println("Travel time of person " + event.getPersonId() + " is " + travelTime + " s.");
    }

    @Override
    public void handleEvent(PersonDepartureEvent event) {

        System.out.println("AgentDepartureEvent -- time: " + event.getTime() + " -- linkId: " + event.getLinkId() + " -- personId: " + event.getPersonId());
        departureTimeByPerson.put(event.getPersonId(), event.getTime());


    }
}