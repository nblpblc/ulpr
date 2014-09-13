package weatherenrich.events;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;

public class RawEvent extends Event {

	public final DirectObject directObject;
	
	private static ValidationConfiguration cfg = ValidationConfiguration.newBuilder()
			.setDefaultVersion(SchemaVersion.DRAFTV4).freeze();
	private static final JsonValidator validator = JsonSchemaFactory.newBuilder() 
			.setValidationConfiguration(cfg).freeze().getValidator();
	
	private static final JsonNode schema;
	static {
		try {
			schema = JsonLoader.fromResource("/raw_calculation_schema.json");
		} catch (IOException ioe) {
			throw new RuntimeException("Unable to load raw event's schema from raw_calculation_schema.json", ioe);
		}
	}
	
	public RawEvent() {
		directObject = null;
	}

	public RawEvent(String operation, String[] args, Integer result) {
		super("perform");
		this.directObject = new DirectObject(operation, args, result);
	}

	public static class DirectObject {
		public final Calculation calculation;
		
		public DirectObject() {
			this.calculation = null;
		}

		public DirectObject(String operation, String[] args, Integer result) {
			this.calculation = new Calculation(operation, args, result);
		}
	}

	public static class Calculation {

		public String operation;
		public String[] args;
		public Integer result;

		public Calculation() {
			super();
			this.operation = null;
			this.args = null;
			this.result = null;
		}

		public Calculation(String operation, String[] args, Integer result) {
			this.operation = operation;
			this.args = args;
			this.result = result;
		}
	}
	
	public static Optional<RawEvent> parse(String json) {
		Optional<RawEvent> event;
		try {
			JsonNode node= MAPPER.readTree(json);
			ProcessingReport report = validator.validate(schema, node);
			System.out.println("Processing report: " + report);
			event = (report.isSuccess()) ? Optional.of(MAPPER.readValue(json, RawEvent.class)) : Optional.empty();
		} catch (IOException | ProcessingException e) {
			System.out.println("Raw event validation error: " + e);
			event = Optional.empty();
		}
		return event;
	}
}
