package hellocalculator;

import static org.junit.Assert.assertTrue;
import hellocalculator.events.InputBadDataEvent;
import hellocalculator.events.PerformCalculationEvent;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;

public class EventSchemaTest {

	private static ObjectMapper MAPPER = new ObjectMapper();

	private final JsonValidator validator;
	private final JsonNode eventSchema;

	public EventSchemaTest() throws IOException {
		super();
		final ValidationConfiguration cfg = ValidationConfiguration
				.newBuilder().setDefaultVersion(SchemaVersion.DRAFTV4).freeze();
		validator = JsonSchemaFactory.newBuilder()
				.setValidationConfiguration(cfg).freeze().getValidator();
		eventSchema = JsonLoader.fromResource("/event_schema.json");
	}

	@Test
	public void validatePerformCalculationEvent() throws ProcessingException,
			IOException {
		PerformCalculationEvent event = new PerformCalculationEvent("addition",
				new String[] { "11", "22", "33" }, 66);
		JsonNode node = MAPPER.readTree(event.asJson());
		final ProcessingReport report = validator.validate(eventSchema, node);
		assertTrue(report.isSuccess());
	}

	@Test
	public void validateInputBadDataEvent() throws ProcessingException,
			IOException {

		InputBadDataEvent badData = new InputBadDataEvent(new String[] { "23",
				"17", "ohno" }, "not all inputs parseable to Integers");
		JsonNode node = MAPPER.readTree(badData.asJson());
		final ProcessingReport report = validator.validate(eventSchema, node);
		assertTrue(report.isSuccess());
	}
}
