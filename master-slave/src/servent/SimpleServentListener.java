package servent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.AppConfig;
import app.Cancellable;
import mutex.DistributedMutex;
import servent.handler.*;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class SimpleServentListener implements Runnable, Cancellable {

    private volatile boolean working = true;

    private PongListener pongListener;
    private DistributedMutex mutex;

    public SimpleServentListener(PongListener pongListener, DistributedMutex mutex) {
        this.pongListener = pongListener;
        this.mutex = mutex;
    }

    /*
     * Thread pool for executing the handlers. Each client will get it's own handler thread.
     */
    private final ExecutorService threadPool = Executors.newWorkStealingPool();

    @Override
    public void run() {
        ServerSocket listenerSocket = null;
        try {
            listenerSocket = new ServerSocket(AppConfig.myServentInfo.getListenerPort(), 100);
            /*
             * If there is no connection after 1s, wake up and see if we should terminate.
             */
            listenerSocket.setSoTimeout(1000);
        } catch (IOException e) {
            AppConfig.timestampedErrorPrint("Couldn't open listener socket on: " + AppConfig.myServentInfo.getListenerPort());
            System.exit(0);
        }


        while (working) {
            try {
                Message clientMessage;

                Socket clientSocket = listenerSocket.accept();

                //GOT A MESSAGE! <3
                clientMessage = MessageUtil.readMessage(clientSocket);

                MessageHandler messageHandler = new NullHandler(clientMessage);

                /*
                 * Each message type has it's own handler.
                 * If we can get away with stateless handlers, we will,
                 * because that way is much simpler and less error prone.
                 */
                switch (clientMessage.getMessageType()) {
                    case NEW_NODE:
                        messageHandler = new NewNodeHandler(clientMessage, mutex);
                        break;
                    case WELCOME:
                        messageHandler = new WelcomeHandler(clientMessage);
                        break;
                    case SORRY:
                        messageHandler = new SorryHandler(clientMessage);
                        break;
                    case UPDATE:
                        messageHandler = new UpdateHandler(clientMessage, mutex);
                        break;
                    case PUT:
                        messageHandler = new PutHandler(clientMessage);
                        break;
                    case ASK_GET:
                        messageHandler = new AskGetHandler(clientMessage);
                        break;
                    case TELL_GET:
                        messageHandler = new TellGetHandler(clientMessage);
                        break;
                    case PING:
                        messageHandler = new PingHandler(clientMessage);
                        break;
                    case PONG:
                        messageHandler = new PongHandler(clientMessage, pongListener);
                        break;
                    case LIST_FILES:
                        messageHandler = new ListFilesHandler(clientMessage);
                        break;
                    case LIST_FILES_RECEIVE:
                        messageHandler = new ListFilesReceiveHandler(clientMessage);
                        break;
                    case ASK_LIST_FILES:
                        messageHandler = new AskListFilesHandler(clientMessage);
                        break;
                    case ASK_LIST_FILES_ERROR:
                        messageHandler = new AskListFilesErrorHandler(clientMessage);
                        break;
                    case REMOVE_FILE:
                        messageHandler = new RemoveFileHandler(clientMessage);
                        break;
                    case REMOVE_FILE_RECEIVE:
                        messageHandler = new RemoveFileReceiveHandler(clientMessage);
                        break;
                    case FOLLOW:
                        messageHandler = new FollowHandler(clientMessage);
                        break;
                    case FOLLOW_FOUND:
                        messageHandler = new FollowFoundHandler(clientMessage);
                        break;
                    case TOKEN:
                        messageHandler = new TokenHandler(clientMessage, mutex);
                        break;
                    case COPY_DATA:
                        messageHandler = new CopyDataHandler(clientMessage);
                        break;
                    case POISON:
                        break;
                }

                threadPool.submit(messageHandler);
            } catch (SocketTimeoutException timeoutEx) {
                //Uncomment the next line to see that we are waking up every second.
//				AppConfig.timedStandardPrint("Waiting...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        this.working = false;
    }

}
