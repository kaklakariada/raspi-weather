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
	private final InfluxDB influxDB;

	InfluxService(InfluxDB influxDB) {
		this.influxDB = influxDB;
	}

	public static InfluxService create() {
		final InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086")
				.enableGzip()
				.setConsistency(ConsistencyLevel.QUORUM)
				.setDatabase(null);
		return new InfluxService(influxDB);
	}

	public void write(String database, Point point) {
		new WriteDbCommand(influxDB, database, point).queue();
	}

	private static class WriteDbCommand extends HystrixCommand<Void> {

		private final InfluxDB influxDB;
		private final String database;
		private final Point point;

		private WriteDbCommand(InfluxDB influxDB, String database, Point point) {
			super(HystrixCommandGroupKey.Factory.asKey("influxdb"));
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

		@Override
		protected Void getFallback() {
			LOG.warn("Writing to db {} failed for point {}: ignore", database, point);
			return null;
		}
	}
}
