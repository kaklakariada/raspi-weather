package weather;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherStationConfig {
	private static final Logger LOG = LoggerFactory.getLogger(WeatherStationConfig.class);

	private final Properties properties;

	private WeatherStationConfig(Properties properties) {
		this.properties = properties;
	}

	public static WeatherStationConfig read() {
		return read(Paths.get("weather.properties"));
	}

	private static WeatherStationConfig read(Path path) {
		return new WeatherStationConfig(readConfig(path));
	}

	public String getInfluxDbUrl() {
		return getValue("influxdb.url");
	}

	public String getInfluxDbUser() {
		return getOptionalValue("influxdb.user");
	}

	public String getInfluxDbPassword() {
		return getOptionalValue("influxdb.password");
	}

	public String getFritzBoxUrl() {
		return getValue("fritzbox.url");
	}

	public String getFritzBoxUser() {
		return getValue("fritzbox.user");
	}

	public String getFritzBoxPassword() {
		return getValue("fritzbox.password");
	}

	public int getBME280I2CBusNumber() {
		return getIntValue("bme280.bus");
	}

	public int getBME280I2CDeviceAddress() {
		return getIntValue("bme280.device");
	}

	private int getIntValue(String propertyName) {
		return Integer.parseInt(getValue(propertyName));
	}

	private String getValue(String propertyName) {
		final String value = getOptionalValue(propertyName);
		if (value == null) {
			throw new WeatherStationException("Property '" + propertyName + "' not found");
		}
		return value;
	}

	private String getOptionalValue(String propertyName) {
		return properties.getProperty(propertyName, null);
	}

	private static Properties readConfig(Path path) {
		final Properties config = new Properties();
		final Path absolutePath = path.toAbsolutePath();
		LOG.debug("Reading config from file {}", absolutePath);
		try (InputStream in = Files.newInputStream(absolutePath)) {
			config.load(in);
		} catch (final IOException e) {
			throw new WeatherStationException("Error loading configuration from " + absolutePath, e);
		}
		return config;
	}

}
