package servent.message.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import app.AppConfig;
import app.Cancellable;
import app.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PoisonMessage;

/**
 * We will have as many instances of these workers as we have neighbors. Each of them
 * reads messages from a queue (two queues, actually, for Chandy-Lamport) and sends them
 * via a simple socket. The thread waits for an ACK on the same socket before sending another
 * message to the same servent.
 *
 * These threads are stopped via {@link PoisonMessage}.
 *
 */
public class FifoSendWorker implements Runnable, Cancellable {

    private int neighbor;

    public FifoSendWorker(int neighbor) {
        this.neighbor = neighbor;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message messageToSend = MessageUtil.pendingSnapshots.get(neighbor).poll(200, TimeUnit.MILLISECONDS);

                if (messageToSend == null) {
                    if (AppConfig.isWhite.get()) {
                        messageToSend = MessageUtil.pendingMessages.get(neighbor).poll(200, TimeUnit.MILLISECONDS);
                    }
                }

                if (messageToSend == null) {
                    continue;
                }

                if (messageToSend.getMessageType() == MessageType.POISON) {
                    break;
                }

                Socket sendSocket;

                synchronized (AppConfig.colorLock) {
                    if (!AppConfig.isWhite.get() && (messageToSend.getMessageType() == MessageType.TRANSACTION || messageToSend.getMessageType() == MessageType.POISON)) {
                        MessageUtil.pendingMessages.get(neighbor).put(messageToSend);
                        continue;
                    }

                    if (MessageUtil.MESSAGE_UTIL_PRINTING) {
                        AppConfig.timestampedStandardPrint("Sending message " + messageToSend);
                    }

                    ServentInfo receiverInfo = messageToSend.getReceiverInfo();

                    sendSocket = new Socket(receiverInfo.getIpAddress(), receiverInfo.getListenerPort());
                    ObjectOutputStream oos = new ObjectOutputStream(sendSocket.getOutputStream());
                    oos.writeObject(messageToSend);
                    oos.flush();

                    messageToSend.sendEffect();
                }

                sendSocket.close();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        try {
            MessageUtil.pendingMessages.get(neighbor).put(new PoisonMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
