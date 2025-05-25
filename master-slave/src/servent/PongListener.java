package servent;

import app.AppConfig;
import app.Cancellable;
import servent.message.PingMessage;
import servent.message.util.MessageUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class PongListener implements Runnable, Cancellable {

    private volatile boolean working = true;
    private volatile int clockwiseMachineAlive = 2;
    private volatile int anticlockwiseMachineAlive = 2;

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

            synchronized (this) {
                int clockwise = clockwiseMachineAlive;
                if (AppConfig.chordState.getSuccessorTable()[0] != null) {
                    if (clockwise == 2) {
                        AppConfig.timestampedStandardPrint("Sending Ping check clockwise: " + clockwiseMachineAlive);
                        MessageUtil.sendMessage(new PingMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort()));
                        clockwiseMachineAlive = 1;
                    } else if (clockwise == 1) {
                        AppConfig.timestampedStandardPrint("Sending worrying Ping check clockwise: " + clockwiseMachineAlive);
                        AppConfig.timestampedStandardPrint(" ---------- SUCCESSOR:  " + AppConfig.chordState.getSuccessorTable()[1].getListenerPort());
                        MessageUtil.sendMessage(new PingMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getSuccessorTable()[1].getListenerPort(), AppConfig.myServentInfo.getListenerPort() + ":" + AppConfig.chordState.getNextNodePort()));
                        clockwiseMachineAlive = 0;
                    } else if (clockwise == 0) {
                        AppConfig.timestampedStandardPrint("Killing clockwise");
                        sendKillMessage(AppConfig.chordState.getNextNodePort());
                        clockwiseMachineAlive = -1;
                    }
                }

                int anticlockwise = anticlockwiseMachineAlive;
                if (AppConfig.chordState.getPredecessor() != null) {
                    if (anticlockwise == 2) {
                        AppConfig.timestampedStandardPrint("Sending Ping check anticlockwise: " + anticlockwiseMachineAlive);
                        AppConfig.timestampedStandardPrint(" ---------- PREDECESSOR:  " + AppConfig.chordState.getPredecessor().getListenerPort());
                        MessageUtil.sendMessage(new PingMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getPredecessor().getListenerPort()));
                        anticlockwiseMachineAlive = 1;
                    } else if (anticlockwise == 1) {
                        AppConfig.timestampedStandardPrint("Sending worrying Ping check anticlockwise: " + anticlockwiseMachineAlive);
                        MessageUtil.sendMessage(new PingMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getSuccessorTable()[1].getListenerPort(), AppConfig.myServentInfo.getListenerPort() + ":" + AppConfig.chordState.getPredecessor().getListenerPort()));
                        anticlockwiseMachineAlive = 0;
                    } else if (anticlockwise == 0) {
                        AppConfig.timestampedStandardPrint("Killing anticlockwise");
                        sendKillMessage(AppConfig.chordState.getPredecessor().getListenerPort());
                        anticlockwiseMachineAlive = -1;
                    }
                }
            }
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

    public synchronized void setClockwiseMachineAliveValue(int value) {
        clockwiseMachineAlive = value;
        AppConfig.timestampedStandardPrint("AAAAAAAAAAAA CLOOOOCK: " + clockwiseMachineAlive);
    }

    public synchronized void setAnticlockwiseMachineAliveValue(int value) {
        anticlockwiseMachineAlive = value;
        AppConfig.timestampedStandardPrint("BBBBBBBBBBB ANTI_CLOOOOCK: " + anticlockwiseMachineAlive);
    }
}
