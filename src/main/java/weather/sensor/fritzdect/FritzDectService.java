package weather.sensor.fritzdect;

import java.time.Clock;
import java.time.Instant;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kaklakariada.fritzbox.HomeAutomation;
import com.github.kaklakariada.fritzbox.http.AccessForbiddenException;
import com.github.kaklakariada.fritzbox.model.homeautomation.DeviceList;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class FritzDectService {
	private static final Logger LOG = LoggerFactory.getLogger(FritzDectService.class);
	private static final int READ_TIMEOUT_MILLIS = 5 * 1000;
	private HomeAutomation homeAutomation;
	private final Supplier<HomeAutomation> homeAutomationSupplier;
	private final Clock clock;

	FritzDectService(Supplier<HomeAutomation> homeAutomationSupplier, Clock clock) {
		this.homeAutomationSupplier = homeAutomationSupplier;
		this.homeAutomation = homeAutomationSupplier.get();
		this.clock = clock;
	}

	public static FritzDectService create(String url, String user, String password) {
		LOG.info("Logging in to '{}' with username '{}'", url, user);
		return new FritzDectService(() -> HomeAutomation.connect(url, user, password), Clock.systemUTC());
	}

	public FritzDectMeasurment getMeasurment() {
		return new GetFritzDectMeasurmentCommand().execute();
	}

	private class GetFritzDectMeasurmentCommand extends HystrixCommand<FritzDectMeasurment> {

		private GetFritzDectMeasurmentCommand() {
			super(HystrixCommandGroupKey.Factory.asKey("fritzbox"), READ_TIMEOUT_MILLIS);
		}

		@Override
		protected FritzDectMeasurment run() {
			try {
				return getMeasurment();
			} catch (final AccessForbiddenException e) {
				LOG.warn("Token is invalid, reconnect", e);
				homeAutomation = homeAutomationSupplier.get();
				return getMeasurment();
			}
		}

		private FritzDectMeasurment getMeasurment() {
			final Instant timestamp = clock.instant();
			final DeviceList deviceListInfo = homeAutomation.getDeviceListInfos();
			return new FritzDectMeasurment(timestamp, deviceListInfo);
		}

		@Override
		protected FritzDectMeasurment getFallback() {
			return null;
		}
	}
}
