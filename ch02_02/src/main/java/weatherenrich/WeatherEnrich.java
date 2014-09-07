package weatherenrich;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

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
		System.out.println(raw);
	}

	private static ConsumerConfig createConfig() {
		Properties props = new Properties();
		props.put("zookeeper.connect", "172.22.13.43:2181");
		props.put("group.id", "group1");
		props.put("zookeeper.session.timeout.ms", "10000");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");
		return new ConsumerConfig(props);
	}

}