package weather.upload;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
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

	public void write(String database, Point point) {
		new WriteDbCommand(influxDB, database, point)
				.observe()
				.subscribe(
						result -> LOG.trace("Successfully wrote {} to db {}: {}", point, database, result),
						exception -> LOG.warn("Writing to db '" + database + "' failed: " + exception.getMessage(),
								exception));
	}

	private static class WriteDbCommand extends HystrixCommand<Void> {

		private final InfluxDB influxDB;
		private final String database;
		private final Point point;

		private WriteDbCommand(InfluxDB influxDB, String database, Point point) {
			super(HystrixCommandGroupKey.Factory.asKey("influxdb"), DB_WRITE_TIMEOUT_MILLIS);
			this.influxDB = influxDB;
			this.database = database;
			this.point = point;
		}

		@Override
		protected Void run() throws Exception {
			LOG.trace("Writing point to db {}...", database);
			influxDB.write(database, DEFAULT_RETENTION_POLICY, point);
			LOG.trace("Wrote point to db {} successfully", database);
			return null;
		}
	}
}
