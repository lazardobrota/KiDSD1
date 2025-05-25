package cli.command;

import app.AppConfig;
import app.ChordState;

public class UploadFileCommand implements CLICommand{

    @Override
    public String commandName() {
        return "upload";
    }

    @Override
    public void execute(String args) {

        if (args.isEmpty()) {
            AppConfig.timestampedErrorPrint("Invalid arguments for put");
            return;
        }

        try {
            String path = args.split(" ")[0];
            int keyOfHashedPath = AppConfig.chordState.chordHash(path);

            if (keyOfHashedPath < 0 || keyOfHashedPath >= ChordState.CHORD_SIZE) {
                throw new NumberFormatException();
            }

            AppConfig.chordState.getUploadsThroughMe().add(keyOfHashedPath);
            AppConfig.chordState.putValue(keyOfHashedPath, path);
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Invalid key and value pair. Both should be ints. 0 <= key <= " + ChordState.CHORD_SIZE
                    + ". 0 <= value.");
        }
    }

}
