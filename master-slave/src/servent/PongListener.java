package servent;

import app.AppConfig;
import app.Cancellable;
import servent.message.PingMessage;
import servent.message.util.MessageUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PongListener implements Runnable, Cancellable {

    private static final Object lockKey = new Object();
    private volatile boolean working = true;
    private final AtomicInteger clockwiseMachineAlive = new AtomicInteger(2);
    private final AtomicInteger anticlockwiseMachineAlive = new AtomicInteger(2);
    private final AtomicBoolean clockwiseUpdatedValue = new AtomicBoolean(false);
    private final AtomicBoolean anticlockwiseUpdatedValue = new AtomicBoolean(false);
//    private volatile int clockwiseMachineAlive = 2;
//    private volatile int anticlockwiseMachineAlive = 2;


    public PongListener() {
    }

    @Override
    public void run() {
        while (working) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            //There needs to be at least 3 nodes for PongListener to work
            if (AppConfig.chordState.getSuccessorTable()[0] == null || AppConfig.chordState.getSuccessorTable()[1] == null)
                continue;


            if (clockwiseUpdatedValue.get()) {
                synchronized (lockKey) {
                    clockwiseMachineAlive.set(2);
                    clockwiseUpdatedValue.set(false);
                }
            }

            if (anticlockwiseUpdatedValue.get()) {
                synchronized (lockKey) {
                    anticlockwiseMachineAlive.set(2);
                    anticlockwiseUpdatedValue.set(false);
                }
            }

            switch (clockwiseMachineAlive.get()) {
                case 2:
                    AppConfig.timestampedStandardPrint("Ping clockwise");
                    MessageUtil.sendMessage(new PingMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort()));
                    break;
                case 1:
                    AppConfig.timestampedStandardPrint("Ping LOOOOONG clockwise");
                    MessageUtil.sendMessage(new PingMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getSuccessorTable()[1].getListenerPort(), AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort()));
                    break;
                case 0:
                    AppConfig.timestampedStandardPrint("Killing clockwise");
                    sendKillMessage(AppConfig.chordState.getNextNodePort());
                    break;
            }

            if (clockwiseMachineAlive.get() > - 1)
                clockwiseMachineAlive.decrementAndGet();


            switch (anticlockwiseMachineAlive.get()) {
                case 2:
                    AppConfig.timestampedStandardPrint("Ping ANTIclockwise");
                    MessageUtil.sendMessage(new PingMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getPredecessor().getListenerPort()));
                    break;
                case 1:
                    AppConfig.timestampedStandardPrint("Ping LOOOOONG ANTIclockwise");
                    MessageUtil.sendMessage(new PingMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getSuccessorTable()[0].getListenerPort(), AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getPredecessor().getListenerPort()));
                    break;
                case 0:
                    AppConfig.timestampedStandardPrint("Killing ANTIclockwise");
                    sendKillMessage(AppConfig.chordState.getPredecessor().getListenerPort());
                    break;
            }

            if (anticlockwiseMachineAlive.get() > - 1)
                anticlockwiseMachineAlive.decrementAndGet();
        }
    }

    private void sendKillMessage(int portOfNodeToKill) {
        int bsPort = AppConfig.BOOTSTRAP_PORT;

        try {
            Socket bsSocket = new Socket("localhost", bsPort);

            PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
            bsWriter.write("Kill\n" + portOfNodeToKill + "\n");
            bsWriter.flush();

            bsSocket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        working = false;
    }

    public void pongClockWiseArrived() {
        synchronized (lockKey) {
            clockwiseUpdatedValue.set(true);
            AppConfig.timestampedStandardPrint("CLOOOOCK PONG");
        }
    }

    public void pongAntiClockWiseArrived() {
        synchronized (lockKey) {
            anticlockwiseUpdatedValue.set(true);
            AppConfig.timestampedStandardPrint("ANTI_CLOOOOCK PONG");
        }
    }

//    public synchronized void setClockwiseMachineAliveValue(int value) {
//        clockwiseMachineAlive = value;
//        AppConfig.timestampedStandardPrint("AAAAAAAAAAAA CLOOOOCK: " + clockwiseMachineAlive);
//    }
//
//    public synchronized void setAnticlockwiseMachineAliveValue(int value) {
//        anticlockwiseMachineAlive = value;
//        AppConfig.timestampedStandardPrint("BBBBBBBBBBB ANTI_CLOOOOCK: " + anticlockwiseMachineAlive);
//    }
}
