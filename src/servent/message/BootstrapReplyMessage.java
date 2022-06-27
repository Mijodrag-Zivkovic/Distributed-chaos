package servent.message;

public class BootstrapReplyMessage extends BasicMessage{


    private int port;

    public BootstrapReplyMessage(int senderPort, int receiverPort, int port) {
        super(MessageType.BOOTSTRAP_REPLY, senderPort, receiverPort);
        this.port=port;
    }

    public int getPort() {
        return port;
    }
}
