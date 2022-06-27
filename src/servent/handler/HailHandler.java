package servent.handler;

import app.AppConfig;
import servent.message.HailReplyMessage;
import servent.message.Message;
import servent.message.NewNodeMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;

public class HailHandler implements MessageHandler{

    private Message clientMessage;

    public HailHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {

        //send hail reply sa svojim podatkom i new node komsijama
        //lockuj deo koda za dodavanje komsije
        //

        //AppConfig.timestampedStandardPrint("Dobili smo hail od " + clientMessage.getSenderPort());
        synchronized (AppConfig.addNeghboursLock)
        {

            //saljemo komsijama
            for (Integer neighbourPort : AppConfig.myServentInfo.getNeighborsPorts())
            {
                //AppConfig.timestampedStandardPrint("Saljem newNode komsiji " + neighbourPort + " o novom nodeu " + clientMessage.getSenderPort());
                MessageUtil.sendMessage(new NewNodeMessage(AppConfig.myServentInfo.getListenerPort(),neighbourPort, clientMessage.getSenderPort()));
            }
            //saljemo podatke o komsijama novom cvoru
            MessageUtil.sendMessage(new HailReplyMessage(AppConfig.myServentInfo.getListenerPort(),clientMessage.getSenderPort(),new ArrayList<>(AppConfig.myServentInfo.getNeighborsPorts())));
            //dodamo kod sebe
            AppConfig.myServentInfo.getNeighborsPorts().add(clientMessage.getSenderPort());

            //posalji bootstrapu new node da bi dodao u aktivne cvorove
            //AppConfig.timestampedStandardPrint("obavestavam bootstrap o dodavanju " + clientMessage.getSenderPort());
            MessageUtil.sendMessage(new NewNodeMessage(AppConfig.myServentInfo.getListenerPort(),AppConfig.BS_PORT, clientMessage.getSenderPort()));
        }

    }
}
