package app;

import cli.CLIParser;
import servent.SimpleServentListener;
import servent.message.BootstrapReplyMessage;
import servent.message.Message;
import servent.message.NewNodeMessage;
import servent.message.util.MessageUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BootStrapMain {

    private volatile boolean working = true;
    private List<Integer> activeServents;

    private class CLIWorker implements Runnable {
        @Override
        public void run() {
            Scanner sc = new Scanner(System.in);

            String line;
            while(true) {
                line = sc.nextLine();

                if (line.equals("stop")) {
                    working = false;
                    break;
                }
            }

            sc.close();
        }
    }

    public BootStrapMain() {
        activeServents = new ArrayList<>();
    }

    public void doBootstrap(int bsPort) {
        Thread cliThread = new Thread(new CLIWorker());
        cliThread.start();

        ServerSocket listenerSocket = null;
        try {
            listenerSocket = new ServerSocket(bsPort);
            listenerSocket.setSoTimeout(1000);
        } catch (IOException e1) {
            AppConfig.timestampedErrorPrint("Problem while opening listener socket.");
            System.exit(0);
        }

        Random rand = new Random(System.currentTimeMillis());

        while (working) {
            try {
                Socket newServentSocket = listenerSocket.accept();
                newServentSocket.setSoTimeout(1000);

                /*
                 * Handling these messages is intentionally sequential, to avoid problems with
                 * concurrent initial starts.
                 *
                 * In practice, we would have an always-active backbone of servents to avoid this problem.
                 */

                Message clientMessage = MessageUtil.readMessage(newServentSocket);
                //case za hail ili new node
                switch (clientMessage.getMessageType()) {
                    case HAIL:
                        AppConfig.timestampedStandardPrint("Got hail from " + clientMessage.getSenderPort());
                        if (activeServents.size() == 0) {

                            AppConfig.timestampedStandardPrint("Active servants 0");
                            activeServents.add(clientMessage.getSenderPort()); //first one doesn't need to confirm
                            MessageUtil.sendMessage(new BootstrapReplyMessage(bsPort,clientMessage.getSenderPort(),-1));
                        } else {
                            int randServent = activeServents.get(rand.nextInt(activeServents.size()));

                            MessageUtil.sendMessage(new BootstrapReplyMessage(bsPort,clientMessage.getSenderPort(),randServent));
                        }
                        break;
                    case NEW_NODE:
                        AppConfig.timestampedStandardPrint("Got NewNodeMessage from "+clientMessage.getSenderPort());
                        NewNodeMessage newNodeMessage = (NewNodeMessage) clientMessage;
                        if (activeServents.contains(newNodeMessage.getNewNodePort()))
                            AppConfig.timestampedErrorPrint("Already have port " + newNodeMessage.getNewNodePort());
                        else
                            activeServents.add(newNodeMessage.getNewNodePort());
                        break;
                }


            } catch (SocketTimeoutException e) {

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Expects one command line argument - the port to listen on.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            AppConfig.timestampedErrorPrint("Bootstrap started without port argument.");
        }

        int bsPort = 0;
        try {
            bsPort = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Bootstrap port not valid: " + args[0]);
            System.exit(0);
        }

        AppConfig.timestampedStandardPrint("Bootstrap server started on port: " + bsPort);

        BootStrapMain bs = new BootStrapMain();
        bs.doBootstrap(bsPort);
    }
}
