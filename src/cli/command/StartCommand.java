package cli.command;

public class StartCommand implements CLICommand{

    private Thread drawThread;

    public StartCommand(Thread drawThread) {
        this.drawThread = drawThread;
    }

    @Override
    public String commandName() {
        return "start";
    }

    @Override
    public void execute(String args) {
        drawThread.start();
    }
}
