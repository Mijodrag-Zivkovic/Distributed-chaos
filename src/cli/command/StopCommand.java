package cli.command;

import app.AppConfig;
import app.DrawWorker;
import cli.CLIParser;
import servent.SimpleServentListener;

public class StopCommand implements CLICommand {

	private CLIParser parser;
	private SimpleServentListener listener;
	private DrawWorker drawWorker;
	
	public StopCommand(CLIParser parser, SimpleServentListener listener, DrawWorker drawWorker) {
		this.parser = parser;
		this.listener = listener;
		this.drawWorker = drawWorker;
	}
	
	@Override
	public String commandName() {
		return "stop";
	}

	@Override
	public void execute(String args) {
		AppConfig.timestampedStandardPrint("Stopping...");
		parser.stop();
		listener.stop();
		drawWorker.stop();
	}

}
