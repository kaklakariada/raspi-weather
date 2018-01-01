package weather.service;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class DewPointServiceTest {

	private DewPointService service;

	@Before
	public void setUp() {
		service = new DewPointService();
	}

	@Test
	public void test() {
		// http://modernus.de/online-taupunkt-rechner/auswirkung-raumluft-temperatur-feuchtigkeit-taupunkt-berechnen
		assertDewPoint(20, 50, 9.25);
		assertDewPoint(25, 48, 13.22);
		assertDewPoint(0, 35, -13.67);
		assertDewPoint(30, 35, 12.86);
		assertDewPoint(30, 95, 29.1);
		assertDewPoint(0, 95, -0.7);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testHumidityLessThanZero() {
		service.calculateDewPoint(20, -0.1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testHumidityGreaterThan100() {
		service.calculateDewPoint(20, 100.1);
	}

	private void assertDewPoint(double temperature, double humidity, double expectedDewPoint) {
		assertEquals(expectedDewPoint, service.calculateDewPoint(temperature, humidity), 0.01);
	}
}
