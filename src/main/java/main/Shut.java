package main;

import main.persistence_layer.IPlayerPersistent;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

    @Override
    public void run() {
        String line = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            while ((line = reader.readLine()) != null) {
                if (line.equalsIgnoreCase("exit")) {
                    jda.shutdown();
                    logger.info("Bot is off");
                    reader.close();
                    break;
                } else {
                    logger.warn("Use 'exit' to shutdown the bot");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        playerPersistent.writePlayersBackToFile();

        System.exit(0);
    }
}
