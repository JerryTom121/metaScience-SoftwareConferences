package som.metascience;

import java.io.*;

/**
 * Logs messages from Phase 2 and controls the last file analyzed (to allow recovering from errors)
 */
public class Phase2Logger {
    public static final String LOG_FILE = "phase2/phase2.log";
    public static final String CONTROL_FILE = "phase2/phase2_control.log";

    FileOutputStream log;
    FileOutputStream control;

    /**
     * Inits the streams to log and control. Note that if there is a control file, the variable is NOT created, thus
     * the developer has to check such file {@link Phase2Logger.lastControl()}
     */
    public Phase2Logger() {
        try {
            log = new FileOutputStream(new File(LOG_FILE));
            if(!(new File(CONTROL_FILE)).exists())
                control = new FileOutputStream(new File(CONTROL_FILE));
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

    /**
     * Registers the last file considered
     *
     * If the control file does not exist, an exception will be thrown
     *
     * @param name The name of the file
     */
    public void control(String name) {
        if(control == null)
            throw new IllegalStateException("There is a control file and has to be taken into consideration");

        try {
            control.write(name.getBytes());
            control.write('\n');
            control.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the last list of the control file
     */
    public String lastControl() {
        if(!(new File(CONTROL_FILE).exists()))
            return null;

        try {
            FileInputStream in = new FileInputStream(CONTROL_FILE);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String lastLine = null, tmp;
            while ((tmp = br.readLine()) != null) {
                lastLine = tmp;
            }

            // Resetting the variable
            control = new FileOutputStream(new File(CONTROL_FILE));
            return lastLine;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
