package servent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.AppConfig;
import app.Cancellable;
import servent.handler.*;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class SimpleServentListener implements Runnable, Cancellable {

	private volatile boolean working = true;
	
	public SimpleServentListener() {
	}

	/*
	 * Thread pool for executing the handlers. Each client will get it's own handler thread.
	 */
	private final ExecutorService threadPool = Executors.newWorkStealingPool();
	
	@Override
	public void run() {
		ServerSocket listenerSocket = null;
		try {
			listenerSocket = new ServerSocket(AppConfig.myServentInfo.getListenerPort(),100);
			/*
			 * If there is no connection after 1s, wake up and see if we should terminate.
			 */
			listenerSocket.setSoTimeout(1000);
		} catch (IOException e) {
			AppConfig.timestampedErrorPrint("Couldn't open listener socket on: " + AppConfig.myServentInfo.getListenerPort());
			System.exit(0);
		}
		//todo send hail to bootstrap
		//AppConfig.timestampedStandardPrint("saljem poruku bsu");
		//MessageUtil.sendMessage(new HailMessage(AppConfig.myServentInfo.getListenerPort(),AppConfig.BS_PORT));
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
					case BOOTSTRAP_REPLY:
						messageHandler = new BootstrapReplyHandler(clientMessage);
						break;
					case HAIL:
						messageHandler = new HailHandler(clientMessage);
						break;
					case HAIL_REPLY:
						messageHandler = new HailReplyHandler(clientMessage);
						break;
					case NEW_NODE:
						messageHandler = new NewNodeHandler(clientMessage);
						break;
					case UPDATE_FRACTAL_ID:
						messageHandler = new UpdateFractalIdHandler(clientMessage);
						break;
					case WAKE_IDLE_NODES:
						messageHandler = new WakeIdleNodesHandler(clientMessage);
						break;
					case SEND_RESULTS:
						messageHandler = new SendResultsHandler(clientMessage);
						break;
					case CHECK_NODE:
						messageHandler = new CheckNodeHandler(clientMessage);
						break;
					case CHECK_NODE_REPLY:
						messageHandler = new CheckNodeReplyHandler(clientMessage);
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
