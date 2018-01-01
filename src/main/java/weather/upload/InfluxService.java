package weather.upload;

import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

public class InfluxService {
	private static final String DEFAULT_RETENTION_POLICY = "autogen";
	private final InfluxDB influxDB;

	InfluxService(InfluxDB influxDB) {
		this.influxDB = influxDB;
	}

	public static InfluxService create() {
		final InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086")
				.enableGzip()
				.setConsistency(ConsistencyLevel.QUORUM)
				.setDatabase(null)
				.enableBatch(10, 10, TimeUnit.SECONDS);
		return new InfluxService(influxDB);
	}

	public void write(String database, Point point) {
		influxDB.write(database, DEFAULT_RETENTION_POLICY, point);
	}
}
