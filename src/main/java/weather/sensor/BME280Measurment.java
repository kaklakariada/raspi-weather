package weather.sensor;

import java.text.NumberFormat;

public class BME280Measurment {
	private final double temp;
	private final double humidity;
	private final double pressure;

	public BME280Measurment(double temp, double humidity, double pressure) {
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

	@Override
	public String toString() {
		final NumberFormat numberFormat = NumberFormat.getNumberInstance();
		return "temp: " + numberFormat.format(temp) + "°C, humidity: " + numberFormat.format(humidity)
				+ "% RH, pressure: " + numberFormat.format(pressure) + "hPa";
	}
}
