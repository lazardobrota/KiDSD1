package cli.command;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import servent.message.AskListFilesMessage;
import servent.message.util.MessageUtil;

public class ListFilesCommand implements CLICommand{
    @Override
    public String commandName() {
        return "list_files";
    }

    @Override
    public void execute(String args) {
        if (args.isEmpty()) {
            AppConfig.timestampedErrorPrint("Invalid arguments for put");
            return;
        }

        try {
            int port = Integer.parseInt(args.split(" ")[0]);
            int chordId = ChordState.chordHash(port);

            if (AppConfig.myServentInfo.getChordId() == chordId)
                AppConfig.chordState.getUploadListOfPaths(AppConfig.myServentInfo.getListenerPort());
            else if (!AppConfig.chordState.isKeyMine(chordId)) {
                ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(chordId);
                MessageUtil.sendMessage(new AskListFilesMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), String.valueOf(chordId)));
            }
            else {
                AppConfig.timestampedErrorPrint("ASK_LIST_FILES Invalid port");
            }

        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Invalid key and value pair. Both should be ints. 0 <= key <= " + ChordState.CHORD_SIZE
                    + ". 0 <= value.");
        }
    }
}
