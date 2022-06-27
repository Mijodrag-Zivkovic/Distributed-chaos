package cli.command;

import app.AppConfig;
import app.QuitDetectionWorker;

public class CheckCommand implements CLICommand{
    @Override
    public String commandName() {
        return "check";
    }

    @Override
    public void execute(String args) {

        int suspiciousPort;
        try {
            suspiciousPort = Integer.parseInt(args);

            if (suspiciousPort < 0) {
                throw new NumberFormatException();
            }


            synchronized (AppConfig.suspiciousLock)
            {
                AppConfig.suspectorPort = AppConfig.myServentInfo.getListenerPort();
                AppConfig.suspiciousPort = suspiciousPort;
            }
            QuitDetectionWorker quitDetectionWorker = new QuitDetectionWorker();
            Thread t = new Thread(quitDetectionWorker);
            t.start();
            //posalji check poruku sumnjivom cvoru
            //uspavaj se na soft sekundi
            //nakon toga posalji poruku nekom cvoru,
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Check komanda ima los argument");
        }
    }
}
