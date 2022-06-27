package servent.message;

import app.CoordinatesModel;

import java.util.ArrayList;

public class SendResultsMessage extends BasicMessage{

    ArrayList<CoordinatesModel> results;

    public SendResultsMessage( int senderPort, int receiverPort, ArrayList<CoordinatesModel> results) {
        super(MessageType.SEND_RESULTS, senderPort, receiverPort);
        this.results = results;
    }

    public ArrayList<CoordinatesModel> getResults() {
        return results;
    }
}
