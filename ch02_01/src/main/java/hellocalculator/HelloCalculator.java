package hellocalculator;

import hellocalculator.events.Event;
import hellocalculator.events.InputBadDataEvent;
import hellocalculator.events.PerformCalculationEvent;

import java.util.Arrays;

import kafka.javaapi.producer.Producer;

public class HelloCalculator {

	public static void main(String[] args) {
		
		final Producer<String, String> producer = Event.createProducer("ec2-54-68-191-209.us-west-2.compute.amazonaws.com:9092");
		
		if (args.length < 2) {
			String err = "Too fiew inputs (" + args.length + ") ";
			System.out.println("Error: " + err);
			new InputBadDataEvent(args, err).sendTo(producer);
		} else {
			try {
				Integer sum = sum(args);
				System.out.println("SUM: " + sum);
				new PerformCalculationEvent("addition", args, sum).sendTo(producer);
			} catch (NumberFormatException nfe) {
				String err = "Not all inputs parseable to Integer";
				System.out.println("Error: " + err);
				new InputBadDataEvent(args, err).sendTo(producer);
			}
		}
	}

	public static Integer sum(String[] args) throws NumberFormatException {
		return Arrays.asList(args).stream().mapToInt(str -> Integer.parseInt(str)).sum();
	}
}
