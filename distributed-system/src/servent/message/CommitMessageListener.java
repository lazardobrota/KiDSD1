package servent.message;

import app.Cancellable;
import app.CausalBroadcastShared;

import java.util.concurrent.TimeUnit;

public class CommitMessageListener implements Runnable, Cancellable {

    private volatile boolean working = true;

    @Override
    public void run() {
        while (working) {
            try {
                PendingMessage pendingMessage = CausalBroadcastShared.GetCommitedCausalMessageQueue().poll(10, TimeUnit.SECONDS);
                if (pendingMessage == null)
                    continue;

                if (pendingMessage.isSending())
                    CausalBroadcastShared.sendingMessage(pendingMessage);
                else
                    CausalBroadcastShared.receiveMessage(pendingMessage, pendingMessage.isUpdateVectorClock());

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void stop() {
        working = false;
    }
}
