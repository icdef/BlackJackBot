package main;

import main.persistence_layer.IPlayerPersistent;
import net.dv8tion.jda.api.JDA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Thread which listens to System.in and shuts down the bot when writing 'exit' into console
 */
public class Shut implements Runnable {
    private JDA jda;
    private IPlayerPersistent playerPersistent;

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
                    System.out.println("Bot is off");
                    reader.close();
                    break;
                } else {
                    System.out.println("Use 'exit' to shutdown");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        playerPersistent.writePlayersBackToFile();

        System.exit(0);
    }
}
