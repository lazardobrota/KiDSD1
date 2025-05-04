package servent.message;

import servent.handler.CausalMessageHandler;

public class PendingMessage {

    private final boolean isSending;
    private final Message message;
    private final CausalMessageHandler causalMessageHandler;

    public PendingMessage(boolean isSending, Message message, CausalMessageHandler causalMessageHandler) {
        this.isSending = isSending;
        this.message = message;
        this.causalMessageHandler = causalMessageHandler;
    }

    public boolean isSending() {
        return isSending;
    }

    public Message getMessage() {
        return message;
    }

    public CausalMessageHandler getCausalMessageHandler() {
        return causalMessageHandler;
    }
}
