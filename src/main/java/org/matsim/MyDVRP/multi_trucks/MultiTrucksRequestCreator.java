/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2012 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.MyDVRP.multi_trucks;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.dvrp.optimizer.Request;
import org.matsim.contrib.dvrp.optimizer.VrpOptimizer;
import org.matsim.contrib.dvrp.run.DvrpMode;
import org.matsim.core.mobsim.framework.events.MobsimAfterSimStepEvent;
import org.matsim.core.mobsim.framework.listeners.MobsimAfterSimStepListener;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * @author michalm
 */
public final class MultiTrucksRequestCreator implements MobsimAfterSimStepListener {
    private final VrpOptimizer optimizer;
    private final PriorityQueue<MultiTrucksRequest> requests = new PriorityQueue<>(20,
            Comparator.comparing(Request::getSubmissionTime));

    @Inject
    public MultiTrucksRequestCreator(@DvrpMode(TransportMode.truck) VrpOptimizer optimizer,
                                     @DvrpMode(TransportMode.truck) Network network) {
        this.optimizer = optimizer;
        for (int i = 0; i < 100; i++) {
            var a = network.getLinks().values().toArray();
            Link fromlink = (Link) a[new Random().nextInt(network.getLinks().size())];
            Link tolink = (Link) a[new Random().nextInt(network.getLinks().size())];

            requests.add(createRequest("parcel_" + i,
                    fromlink.getId().toString(), tolink.getId().toString(),
					new Random().nextInt(2700), network));
        }


//		requests.addAll(Arrays.asList(
//				createRequest("parcel_0", "114", "349", 0, network),
//				createRequest("parcel_1", "144", "437", 100, network),
//				createRequest("parcel_2", "223", "347", 200, network),
//				createRequest("parcel_3", "234", "119", 200, network),
//				createRequest("parcel_4", "314", "260", 200, network),
//				createRequest("parcel_5", "333", "438", 250, network),
//				createRequest("parcel_6", "325", "111", 300, network),
//				createRequest("parcel_7", "412", "318", 300, network),
//				createRequest("parcel_8", "455", "236", 300, network),
//				createRequest("parcel_9", "139", "330", 600, network),
//				createRequest("parcel_10", "142", "349", 700, network),
//				createRequest("parcel_11", "144", "142", 780, network),
//				createRequest("parcel_12", "223", "214", 800, network),
//				createRequest("parcel_13", "263", "119", 800, network),
//				createRequest("parcel_14", "314", "324", 1100, network),
//				createRequest("parcel_15", "333", "350", 1100, network),
//				createRequest("parcel_16", "120", "111", 1200, network),
//				createRequest("parcel_17", "412", "318", 1200, network),
//				createRequest("parcel_18", "455", "458", 1200, network),
//				createRequest("parcel_19", "111", "430", 2700, network)
//
//				));
    }

    private MultiTrucksRequest createRequest(String requestId, String fromLinkId, String toLinkId, double time,
                                             Network network) {
        return new MultiTrucksRequest(Id.create(requestId, Request.class),
                network.getLinks().get(Id.createLinkId(fromLinkId)), network.getLinks().get(Id.createLinkId(toLinkId)),
                time);
    }

    @Override
    public void notifyMobsimAfterSimStep(@SuppressWarnings("rawtypes") MobsimAfterSimStepEvent e) {
        while (isReadyForSubmission(requests.peek(), e.getSimulationTime())) {
            optimizer.requestSubmitted(requests.poll());
        }
    }

    private boolean isReadyForSubmission(MultiTrucksRequest request, double currentTime) {
        return request != null && request.getSubmissionTime() <= currentTime;
    }
}
