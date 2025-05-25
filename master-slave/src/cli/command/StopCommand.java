package cli.command;

import app.AppConfig;
import cli.CLIParser;
import servent.PongListener;
import servent.SimpleServentListener;

public class StopCommand implements CLICommand {

	private CLIParser parser;
	private SimpleServentListener listener;
	private PongListener pongListener;

	public StopCommand(CLIParser parser, SimpleServentListener listener, PongListener pongListener) {
		this.parser = parser;
		this.listener = listener;
		this.pongListener = pongListener;
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
		pongListener.stop();
	}

}
