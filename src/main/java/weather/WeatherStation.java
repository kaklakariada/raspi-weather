package weather;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import weather.sensor.BME280;
import weather.sensor.BME280Converter;
import weather.upload.InfluxService;

public class WeatherStation {

	private static final Logger LOG = LoggerFactory.getLogger(WeatherStation.class);

	public static void main(String[] args) throws UnsupportedBusNumberException, IOException, InterruptedException {

		final WeatherStationConfig config = WeatherStationConfig.read();
		final BME280 bme280 = BME280.create(config.getBME280I2CBusNumber(), config.getBME280I2CDeviceAddress());
		final InfluxService influxService = InfluxService.create(config.getInfluxDbUrl(), config.getInfluxDbUser(),
				config.getInfluxDbPassword());

		final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(2);

		final Runnable bme280Publisher = MeasurmentPublisher.influxDbPublisher(bme280::read, new BME280Converter(),
				influxService, "weather");

		scheduledThreadPool.scheduleAtFixedRate(bme280Publisher, 0, 3, TimeUnit.SECONDS);
	}
}
