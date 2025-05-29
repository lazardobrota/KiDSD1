package cli.command;

import app.AppConfig;
import app.ChordState;

public class VisibilityCommand implements CLICommand{

    @Override
    public String commandName() {
        return "visibility";
    }

    @Override
    public void execute(String args) {

        if (args.isEmpty()) {
            AppConfig.timestampedErrorPrint("Invalid arguments for " + commandName());
            return;
        }

        String vis = args.split(" ")[0];

        if (vis.equalsIgnoreCase("public"))
            AppConfig.chordState.setNodePublic(true);
        else if (vis.equalsIgnoreCase("private"))
            AppConfig.chordState.setNodePublic(false);
        else
            AppConfig.timestampedErrorPrint("Invalid arguments for " + commandName());
    }

}
