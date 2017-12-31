package weather;

import java.io.IOException;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import weather.sensor.BME280;
import weather.sensor.BME280Measurment;

public class WeatherStation {
	public static void main(String[] args) throws UnsupportedBusNumberException, IOException {

		final BME280 bme280 = BME280.create();
		final BME280Measurment measurment = bme280.read();
		System.out.println(measurment);
	}
}
