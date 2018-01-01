package weather;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weather.upload.InfluxService;

public class MeasurmentPublisher<I, O> implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(MeasurmentPublisher.class);

	private final Supplier<I> supplier;
	private final Function<I, O> converter;
	private final Consumer<O> consumer;

	public MeasurmentPublisher(Supplier<I> supplier, Function<I, O> converter, Consumer<O> consumer) {
		this.supplier = supplier;
		this.converter = converter;
		this.consumer = consumer;
	}

	public static <I> Runnable influxDbPublisher(Supplier<I> supplier, Function<I, Collection<Point>> converter,
			InfluxService influx, String database) {
		return new MeasurmentPublisher<>(supplier, converter, point -> influx.write(database, point));
	}

	@Override
	public void run() {
		final I value = supplier.get();
		if (value == null) {
			LOG.warn("Got null from supplier {}: ignore", supplier);
			return;
		}
		final O convertedValue = converter.apply(value);
		LOG.debug("Publishing {}", value);
		consumer.accept(convertedValue);
	}
}
