package servent.message;

public class CheckNodeMessage extends BasicMessage{


    private boolean helper;
    private int suspiciousPort;

    public CheckNodeMessage(int senderPort, int receiverPort,boolean helper, int suspiciousPort) {
        super(MessageType.CHECK_NODE, senderPort, receiverPort);
        this.helper = helper;
        this.suspiciousPort = suspiciousPort;
    }

    public boolean isHelper() {
        return helper;
    }

    public int getSuspiciousPort() {
        return suspiciousPort;
    }
}
