package weather.sensor.fritzdect;

import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.influxdb.dto.Point;

import com.github.kaklakariada.fritzbox.model.homeautomation.Device;

public class FritzDectConverter implements Function<FritzDectMeasurment, Collection<Point>> {

	@Override
	public Collection<Point> apply(FritzDectMeasurment measurment) {
		return measurment.getDeviceList().getDevices().stream()
				.map(device -> createPoint(measurment.getTimestamp(), device))
				.collect(toList());
	}

	private Point createPoint(Instant timestamp, Device device) {
		return Point.measurement(device.getName())
				.time(timestamp.toEpochMilli(), TimeUnit.MILLISECONDS)
				.addField("deviceLocked", device.getSwitchState().isDeviceLocked())
				.addField("locked", device.getSwitchState().isLocked())
				.addField("on", device.getSwitchState().isOn())
				.addField("energyWattHours", device.getPowerMeter().getEnergyWattHours())
				.addField("powerWatt", device.getPowerMeter().getPowerWatt())
				.addField("temp", device.getTemperature().getCelsius())
				.build();
	}
}
