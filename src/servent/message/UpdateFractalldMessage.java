package servent.message;

public class UpdateFractalldMessage extends BasicMessage{

    private int index;
    private String value;
    boolean lastInLvl;
    boolean lastInSubFractal;

    public UpdateFractalldMessage(int senderPort, int receiverPort, int index, String value, boolean lastInLvl, boolean lastInSubFractal) {

        super(MessageType.UPDATE_FRACTAL_ID, senderPort, receiverPort);
        this.index = index;
        this.value = value;
        this.lastInLvl = lastInLvl;
        this.lastInSubFractal = lastInSubFractal;
    }

    public int getIndex() {
        return index;
    }

    public String getValue() {
        return value;
    }

    public boolean isLastInLvl() {
        return lastInLvl;
    }

    public boolean isLastInSubFractal() {
        return lastInSubFractal;
    }
}
