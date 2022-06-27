package servent.message;

import app.CoordinatesModel;

import java.util.ArrayList;

public class WakeIdleNodesMessage extends BasicMessage{

    private ArrayList<CoordinatesModel> newCoordinates;

    public WakeIdleNodesMessage(int senderPort, int receiverPort, ArrayList<CoordinatesModel> newCoordinates) {
        super(MessageType.WAKE_IDLE_NODES, senderPort, receiverPort);
        this.newCoordinates = newCoordinates;
    }

    public ArrayList<CoordinatesModel> getNewCoordinates() {
        return newCoordinates;
    }
}
