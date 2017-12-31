package weather;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.influxdb.dto.Point;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import weather.sensor.BME280;
import weather.sensor.BME280Measurment;
import weather.upload.InfluxService;

public class WeatherStation {
	public static void main(String[] args) throws UnsupportedBusNumberException, IOException, InterruptedException {

		final BME280 bme280 = BME280.create();
		final InfluxService influxService = InfluxService.create();
		influxService.createDatabase();

		while (true) {
			final long timestamp = System.currentTimeMillis();
			final BME280Measurment measurment = bme280.read();

			final Point point = Point.measurement("weather")
					.time(timestamp, TimeUnit.MILLISECONDS)
					.addField("temp", measurment.getTemp())
					.addField("humidity", measurment.getHumidity())
					.addField("pressure", measurment.getPressure())
					.build();

			influxService.write(point);
			System.out.println(measurment);
			Thread.sleep(3 * 1000);
		}
	}
}
