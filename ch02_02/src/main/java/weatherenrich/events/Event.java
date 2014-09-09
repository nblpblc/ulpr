package weatherenrich.events;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JacksonUtils;

public abstract class Event {

	public Subject subject;
	public String verb;
	public Context context;

	protected static final ObjectMapper MAPPER = JacksonUtils.newMapper();
	private static final String STREAM = "calc_events";
	
	public Event() {
		this.subject = null;
		this.verb = null;
		this.context = null;
	}

	public Event(String verb) {
		this.subject = new Subject(getHostname());
		this.verb = verb;
		this.context = new Context(getTimestamp());
	}

	public static Producer<String, String> createProducer(String brokers) {
		Properties props = new Properties();
		props.put("metadata.broker.list", brokers);
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("request.required.acks", "1");
		ProducerConfig config = new ProducerConfig(props);
		return new Producer<String, String>(config);
	}

	public void sendTo(Producer<String, String> producer) {
		String key = this.subject.hostname;
		String message = this.asJson();
		KeyedMessage<String, String> data = new KeyedMessage<>(STREAM, key, message);
		producer.send(data);
	}

	protected String getTimestamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:MM:ssZ");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(new Date());
	}

	private String getHostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException uhe) {
			return "unknown";
		}
	}

	public String asJson() {
		try {
			return MAPPER.writeValueAsString(this);
		} catch (JsonProcessingException jpe) {
			throw new RuntimeException("Problem converting event to JSON", jpe);
		}
	}

	public static class Subject {
		public final String hostname;
		
		public Subject() {
			this.hostname = null;
		}

		public Subject(String hostname) {
			this.hostname = hostname;
		}
	}

	public static class Context {
		public final String timestamp;
		
		public Context() {
			this.timestamp = null;
		}

		public Context(String timestamp) {
			this.timestamp = timestamp;
		}
	}
}
