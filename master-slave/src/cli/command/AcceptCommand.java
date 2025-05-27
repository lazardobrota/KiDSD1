package cli.command;

import app.AppConfig;
import app.ChordState;

public class AcceptCommand implements CLICommand{
    @Override
    public String commandName() {
        return "accept";
    }

    @Override
    public void execute(String args) {
        if (args.isEmpty()) {
            AppConfig.timestampedErrorPrint("Invalid arguments for " + commandName());
            return;
        }

        try {
            int port = Integer.parseInt(args.split(" ")[0]);
            if (!AppConfig.chordState.getPendingFollowRequests().contains(port)) {
                AppConfig.timestampedStandardPrint("Node doesn't have given port in pending state");
            }
            else {
                AppConfig.chordState.getPendingFollowRequests().remove(port);
                AppConfig.chordState.getAcceptedFollows().add(port);
                AppConfig.timestampedStandardPrint("Accepted given port");
            }
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Invalid key and value pair. Both should be ints. 0 <= key <= " + ChordState.CHORD_SIZE
                    + ". 0 <= value.");
        }
    }
}
