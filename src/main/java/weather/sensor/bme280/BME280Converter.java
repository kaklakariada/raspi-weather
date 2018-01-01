package weather.sensor.bme280;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.influxdb.dto.Point;

import weather.service.DewPointService;

public class BME280Converter implements Function<BME280Measurment, Collection<Point>> {

	private final DewPointService dewPointService = new DewPointService();

	@Override
	public Collection<Point> apply(BME280Measurment measurment) {
		final double dewPoint = dewPointService.calculateDewPoint(measurment.getTemp(), measurment.getHumidity());
		return Collections.singleton(Point.measurement("weather")
				.time(measurment.getTimestamp().toEpochMilli(), TimeUnit.MILLISECONDS)
				.addField("temp", measurment.getTemp())
				.addField("humidity", measurment.getHumidity())
				.addField("pressure", measurment.getPressure())
				.addField("dew_point", dewPoint)
				.build());
	}
}
