package cli.command;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import servent.message.FollowMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class FollowCommand implements CLICommand{
    @Override
    public String commandName() {
        return "follow";
    }

    @Override
    public void execute(String args) {
        if (args.isEmpty()) {
            AppConfig.timestampedErrorPrint("Invalid arguments for " + commandName());
            return;
        }

        try {
            int port = Integer.parseInt(args.split(" ")[0]);
            int chordId = ChordState.chordHash(port);

            if (chordId < 0 || chordId >= ChordState.CHORD_SIZE) {
                throw new NumberFormatException();
            }

            if (chordId == AppConfig.myServentInfo.getChordId()) {
                AppConfig.timestampedErrorPrint("Can't follow itself");
                return;
            }

            AppConfig.chordState.followNode(chordId);

        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Invalid key and value pair. Both should be ints. 0 <= key <= " + ChordState.CHORD_SIZE
                    + ". 0 <= value.");
        }
    }
}
