package weatherenrich;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import weatherenrich.events.RawEvent;

public class WeatherEnrich {

	public static final String RAW_STREAM = "calc_events";

	public static final void main(String[] args) {
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
				processEvent(new String(it.next().message()));
			}
		}
	}

	private static void processEvent(String raw) {
		System.out.println("Going to process event: " + raw);
		Optional<RawEvent> rawEvent = RawEvent.parse(raw);
		System.out.println("Have processed event: " + rawEvent);
		rawEvent.ifPresent(r ->
		       System.out.println(r.asJson()));
	}

	private static ConsumerConfig createConfig() {
		Properties props = new Properties();
//		TODO: replace local ip to EC2 public ip
		props.put("zookeeper.connect", "ec2-54-68-191-209.us-west-2.compute.amazonaws.com:2181");
		props.put("group.id", "group1");
		props.put("zookeeper.session.timeout.ms", "20000");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");
		return new ConsumerConfig(props);
	}

}