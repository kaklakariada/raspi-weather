package weather;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import weather.sensor.BME280;
import weather.sensor.BME280Measurment;
import weather.upload.InfluxService;

public class WeatherStation {

	private static final Logger LOG = LoggerFactory.getLogger(WeatherStation.class);

	public static void main(String[] args) throws UnsupportedBusNumberException, IOException, InterruptedException {

		final BME280 bme280 = BME280.create();
		final InfluxService influxService = InfluxService.create();

		final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(2);

		final Runnable publisher = MeasurmentPublisher.influxDbPublisher(bme280::read,
				WeatherStation::convertBME280Measurment, influxService, "weather");

		scheduledThreadPool.scheduleAtFixedRate(publisher, 0, 3, TimeUnit.SECONDS);
	}

	private static Point convertBME280Measurment(final BME280Measurment measurment) {
		final long timestamp = System.currentTimeMillis();
		final Point point = Point.measurement("weather")
				.time(timestamp, TimeUnit.MILLISECONDS)
				.addField("temp", measurment.getTemp())
				.addField("humidity", measurment.getHumidity())
				.addField("pressure", measurment.getPressure())
				.build();
		return point;
	}
}
