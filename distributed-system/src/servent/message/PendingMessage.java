package servent.message;

import servent.handler.CausalMessageHandler;

import java.util.ArrayList;
import java.util.List;

public class PendingMessage {

    private final boolean isSending;
    private final Message message;
    private final List<Message> sendMessagesList;
    private final CausalMessageHandler causalMessageHandler;

    public PendingMessage(boolean isSending, Message message, CausalMessageHandler causalMessageHandler) {
        this.isSending = isSending;
        this.message = message;
        this.causalMessageHandler = causalMessageHandler;

        sendMessagesList = new ArrayList<>();
    }

    public PendingMessage(boolean isSending, List<Message> sendMessagesList, CausalMessageHandler causalMessageHandler) {
        this.isSending = isSending;
        this.sendMessagesList = sendMessagesList;
        this.causalMessageHandler = causalMessageHandler;

        message = null;
    }

    public boolean isSending() {
        return isSending;
    }

    public Message getMessage() {
        return message;
    }

    public List<Message> getSendMessagesList() {
        return sendMessagesList;
    }

    public CausalMessageHandler getCausalMessageHandler() {
        return causalMessageHandler;
    }
}
