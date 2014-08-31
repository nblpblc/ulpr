package hellocalculator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class HelloCalculatorTest {

	@Test
	public void testSum() {
	assertThat(HelloCalculator.sum(new String[] {"12", "23", "56"}), equalTo(91));
	}
}
