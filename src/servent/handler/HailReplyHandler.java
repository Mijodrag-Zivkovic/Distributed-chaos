package servent.handler;

import app.AppConfig;
import servent.message.HailReplyMessage;
import servent.message.Message;

public class HailReplyHandler implements MessageHandler{

    private Message clientMessage;

    public HailReplyHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {

        HailReplyMessage hailReply = (HailReplyMessage) clientMessage;

        synchronized (AppConfig.addNeghboursLock)
        {
            //AppConfig.timestampedStandardPrint("Dobili smo hailReply od " + hailReply.getSenderPort());
            //AppConfig.timestampedStandardPrint("Dodajem komsiju " + hailReply.getSenderPort());
            AppConfig.myServentInfo.getNeighborsPorts().add(hailReply.getSenderPort());
//            for (Integer neighbour : hailReply.getNeighbours())
//                AppConfig.timestampedStandardPrint("Dodajem komsiju " + neighbour);
            AppConfig.myServentInfo.getNeighborsPorts().addAll(hailReply.getNeighbours());

        }

    }
}
