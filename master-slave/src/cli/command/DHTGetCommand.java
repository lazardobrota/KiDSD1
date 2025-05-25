package cli.command;

import app.AppConfig;
import cli.ValueTypes;

public class DHTGetCommand implements CLICommand {

	@Override
	public String commandName() {
		return "dht_get";
	}

	@Override
	public void execute(String args) {
		try {
			int key = Integer.parseInt(args);
			
			String val = AppConfig.chordState.getValue(key);
			
			if (val.equals(ValueTypes.ASKED_ANOTHER_NODE.toString())) {
				AppConfig.timestampedStandardPrint("Please wait...");
			} else if (val.equals(ValueTypes.EMPTY.toString())) {
				AppConfig.timestampedStandardPrint("No such key: " + key);
			} else {
				AppConfig.timestampedStandardPrint(key + ": " + val);
			}
		} catch (NumberFormatException e) {
			AppConfig.timestampedErrorPrint("Invalid argument for dht_get: " + args + ". Should be key, which is an int.");
		}
	}

}
