/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
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

import com.google.inject.Inject;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.fleet.Fleet;
import org.matsim.contrib.dvrp.optimizer.Request;
import org.matsim.contrib.dvrp.optimizer.VrpOptimizer;
import org.matsim.contrib.dvrp.path.VrpPathWithTravelData;
import org.matsim.contrib.dvrp.path.VrpPaths;
import org.matsim.contrib.dvrp.router.TimeAsTravelDisutility;
import org.matsim.contrib.dvrp.run.DvrpMode;
import org.matsim.contrib.dvrp.schedule.*;
import org.matsim.contrib.dvrp.schedule.Schedule.ScheduleStatus;
import org.matsim.core.mobsim.framework.MobsimTimer;
import org.matsim.core.router.speedy.SpeedyDijkstraFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.trafficmonitoring.FreeSpeedTravelTime;

import java.util.List;
import java.util.Random;

/**
 * @author michalm
 */
final class MultiTrucksOptimizer implements VrpOptimizer {
    enum MultiTrucksTaskType implements Task.TaskType {
        EMPTY_DRIVE, LOADED_DRIVE, PICKUP, DELIVERY, WAIT
    }

    private final MobsimTimer timer;

    private final TravelTime travelTime;
    private final LeastCostPathCalculator router;


    Fleet thisfleet;
    private static final double PICKUP_DURATION = 120;
    private static final double DELIVERY_DURATION = 60;
    private static Boolean aBoolean = true;

    @Inject
    public MultiTrucksOptimizer(@DvrpMode(TransportMode.truck) Network network, @DvrpMode(TransportMode.truck) Fleet fleet,
                                MobsimTimer timer) {
        this.timer = timer;
        travelTime = new FreeSpeedTravelTime();
        router = new SpeedyDijkstraFactory().createPathCalculator(network, new TimeAsTravelDisutility(travelTime),
                travelTime);
        thisfleet = fleet;
        for (DvrpVehicle vehicle : thisfleet.getVehicles().values()) {
            vehicle.getSchedule()
                    .addTask(new DefaultStayTask(MultiTrucksTaskType.WAIT, vehicle.getServiceBeginTime(), vehicle.getServiceEndTime(),
                            vehicle.getStartLink()));
        }

    }

    @Override
    public void requestSubmitted(Request request) {

        double currentTime = timer.getTimeOfDay();


        DvrpVehicle vehicle = ((DvrpVehicle) (thisfleet.getVehicles().values().toArray()
                [new Random().nextInt(thisfleet.getVehicles().size())]));
        Schedule schedule = vehicle.getSchedule();
        StayTask lastTask = (StayTask) Schedules.getLastTask(schedule);// only WaitTask possible here

        switch (lastTask.getStatus()) {
            case PLANNED:
                schedule.removeLastTask();// remove wait task
                break;

            case STARTED:
                lastTask.setEndTime(currentTime);// shorten wait task
                break;

            case PERFORMED:
            default:
                throw new IllegalStateException();
        }
        //Boolean selectVehicle1=true;
        MultiTrucksRequest req = (MultiTrucksRequest) request;
        Link fromLink = req.getFromLink();
        Link toLink = req.getToLink();


        double t0 = schedule.getStatus() == ScheduleStatus.UNPLANNED ?
                Math.max(vehicle.getServiceBeginTime(), currentTime) :
                Schedules.getLastTask(schedule).getEndTime();

        VrpPathWithTravelData pathToCustomer = VrpPaths.calcAndCreatePath(lastTask.getLink(), fromLink, t0, router,
                travelTime);
        schedule.addTask(new DefaultDriveTask(MultiTrucksTaskType.EMPTY_DRIVE, pathToCustomer));

        double t1 = pathToCustomer.getArrivalTime();
        double t2 = t1 + PICKUP_DURATION;// 2 minutes for the pickup
        schedule.addTask(new MultiTrucksServeTask(MultiTrucksTaskType.PICKUP, t1, t2, fromLink, req));

        VrpPathWithTravelData pathWithCustomer = VrpPaths.calcAndCreatePath(fromLink, toLink, t2, router, travelTime);
        schedule.addTask(new DefaultDriveTask(MultiTrucksTaskType.LOADED_DRIVE, pathWithCustomer));

        double t3 = pathWithCustomer.getArrivalTime();
        double t4 = t3 + DELIVERY_DURATION;// 1 minute for the delivery
        schedule.addTask(new MultiTrucksServeTask(MultiTrucksTaskType.DELIVERY, t3, t4, toLink, req));

        // just wait (and be ready) till the end of the vehicle's time window (T1)
        double tEnd = Math.max(t4, vehicle.getServiceEndTime());
        schedule.addTask(new DefaultStayTask(MultiTrucksTaskType.WAIT, t4, tEnd, toLink));


    }

    @Override
    public void nextTask(DvrpVehicle vehicle) {
        updateTimings(vehicle);
        vehicle.getSchedule().nextTask();
    }

    /**
     * Simplified version. For something more advanced, see
     * {@link org.matsim.contrib.taxi.scheduler.TaxiScheduler#updateBeforeNextTask(DvrpVehicle)} in the taxi contrib
     */
    private void updateTimings(DvrpVehicle vehicle1) {
        Schedule schedule = vehicle1.getSchedule();
        if (schedule.getStatus() != ScheduleStatus.STARTED) {
            return;
        }

        double now = timer.getTimeOfDay();
        Task currentTask = schedule.getCurrentTask();
        double diff = now - currentTask.getEndTime();

        if (diff == 0) {
            return;
        }

        currentTask.setEndTime(now);

        List<? extends Task> tasks = schedule.getTasks();
        int nextTaskIdx = currentTask.getTaskIdx() + 1;

        // all except the last task (waiting)
        for (int i = nextTaskIdx; i < tasks.size() - 1; i++) {
            Task task = tasks.get(i);
            task.setBeginTime(task.getBeginTime() + diff);
            task.setEndTime(task.getEndTime() + diff);
        }

        // wait task
        if (nextTaskIdx != tasks.size()) {
            Task waitTask = tasks.get(tasks.size() - 1);
            waitTask.setBeginTime(waitTask.getBeginTime() + diff);

            double tEnd = Math.max(waitTask.getBeginTime(), vehicle1.getServiceEndTime());
            waitTask.setEndTime(tEnd);
        }
    }
}