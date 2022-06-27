package servent.message;

public class NewNodeMessage extends BasicMessage{

    private int newNodePort;

    public NewNodeMessage(int senderPort, int receiverPort, int newNodePort) {
        super(MessageType.NEW_NODE, senderPort, receiverPort);
        this.newNodePort = newNodePort;
    }

    public int getNewNodePort() {
        return newNodePort;
    }
}
