package weatherenrich;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import weatherenrich.events.EnrichedEvent;
import weatherenrich.events.Event;
import weatherenrich.events.RawEvent;
import WeatherAPI.IWeather;
import WeatherAPI.WeatherAPI;


public class WeatherEnrich {

	public static final String RAW_STREAM = "calc_events";

	public static final void main(String[] args) {
		Producer<String, String> producer = Event.createProducer("ec2-54-68-151-188.us-west-2.compute.amazonaws.com:9092");
		ConsumerIterator<byte[], byte[]> it = null;
		ConsumerConnector consumer = kafka.consumer.Consumer
				.createJavaConsumerConnector(createConfig());
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(RAW_STREAM, new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer
				.createMessageStreams(topicCountMap);
		KafkaStream<byte[], byte[]> stream = consumerMap.get(RAW_STREAM).get(0);
		while (true) {
			it = stream.iterator();
			while (it.hasNext()) {
				processEvent(new String(it.next().message()), producer);
			}
		}
	}

	private static void processEvent(String raw, Producer<String, String> producer) {
		System.out.println("Going to process event: " + raw);
		Optional<RawEvent> rawEvent = RawEvent.parse(raw);
		System.out.println("Have processed event: " + rawEvent);
		rawEvent.ifPresent(r -> {
			IWeather weather = WeatherAPI.getWeather("New York City", "NY");
			List<String> conditions = weather.getConditions()
					.stream()
					.map(o -> o.toString())
					.collect(Collectors.toList());
			double temp = weather.getDegreesCelsius();
			EnrichedEvent enrichedEvent = new EnrichedEvent(r, temp, conditions);
			System.out.println("<<Sending event to Kafka enriched stream>>\n" + enrichedEvent.asJson());
			enrichedEvent.sendTo(producer);
		});
	}

	private static ConsumerConfig createConfig() {
		Properties props = new Properties();
		props.put("zookeeper.connect", "ec2-54-68-151-188.us-west-2.compute.amazonaws.com:2181");
		props.put("group.id", "group1");
		props.put("zookeeper.session.timeout.ms", "20000");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");
		return new ConsumerConfig(props);
	}

}