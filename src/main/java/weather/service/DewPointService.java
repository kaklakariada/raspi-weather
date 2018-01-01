package weather.service;

public class DewPointService {

	// https://de.wikipedia.org/wiki/Taupunkt#Berechnung_des_Taupunkts_von_feuchter_Luft
	final static double K1 = 6.112;
	final static double K2 = 17.62;
	final static double K3 = 243.12;

	public double calculateDewPoint(double temp, double humidityPercent) {
		if (humidityPercent < 0) {
			throw new IllegalArgumentException("Humidity " + humidityPercent + " < 0");
		}
		if (humidityPercent > 100) {
			throw new IllegalArgumentException("Humidity " + humidityPercent + " > 100");
		}
		final double humidity = humidityPercent / 100;
		final double factor1 = K2 * temp / (K3 + temp) + Math.log(humidity);
		final double factor2 = K2 * K3 / (K3 + temp) - Math.log(humidity);
		return K3 * factor1 / factor2;
	}
}
