package java_files.persistence_layer;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java_files.Player;
import java.io.*;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class PlayerFilePersistent implements IPlayerPersistent{

    private static final Logger logger = LoggerFactory.getLogger(PlayerFilePersistent.class);
    private final String fileRegisteredPlayersPath =
            Paths.get(System.getProperty("user.dir"), "AllPlayers.csv").toString();
    private final Map<String, Player> registeredPlayers = new HashMap<>();
    private final File fileRegisteredPlayers = new File(fileRegisteredPlayersPath);
    private JDA jda;

    public PlayerFilePersistent(JDA jda) {
        this.jda = jda;
    }
    public PlayerFilePersistent(){

    }

    public Map<String, Player> readAlreadyRegisteredPlayers() {

        // will need to refactor when moving file
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileRegisteredPlayers)))) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] lineSplit = line.split(";");
                registeredPlayers.put(lineSplit[0],
                        new Player(lineSplit[0], jda.retrieveUserById(lineSplit[0]).complete().getAsTag(),
                                Double.parseDouble(lineSplit[1])));
            }
        } catch (FileNotFoundException e) {
            try {
                boolean created = new File(fileRegisteredPlayersPath).createNewFile();
                if (!created) {
                    logger.info("Filename does already exist!");
                }
            } catch (IOException | SecurityException ex) {
                logger.error(Arrays.toString(ex.getStackTrace()));
            }
        }
        return registeredPlayers;
    }

    public void registerPlayer(User user, TextChannel channel) {
        if (!registeredPlayers.containsKey(user.getId())) {
            try (
                    BufferedWriter writer = new BufferedWriter(
                            new FileWriter(fileRegisteredPlayers, true))) {
                writer.write(user.getId() + ";1000\n");
                registeredPlayers.put(user.getId(), new Player(user.getId(), user.getAsTag(), 1000));
                channel.sendMessage(user.getAsTag() + " got registered! You start with 1000 coins")
                        .queue(msg -> msg.delete().queueAfter(2, TimeUnit.SECONDS));
            } catch (IOException e) {
                logger.error(Arrays.toString(e.getStackTrace()));
            }
        } else {
            channel.sendMessage("You are already registered")
                    .queue(msg -> msg.delete().queueAfter(2, TimeUnit.SECONDS));
        }
    }

    public void writePlayersBackToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileRegisteredPlayers, false))) {
            for (Map.Entry<String, Player> entry : registeredPlayers.entrySet()) {
                writer.write(entry.getKey() + ";" + entry.getValue().getMoney() + "\n");
            }

        } catch (IOException e) {
            logger.error(Arrays.toString(e.getStackTrace()));
        }
    }

    public String readPlayerBalance(Player player){
        NumberFormat nf = new DecimalFormat("##.###");
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileRegisteredPlayers)))) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] lineSplit = line.split(";");
                if (lineSplit[0].equals(player.getUuid())){
                    return nf.format(Double.parseDouble(lineSplit[1]));
                }
            }

        }
        catch (FileNotFoundException ex){
            jda.openPrivateChannelById(player.getUuid()).queue(conn -> conn.sendMessage("Something went wrong!").queue());
            logger.error("Could not find file in path {} to read balance from player {} with uuid {}", fileRegisteredPlayersPath, player.getNameNoTag(), player.getUuid());
            logger.error("Error: {}", ex.getMessage());
            logger.error("Stacktrace: {}", Arrays.toString(ex.getStackTrace()));
        }
        return null;
    }
}
