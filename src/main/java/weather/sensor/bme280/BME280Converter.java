package weather.sensor.bme280;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.influxdb.dto.Point;

public class BME280Converter implements Function<BME280Measurment, Point> {

	@Override
	public Point apply(BME280Measurment measurment) {
		final Point point = Point.measurement("weather")
				.time(measurment.getTimestamp().toEpochMilli(), TimeUnit.MILLISECONDS)
				.addField("temp", measurment.getTemp())
				.addField("humidity", measurment.getHumidity())
				.addField("pressure", measurment.getPressure())
				.build();
		return point;
	}
}
