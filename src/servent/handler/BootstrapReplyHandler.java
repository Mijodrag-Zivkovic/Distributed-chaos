package servent.handler;

import app.AppConfig;
import servent.message.BootstrapReplyMessage;
import servent.message.HailMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class BootstrapReplyHandler implements MessageHandler{

    private Message clientMessage;

    public BootstrapReplyHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        //todo logika za dalje
        //AppConfig.timestampedStandardPrint("u bootsrapReply handleru smo");
        BootstrapReplyMessage bsReply = (BootstrapReplyMessage) clientMessage;
        int serventPort = bsReply.getPort();
        if (serventPort==-1)
        {
            //AppConfig.timestampedStandardPrint("We are first in system, nice");
        }
        else
        {
            //AppConfig.timestampedStandardPrint("Saljem hail serventu " + serventPort);
            MessageUtil.sendMessage(new HailMessage(AppConfig.myServentInfo.getListenerPort(),serventPort));
        }
    }
}
