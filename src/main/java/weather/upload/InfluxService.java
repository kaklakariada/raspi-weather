package weather.upload;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

public class InfluxService {

	private static final String DATABASE_NAME = "weather";
	private static final String RETENTIONPOLICY_NAME = "weatherRetentionPolicy";
	private final InfluxDB influxDB;

	InfluxService(InfluxDB influxDB) {
		this.influxDB = influxDB;
	}

	public static InfluxService create() {
		final InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086");
		influxDB.enableGzip();
		return new InfluxService(influxDB);
	}

	public void createDatabase() {
		influxDB.createDatabase(DATABASE_NAME);
		influxDB.createRetentionPolicy(RETENTIONPOLICY_NAME, DATABASE_NAME, "30d", "30m", 2, true);
	}

	public void write(Point... points) {
		final BatchPoints batchPoints = BatchPoints.database(DATABASE_NAME)
				.tag("async", "true")
				.retentionPolicy(RETENTIONPOLICY_NAME)
				.consistency(ConsistencyLevel.ALL)
				.points(points)
				.build();
		influxDB.write(batchPoints);
	}
}
