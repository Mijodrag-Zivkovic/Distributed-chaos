package servent.message;

public class CheckNodeReply extends BasicMessage{

    boolean suspicious;

    public CheckNodeReply( int senderPort, int receiverPort, boolean suspicious) {
        super(MessageType.CHECK_NODE_REPLY, senderPort, receiverPort);
        this.suspicious = suspicious;
    }

    public boolean isSuspicious() {
        return suspicious;
    }
}
