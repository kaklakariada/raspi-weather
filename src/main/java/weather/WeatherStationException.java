package weather;

public class WeatherStationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public WeatherStationException(String message, Throwable cause) {
		super(message, cause);
	}

	public WeatherStationException(String message) {
		super(message);
	}
}
