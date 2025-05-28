package mutex;

import app.AppConfig;
import app.ServentInfo;
import servent.message.TokenMessage;
import servent.message.util.MessageUtil;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TokenMutex implements DistributedMutex {

//    private static final Object lockKey = new Object();

    private volatile boolean haveToken = false;
    private final Queue<Object> wantLock = new ConcurrentLinkedQueue<>();

    @Override
    public void lock(Object object) {
        wantLock.add(object);
        while (!haveToken) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void unlock(Object object) {
        wantLock.remove(object);
        if (wantLock.isEmpty()) {
            haveToken = false;
            sendTokenForward();
        }
    }

    public void receiveToken() {
        if (wantLock.isEmpty()) {
            sendTokenForward();
        } else {
            haveToken = true;
        }
    }

    public void sendTokenForward() {
        ServentInfo[] successorTable = AppConfig.chordState.getSuccessorTable();

        if (successorTable[0] == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            receiveToken();

            return;
        }

        int nextNodePort = successorTable[0].getListenerPort();
        MessageUtil.sendMessage(new TokenMessage(AppConfig.myServentInfo.getListenerPort(), nextNodePort));
    }
}
