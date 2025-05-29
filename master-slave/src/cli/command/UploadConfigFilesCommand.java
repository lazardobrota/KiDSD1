package cli.command;

import app.AppConfig;

public class UploadConfigFilesCommand implements CLICommand{

    @Override
    public String commandName() {
        return "upload_config";
    }

    @Override
    public void execute(String args) {
        UploadFileCommand uploadFileCommand = new UploadFileCommand();
        for (String imagePath : AppConfig.myServentStartUploadList)
            uploadFileCommand.execute(imagePath);
    }

}
