package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.SendResultsMessage;

import java.util.ArrayList;

public class SendResultsHandler implements MessageHandler{

    Message clientMessage;

    public SendResultsHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        //AppConfig.timestampedStandardPrint("Stigli rezultati od " + clientMessage.getSenderPort());
        SendResultsMessage sendResultsMessage = (SendResultsMessage) clientMessage;
        if(AppConfig.resultsPerPort.get(sendResultsMessage.getSenderPort()) == null)
        {
            AppConfig.resultsPerPort.put(sendResultsMessage.getSenderPort(),new ArrayList<>());
        }
        AppConfig.resultsPerPort.get(sendResultsMessage.getSenderPort()).addAll(sendResultsMessage.getResults());
    }
}
