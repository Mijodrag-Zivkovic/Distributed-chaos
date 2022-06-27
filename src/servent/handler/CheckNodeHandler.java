package servent.handler;

import app.AppConfig;
import servent.message.CheckNodeMessage;
import servent.message.CheckNodeReply;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class CheckNodeHandler implements MessageHandler{

    Message clientMessage;

    public CheckNodeHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {

        CheckNodeMessage message = (CheckNodeMessage) clientMessage;
        if (message.isHelper())
        {
            //todo treba da saljem poruku sumnjivom
            synchronized (AppConfig.suspiciousLock)
            {
                AppConfig.suspectorPort = message.getSenderPort();
                AppConfig.suspiciousPort = message.getSuspiciousPort();
                AppConfig.timeOfSending = System.currentTimeMillis();
            }
            MessageUtil.sendMessage(new CheckNodeMessage(AppConfig.myServentInfo.getListenerPort(), message.getSuspiciousPort(), false, -1));
            try {
                Thread.sleep(AppConfig.softLimit);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (AppConfig.suspiciousLock)
            {
                if (AppConfig.didReply)
                {
                    MessageUtil.sendMessage(new CheckNodeReply(AppConfig.myServentInfo.getListenerPort(),AppConfig.suspectorPort,false));
                }
                else
                    MessageUtil.sendMessage(new CheckNodeReply(AppConfig.myServentInfo.getListenerPort(),AppConfig.suspectorPort,true));
            }
        }
        else
        {
            //todo ja sam sumnjiv, odgovaram
            MessageUtil.sendMessage(new CheckNodeReply(AppConfig.myServentInfo.getListenerPort(), clientMessage.getSenderPort(),false));
        }

    }
}
