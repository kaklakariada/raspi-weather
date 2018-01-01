package weather.upload;

import java.util.Collection;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class InfluxService {
	private static final Logger LOG = LoggerFactory.getLogger(InfluxService.class);

	private static final String DEFAULT_RETENTION_POLICY = "autogen";
	private static final int DB_WRITE_TIMEOUT_MILLIS = 5 * 1000;
	private final InfluxDB influxDB;

	InfluxService(InfluxDB influxDB) {
		this.influxDB = influxDB;
	}

	public static InfluxService create(String url, String username, String password) {
		LOG.info("Connecting to '{}' with user '{}'", url, username);
		final InfluxDB influxDB = createDbConnection(url, username, password)
				.enableGzip()
				.setConsistency(ConsistencyLevel.QUORUM)
				.setDatabase(null);
		return new InfluxService(influxDB);
	}

	private static InfluxDB createDbConnection(String url, String username, String password) {
		if (username == null || username.isEmpty()) {
			return InfluxDBFactory.connect(url);
		}
		return InfluxDBFactory.connect(url, username, password);
	}

	public void write(String database, Collection<Point> points) {
		new WriteDbCommand(influxDB, database, points)
				.observe()
				.subscribe(
						result -> LOG.trace("Successfully wrote {} points to db {}: {}", points.size(), database,
								result),
						exception -> LOG.warn("Writing to db '" + database + "' failed: " + exception.getMessage(),
								exception));
	}

	private static class WriteDbCommand extends HystrixCommand<Void> {

		private final InfluxDB influxDB;
		private final String database;
		private final Collection<Point> points;

		private WriteDbCommand(InfluxDB influxDB, String database, Collection<Point> points) {
			super(HystrixCommandGroupKey.Factory.asKey("influxdb"), DB_WRITE_TIMEOUT_MILLIS);
			this.influxDB = influxDB;
			this.database = database;
			this.points = points;
		}

		@Override
		protected Void run() throws Exception {
			LOG.trace("Writing {} points to db {}...", points.size(), database);
			final BatchPoints batch = BatchPoints.database(database).retentionPolicy(DEFAULT_RETENTION_POLICY).build();
			points.forEach(batch::point);
			influxDB.write(batch);
			LOG.trace("Wrote {} points to db {} successfully", batch.getPoints().size(), database);
			return null;
		}
	}
}
