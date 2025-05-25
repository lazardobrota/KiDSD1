package servent.message;

import java.util.Map;

public class WelcomeMessage extends BasicMessage {

	private static final long serialVersionUID = -8981406250652693908L;

	private Map<Integer, String> values;
	
	public WelcomeMessage(int senderPort, int receiverPort, Map<Integer, String> values) {
		super(MessageType.WELCOME, senderPort, receiverPort);
		
		this.values = values;
	}
	
	public Map<Integer, String> getValues() {
		return values;
	}
}
