package weather.sensor.bme280;

import java.text.NumberFormat;
import java.time.Instant;

public class BME280Measurment {
	private final Instant timestamp;
	private final double temp;
	private final double humidity;
	private final double pressure;

	public BME280Measurment(Instant timestamp, double temp, double humidity, double pressure) {
		this.timestamp = timestamp;
		this.temp = temp;
		this.humidity = humidity;
		this.pressure = pressure;
	}

	public double getTemp() {
		return temp;
	}

	public double getHumidity() {
		return humidity;
	}

	public double getPressure() {
		return pressure;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		final NumberFormat numberFormat = NumberFormat.getNumberInstance();
		return "temp: " + numberFormat.format(temp) + "°C, humidity: " + numberFormat.format(humidity)
				+ "% RH, pressure: " + numberFormat.format(pressure) + "hPa";
	}

}
