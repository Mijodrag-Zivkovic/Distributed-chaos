package servent.handler;

import app.AppConfig;
import app.DrawWorker;
import servent.message.Message;
import servent.message.WakeIdleNodesMessage;

public class WakeIdleNodesHandler implements MessageHandler{


    Message clientMessage;

    public WakeIdleNodesHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        WakeIdleNodesMessage message = (WakeIdleNodesMessage) clientMessage;
        synchronized (AppConfig.startingCoordinatesLock)
        {
            AppConfig.timestampedStandardPrint("Dobio nove koordinate od " + clientMessage.getSenderPort());
            for (int i = 0; i < message.getNewCoordinates().size();i++)
            {
                AppConfig.timestampedStandardPrint("X = " + message.getNewCoordinates().get(i).getX());
                AppConfig.timestampedStandardPrint("Y = " + message.getNewCoordinates().get(i).getY());
            }

            AppConfig.myStartingCoordinates.clear();
            AppConfig.myStartingCoordinates.addAll(message.getNewCoordinates());
            DrawWorker.setIdle(false);
        }
    }
}
