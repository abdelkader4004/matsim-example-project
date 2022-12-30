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

package org.matsim.MyDVRP.two_trucks;

import com.google.inject.Inject;
import org.matsim.api.core.v01.Id;
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

/**
 * @author michalm
 */
final class TwoTrucksOptimizer implements VrpOptimizer {
	enum TwoTrucksTaskType implements Task.TaskType {
		EMPTY_DRIVE, LOADED_DRIVE, PICKUP, DELIVERY, WAIT
	}

	private final MobsimTimer timer;

	private final TravelTime travelTime;
	private final LeastCostPathCalculator router;

	private final DvrpVehicle vehicle1;// we have only one vehicle
	private final DvrpVehicle vehicle2;// we have only one vehicle
	private static final double PICKUP_DURATION = 120;
	private static final double DELIVERY_DURATION = 60;
	private static  Boolean aBoolean = true;

	@Inject
	public TwoTrucksOptimizer(@DvrpMode(TransportMode.truck) Network network, @DvrpMode(TransportMode.truck) Fleet fleet,
							  MobsimTimer timer) {
		this.timer = timer;
		travelTime = new FreeSpeedTravelTime();
		router = new SpeedyDijkstraFactory().createPathCalculator(network, new TimeAsTravelDisutility(travelTime),
				travelTime);
		vehicle1=fleet.getVehicles().get(Id.createVehicleId("truck_one"));
		vehicle2=fleet.getVehicles().get(Id.createVehicleId("truck_two"));

//	vehicle1 = fleet.getVehicles().values().iterator().next();
//    vehicle2 = fleet.getVehicles().values().iterator().next();
		vehicle1.getSchedule()
				.addTask(new DefaultStayTask(TwoTrucksTaskType.WAIT, vehicle1.getServiceBeginTime(), vehicle1.getServiceEndTime(),
						vehicle1.getStartLink()));
		vehicle2.getSchedule()
				.addTask(new DefaultStayTask(TwoTrucksTaskType.WAIT, vehicle2.getServiceBeginTime(), vehicle2.getServiceEndTime(),
						vehicle2.getStartLink()));
	}

	@Override
	public void requestSubmitted(Request request) {

		double currentTime = timer.getTimeOfDay();
		aBoolean=!aBoolean;
if (aBoolean){
	Schedule schedule1 = vehicle1.getSchedule();
	StayTask lastTask1 = (StayTask)Schedules.getLastTask(schedule1);// only WaitTask possible here

		switch (lastTask1.getStatus()) {
			case PLANNED:
				schedule1.removeLastTask();// remove wait task
				break;

			case STARTED:
				lastTask1.setEndTime(currentTime);// shorten wait task
				break;

			case PERFORMED:
			default:
				throw new IllegalStateException();
		}
	//Boolean selectVehicle1=true;
	TwoTrucksRequest req = (TwoTrucksRequest)request;
	Link fromLink = req.getFromLink();
	Link toLink = req.getToLink();



	double t0 = schedule1.getStatus() == ScheduleStatus.UNPLANNED ?
			Math.max(vehicle1.getServiceBeginTime(), currentTime) :
			Schedules.getLastTask(schedule1).getEndTime();

	VrpPathWithTravelData pathToCustomer = VrpPaths.calcAndCreatePath(lastTask1.getLink(), fromLink, t0, router,
			travelTime);
	schedule1.addTask(new DefaultDriveTask(TwoTrucksTaskType.EMPTY_DRIVE, pathToCustomer));

	double t1 = pathToCustomer.getArrivalTime();
	double t2 = t1 + PICKUP_DURATION;// 2 minutes for the pickup
	schedule1.addTask(new TwoTrucksServeTask(TwoTrucksTaskType.PICKUP, t1, t2, fromLink, req));

	VrpPathWithTravelData pathWithCustomer = VrpPaths.calcAndCreatePath(fromLink, toLink, t2, router, travelTime);
	schedule1.addTask(new DefaultDriveTask(TwoTrucksTaskType.LOADED_DRIVE, pathWithCustomer));

	double t3 = pathWithCustomer.getArrivalTime();
	double t4 = t3 + DELIVERY_DURATION;// 1 minute for the delivery
	schedule1.addTask(new TwoTrucksServeTask(TwoTrucksTaskType.DELIVERY, t3, t4, toLink, req));

	// just wait (and be ready) till the end of the vehicle's time window (T1)
	double tEnd = Math.max(t4, vehicle1.getServiceEndTime());
	schedule1.addTask(new DefaultStayTask(TwoTrucksTaskType.WAIT, t4, tEnd, toLink));


}else{
	Schedule schedule2 = vehicle1.getSchedule();
	StayTask lastTask2 = (StayTask)Schedules.getLastTask(schedule2);//


		switch (lastTask2.getStatus()) {
			case PLANNED:
				schedule2.removeLastTask();// remove wait task
				break;

			case STARTED:
				lastTask2.setEndTime(currentTime);// shorten wait task
				break;

			case PERFORMED:
			default:
				throw new IllegalStateException();
		}
	//Boolean selectVehicle1=true;
	TwoTrucksRequest req = (TwoTrucksRequest)request;
	Link fromLink = req.getFromLink();
	Link toLink = req.getToLink();


	double t0 = schedule2.getStatus() == ScheduleStatus.UNPLANNED ?
			Math.max(vehicle2.getServiceBeginTime(), currentTime) :
			Schedules.getLastTask(schedule2).getEndTime();

	VrpPathWithTravelData pathToCustomer = VrpPaths.calcAndCreatePath(lastTask2.getLink(), fromLink, t0, router,
			travelTime);
	schedule2.addTask(new DefaultDriveTask(TwoTrucksTaskType.EMPTY_DRIVE, pathToCustomer));

	double t1 = pathToCustomer.getArrivalTime();
	double t2 = t1 + PICKUP_DURATION;// 2 minutes for the pickup
	schedule2.addTask(new TwoTrucksServeTask(TwoTrucksTaskType.PICKUP, t1, t2, fromLink, req));

	VrpPathWithTravelData pathWithCustomer = VrpPaths.calcAndCreatePath(fromLink, toLink, t2, router, travelTime);
	schedule2.addTask(new DefaultDriveTask(TwoTrucksTaskType.LOADED_DRIVE, pathWithCustomer));

	double t3 = pathWithCustomer.getArrivalTime();
	double t4 = t3 + DELIVERY_DURATION;// 1 minute for the delivery
	schedule2.addTask(new TwoTrucksServeTask(TwoTrucksTaskType.DELIVERY, t3, t4, toLink, req));

	// just wait (and be ready) till the end of the vehicle's time window (T1)
	double tEnd = Math.max(t4, vehicle2.getServiceEndTime());
	schedule2.addTask(new DefaultStayTask(TwoTrucksTaskType.WAIT, t4, tEnd, toLink));

}

//		double tZero=-1;
//		if(schedule1.getStatus() == ScheduleStatus.UNPLANNED){
//			tZero=Math.max(vehicle1.getServiceBeginTime(), currentTime);
//		}
//		else if(schedule2.getStatus() == ScheduleStatus.UNPLANNED){
//			tZero=Math.max(vehicle2.getServiceBeginTime(), currentTime);
//			selectVehicle1=false;
//		}
//		else{
//			tZero=Math.min(Schedules.getLastTask(schedule1).getEndTime(),Schedules.getLastTask(schedule2).getEndTime());
//			selectVehicle1=Schedules.getLastTask(schedule1).getEndTime()<Schedules.getLastTask(schedule2).getEndTime();
//		}

//		double t0 = schedule1.getStatus() == ScheduleStatus.UNPLANNED ?
//				Math.max(vehicle1.getServiceBeginTime(), currentTime) :
//				Schedules.getLastTask(schedule1).getEndTime();







//		if(selectVehicle1){
//
//		VrpPathWithTravelData pathToCustomer = VrpPaths.calcAndCreatePath(lastTask1.getLink(), fromLink, tZero, router,
//				travelTime);
//		schedule1.addTask(new DefaultDriveTask(TwoTrucksTaskType.EMPTY_DRIVE, pathToCustomer));
//
//		double t1 = pathToCustomer.getArrivalTime();
//		double t2 = t1 + PICKUP_DURATION;// 2 minutes for the pickup
//		schedule1.addTask(new TwoTrucksServeTask(TwoTrucksTaskType.PICKUP, t1, t2, fromLink, req));
//
//		VrpPathWithTravelData pathWithCustomer = VrpPaths.calcAndCreatePath(fromLink, toLink, t2, router, travelTime);
//		schedule1.addTask(new DefaultDriveTask(TwoTrucksTaskType.LOADED_DRIVE, pathWithCustomer));
//
//		double t3 = pathWithCustomer.getArrivalTime();
//		double t4 = t3 + DELIVERY_DURATION;// 1 minute for the delivery
//		schedule1.addTask(new TwoTrucksServeTask(TwoTrucksTaskType.DELIVERY, t3, t4, toLink, req));
//
//		// just wait (and be ready) till the end of the vehicle's time window (T1)
//		double tEnd = Math.max(t4, vehicle1.getServiceEndTime());
//		schedule1.addTask(new DefaultStayTask(TwoTrucksTaskType.WAIT, t4, tEnd, toLink));
//		}
//		else{
//			VrpPathWithTravelData pathToCustomer = VrpPaths.calcAndCreatePath(lastTask2.getLink(), fromLink, tZero, router,
//					travelTime);
//			schedule2.addTask(new DefaultDriveTask(TwoTrucksTaskType.EMPTY_DRIVE, pathToCustomer));
//
//			double t1 = pathToCustomer.getArrivalTime();
//			double t2 = t1 + PICKUP_DURATION;// 2 minutes for the pickup
//			schedule2.addTask(new TwoTrucksServeTask(TwoTrucksTaskType.PICKUP, t1, t2, fromLink, req));
//
//			VrpPathWithTravelData pathWithCustomer = VrpPaths.calcAndCreatePath(fromLink, toLink, t2, router, travelTime);
//			schedule2.addTask(new DefaultDriveTask(TwoTrucksTaskType.LOADED_DRIVE, pathWithCustomer));
//
//			double t3 = pathWithCustomer.getArrivalTime();
//			double t4 = t3 + DELIVERY_DURATION;// 1 minute for the delivery
//			schedule2.addTask(new TwoTrucksServeTask(TwoTrucksTaskType.DELIVERY, t3, t4, toLink, req));
//
//			// just wait (and be ready) till the end of the vehicle's time window (T1)
//			double tEnd = Math.max(t4, vehicle2.getServiceEndTime());
//			schedule2.addTask(new DefaultStayTask(TwoTrucksTaskType.WAIT, t4, tEnd, toLink));
//
//		}
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
