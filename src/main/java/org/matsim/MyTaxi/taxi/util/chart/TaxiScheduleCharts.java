package org.matsim.MyTaxi.taxi.util.chart;

import org.jfree.chart.JFreeChart;
import org.matsim.MyTaxi.taxi.schedule.TaxiDropoffTask;
import org.matsim.MyTaxi.taxi.schedule.TaxiPickupTask;
import org.matsim.MyTaxi.taxi.schedule.TaxiTaskBaseType;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.util.chart.ScheduleCharts;
import org.matsim.contrib.dvrp.util.chart.ScheduleCharts.DescriptionCreator;
import org.matsim.contrib.dvrp.util.chart.ScheduleCharts.PaintSelector;

import java.awt.*;
import java.util.Collection;

import static org.matsim.MyTaxi.taxi.schedule.TaxiTaskBaseType.*;

public class TaxiScheduleCharts {
	public static JFreeChart chartSchedule(Collection<? extends DvrpVehicle> vehicles) {
		return ScheduleCharts.chartSchedule(vehicles, TAXI_DESCRIPTION_CREATOR, TAXI_PAINT_SELECTOR);
	}

	public static final DescriptionCreator TAXI_DESCRIPTION_CREATOR = task -> task.getTaskType() + "";

	public static final DescriptionCreator TAXI_DESCRIPTION_WITH_PASSENGER_ID_CREATOR = task -> {
		TaxiTaskBaseType baseType = getBaseTypeOrElseThrow(task);
		if (baseType == PICKUP) {
			return task.getTaskType() + "_" + ((TaxiPickupTask)task).getRequest().getPassengerId();
		}
		if (baseType == DROPOFF) {
			return task.getTaskType() + "_" + ((TaxiDropoffTask)task).getRequest().getPassengerId();
		}
		return task.getTaskType() + "";
	};

	private static final Color OCCUPIED_DRIVE_COLOR = new Color(200, 0, 0);
	private static final Color PICKUP_DROPOFF_COLOR = new Color(0, 0, 200);

	private static final Color EMPTY_DRIVE_COLOR = new Color(100, 0, 0);
	private static final Color STAY_COLOR = new Color(0, 0, 100);

	public static final PaintSelector TAXI_PAINT_SELECTOR = task -> {
		switch (getBaseTypeOrElseThrow(task)) {
			case PICKUP:
			case DROPOFF:
				return PICKUP_DROPOFF_COLOR;

			case OCCUPIED_DRIVE:
				return OCCUPIED_DRIVE_COLOR;

			case EMPTY_DRIVE:
				return EMPTY_DRIVE_COLOR;

			case STAY:
				return STAY_COLOR;

			default:
				throw new IllegalStateException();
		}
	};
}
