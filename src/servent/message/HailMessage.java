package servent.message;

public class HailMessage extends BasicMessage{

    public HailMessage(int senderPort, int receiverPort) {
        super(MessageType.HAIL, senderPort, receiverPort);
    }
}
