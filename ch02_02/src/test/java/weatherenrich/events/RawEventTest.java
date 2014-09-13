package weatherenrich.events;

import java.util.Optional;

import junit.framework.TestCase;

import org.junit.Test;

public class RawEventTest extends TestCase {

	@Test
	public void testAsJson() {
		String event =
				"{"
				+ "\"subject\" : {"
				+     "\"hostname\" : \"prive-751fce1e0\""
				+ "},"
				+ "\"verb\" : \"perform\","
				+ "\"context\" : {"
				+ "    \"timestamp\" : \"2014-09-13T15:09:58+0000\""
				+ "},"
				+ "\"directObject\" : {"
				+ "    \"calculation\" : {"
				+ "        \"operation\" : \"addition\","
				+ "        \"args\" : [ \"1\", \"2\", \"4\", \"6\", \"7\", \"5\" ],"
				+ "        \"result\" : 25"
				+ "    }"
				+ "  }"
				+ "}";
		Optional<RawEvent> rawEvent = RawEvent.parse(event);
		assertTrue(rawEvent.isPresent());
	}
}