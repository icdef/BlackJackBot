package java_files;

import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java_files.persistence_layer.IPlayerPersistent;
import java.util.Arrays;

/**
 * Thread which listens to System.in and shuts down the bot when writing 'exit' into console
 */
public class Shut implements Runnable {
    private final JDA jda;
    private final IPlayerPersistent playerPersistent;
    private static final Logger logger = LoggerFactory.getLogger(Shut.class);
    public Shut(JDA jda, IPlayerPersistent playerPersistent) {
        this.jda = jda;
        this.playerPersistent = playerPersistent;
    }

    /**
     * when writing exit into console the bot shuts down
     */
    @Override
    public void run() {
        String line = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            while ((line = reader.readLine()) != null) {
                if (line.equalsIgnoreCase("exit")) {
                    jda.shutdown();
                    System.out.println("Bot is off");
                    reader.close();
                    break;
                } else {
                    System.out.println("Write exit to shutdown the bot");
                }
            }
        } catch (IOException e) {
            logger.error(Arrays.toString(e.getStackTrace()));
        }
        playerPersistent.writePlayersBackToFile();

        System.exit(0);
    }
}
