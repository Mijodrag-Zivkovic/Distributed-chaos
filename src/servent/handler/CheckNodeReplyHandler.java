package servent.handler;

import app.AppConfig;
import servent.message.CheckNodeReply;
import servent.message.Message;

public class CheckNodeReplyHandler implements MessageHandler{

    Message clientMessage;

    public CheckNodeReplyHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {

        CheckNodeReply message = (CheckNodeReply) clientMessage;
        synchronized (AppConfig.suspiciousLock)
        {
            //todo proveri da li sam suspector ili helper
            if (AppConfig.myServentInfo.getListenerPort()==AppConfig.suspectorPort)
            {
                if(message.isSuspicious())
                {

                }
                else
                {
                    //todo dobili smo odgovor koji smo cekali
                    if (AppConfig.suspiciousPort== message.getSenderPort())
                    {
                        AppConfig.didReply=true;
                    }
                    //todo dobili smo odgovor helpera
                    else
                    {
                        AppConfig.helperDidReply = true;
                    }
                }
            }
            else
            {
                AppConfig.didReply=true;
            }
        }
    }
}
