package weather.sensor.fritzdect;

import java.time.Clock;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kaklakariada.fritzbox.HomeAutomation;

public class FritzDectService {
	private static final Logger LOG = LoggerFactory.getLogger(FritzDectService.class);
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
		final Instant timestamp = clock.instant();
		return new FritzDectMeasurment(timestamp, homeAutomation.getDeviceListInfos());
	}
}
