package main.util;

import net.dv8tion.jda.api.JDA;

import java.io.*;
import java.util.Map;

/**
 * Thread which listens to System.in and shuts down the bot when writing 'exit' into console
 */
public class Shut implements Runnable {
    private JDA jda;
    private File fileRegisteredPlayers;
    private Map<String, Player> registeredPlayers;

    public Shut(JDA jda, File fileRegisteredPlayers,Map<String,Player> registeredPlayers) {
        this.jda = jda;
        this.fileRegisteredPlayers = fileRegisteredPlayers;
        this.registeredPlayers = registeredPlayers;
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileRegisteredPlayers, false))) {
            for (Map.Entry<String, Player> entry : registeredPlayers.entrySet()) {
                writer.write(entry.getKey()+";"+entry.getValue().getMoney()+"\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
