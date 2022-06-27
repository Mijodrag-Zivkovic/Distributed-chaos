package app;

import servent.message.CheckNodeMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class QuitDetectionWorker implements Runnable{


    @Override
    public void run() {

        MessageUtil.sendMessage(new CheckNodeMessage(AppConfig.myServentInfo.getListenerPort(),AppConfig.suspiciousPort,false,-1));
        try {
            Thread.sleep(AppConfig.softLimit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean didReply;
        synchronized (AppConfig.suspiciousLock)
        {
            didReply = AppConfig.didReply;
        }
        if (didReply)
        {
            AppConfig.timestampedStandardPrint("Sumnjivi cvor je odgovorio");
        }
        else
        {
            AppConfig.timestampedStandardPrint("Sumnjivi cvor nije odgovorio nakon krace granice");
            MessageUtil.sendMessage(new CheckNodeMessage(AppConfig.myServentInfo.getListenerPort(),1300,true,AppConfig.suspiciousPort));
            try {
                Thread.sleep(AppConfig.hardLimit);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (AppConfig.suspiciousLock)
            {
                if (AppConfig.didReply)
                    AppConfig.timestampedStandardPrint("Sumnjivi cvor je odgovorio");
                else if (AppConfig.helperDidReply)
                    AppConfig.timestampedStandardPrint("Helper je dobio odgovor a mi ne, problem je u nama");
                else
                    AppConfig.timestampedStandardPrint("Sumnjivi cvor treba da se ugasi");
            }
        }
    }
}
