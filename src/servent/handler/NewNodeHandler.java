package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.NewNodeMessage;

public class NewNodeHandler implements MessageHandler{

    private Message clientMessage;

    public NewNodeHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {

        NewNodeMessage newNodeMessage = (NewNodeMessage) clientMessage;

        synchronized (AppConfig.addNeghboursLock)
        {
            //AppConfig.timestampedStandardPrint("Recieved newNode " + newNodeMessage.getNewNodePort());
            AppConfig.myServentInfo.getNeighborsPorts().add(newNodeMessage.getNewNodePort());
        }

    }

}
