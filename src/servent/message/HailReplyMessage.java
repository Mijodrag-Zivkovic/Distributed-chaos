package servent.message;

import java.util.List;

public class HailReplyMessage extends BasicMessage{

    private List<Integer> neighbours;

    public HailReplyMessage(int senderPort, int receiverPort, List<Integer> neighbours) {
        super(MessageType.HAIL_REPLY, senderPort, receiverPort);
        this.neighbours = neighbours;
    }

    public List<Integer> getNeighbours() {
        return neighbours;
    }
}
