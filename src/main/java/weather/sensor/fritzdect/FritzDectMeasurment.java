package weather.sensor.fritzdect;

import java.time.Instant;

import com.github.kaklakariada.fritzbox.model.homeautomation.DeviceList;

public class FritzDectMeasurment {
	private final Instant timestamp;
	private final DeviceList deviceList;

	public FritzDectMeasurment(Instant timestamp, DeviceList deviceList) {
		this.timestamp = timestamp;
		this.deviceList = deviceList;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public DeviceList getDeviceList() {
		return deviceList;
	}

	@Override
	public String toString() {
		return "FritzDectMeasurment [timestamp=" + timestamp + ", deviceList=" + deviceList + "]";
	}
}
