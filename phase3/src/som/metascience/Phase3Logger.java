package som.metascience;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Basic logger for phase 3
 */
public class Phase3Logger {
    public static final String LOG_FILE = "phase3/phase3.log";

    FileOutputStream log;

    /**
     * Inits the streams to log
     */
    public Phase3Logger() {
        try {
            log = new FileOutputStream(new File(LOG_FILE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs a message
     * @param message The message to be logged
     */
    public void log(String message) {
        try {
            log.write(message.getBytes());
            log.write('\n');
            log.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
