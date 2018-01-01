package weather.sensor.fritzdect;

import java.time.Clock;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kaklakariada.fritzbox.HomeAutomation;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class FritzDectService {
	private static final Logger LOG = LoggerFactory.getLogger(FritzDectService.class);
	private static final int READ_TIMEOUT_MILLIS = 5 * 1000;
	private final HomeAutomation homeAutomation;
	private final Clock clock;

	FritzDectService(HomeAutomation homeAutomation, Clock clock) {
		this.homeAutomation = homeAutomation;
		this.clock = clock;
	}

	public static FritzDectService create(String url, String user, String password) {
		LOG.info("Logging in to '{}' with username '{}'", url, user);
		final HomeAutomation homeAutomation = HomeAutomation.connect(url, user, password);
		return new FritzDectService(homeAutomation, Clock.systemUTC());
	}

	public FritzDectMeasurment getMeasurment() {
		return new GetFritzDectMeasurmentCommand(homeAutomation, clock).execute();
	}

	private static class GetFritzDectMeasurmentCommand extends HystrixCommand<FritzDectMeasurment> {

		private final HomeAutomation homeAutomation;
		private final Clock clock;

		private GetFritzDectMeasurmentCommand(HomeAutomation homeAutomation, Clock clock) {
			super(HystrixCommandGroupKey.Factory.asKey("fritzbox"), READ_TIMEOUT_MILLIS);
			this.homeAutomation = homeAutomation;
			this.clock = clock;
		}

		@Override
		protected FritzDectMeasurment run() throws Exception {
			final Instant timestamp = clock.instant();
			return new FritzDectMeasurment(timestamp, homeAutomation.getDeviceListInfos());
		}

		@Override
		protected FritzDectMeasurment getFallback() {
			return null;
		}
	}
}
