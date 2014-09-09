package weatherenrich.events;

public class RawEvent extends Event {

	public final DirectObject directObject;

	public RawEvent(String operation, String[] args, Integer result) {
		super("perform");
		this.directObject = new DirectObject(operation, args, result);
	}

	public static class DirectObject {
		public final Calculation calculation;

		public DirectObject(String operation, String[] args, Integer result) {
			this.calculation = new Calculation(operation, args, result);
		}
	}

	public static class Calculation {

		public String operation;
		public String[] args;
		public Integer result;

		public Calculation(String operation, String[] args, Integer result) {
			this.operation = operation;
			this.args = args;
			this.result = result;
		}
	}
}
