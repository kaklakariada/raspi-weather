package weather;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import weather.sensor.bme280.BME280;
import weather.sensor.bme280.BME280Converter;
import weather.sensor.fritzdect.FritzDectConverter;
import weather.sensor.fritzdect.FritzDectService;
import weather.upload.InfluxService;

public class WeatherStation {

	public static void main(String[] args) throws UnsupportedBusNumberException, IOException, InterruptedException {
		final WeatherStationConfig config = WeatherStationConfig.read();
		final BME280 bme280 = BME280.create(config.getBME280I2CBusNumber(), config.getBME280I2CDeviceAddress());
		final FritzDectService fritzDectService = FritzDectService.create(config.getFritzBoxUrl(),
				config.getFritzBoxUser(), config.getFritzBoxPassword());
		final InfluxService influxService = InfluxService.create(config.getInfluxDbUrl(), config.getInfluxDbUser(),
				config.getInfluxDbPassword());

		final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(2);

		final Runnable bme280Publisher = MeasurmentPublisher.influxDbPublisher(bme280::read, new BME280Converter(),
				influxService, "weather");
		final Runnable fritzDectPublisher = MeasurmentPublisher.influxDbPublisher(fritzDectService::getMeasurment,
				new FritzDectConverter(), influxService, "fritzdect");

		scheduledThreadPool.scheduleAtFixedRate(bme280Publisher, 0, 3, TimeUnit.SECONDS);
		scheduledThreadPool.scheduleAtFixedRate(fritzDectPublisher, 2, 5, TimeUnit.SECONDS);
	}
}
