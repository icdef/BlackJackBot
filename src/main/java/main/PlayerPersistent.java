package main;

import main.util.Player;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class PlayerPersistent {

    private final String fileRegisteredPlayersPath = System.getProperty("user.dir") + File.separator + "AllPlayers.csv";
    private final Map<String, Player> registeredPlayers = new HashMap<>();
    private final File fileRegisteredPlayers = new File(fileRegisteredPlayersPath);
    private final JDA jda;

    public PlayerPersistent(JDA jda) {
        this.jda = jda;
    }


    /**
     * reads the input file and returns a map with key: userName and value: Player instance with balance and name from file
     *
     * @return Map with Key=UserName Value=Player instance with name and balance from file
     */
    public Map<String, Player>readAlreadyRegisteredPlayers() {

        // will need to refactor when moving file
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileRegisteredPlayers)))) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] lineSplitted = line.split(";");
                registeredPlayers.put(lineSplitted[0], new Player(lineSplitted[0],jda.retrieveUserById(lineSplitted[0]).complete().getAsTag(), Double.parseDouble(lineSplitted[1])));
            }
        } catch (FileNotFoundException e) {
            try {
                boolean created = new File(fileRegisteredPlayersPath).createNewFile();
                if (!created) {
                    System.out.println("File name already exists");
                }
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }
        return registeredPlayers;
    }
    /**
     * Registers a player for the BlackJack game. Prints the feedback of the registration to the channel.
     *
     * @param user the new Player
     * @param channel  text channel where the answer is written to
     */
    public void registerPlayer(User user, TextChannel channel) {
        if (!registeredPlayers.containsKey(user.getId())) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileRegisteredPlayers, true))) {
                writer.write(user.getId() + ";1000\n");
                registeredPlayers.put(user.getId(), new Player(user.getId(),user.getAsTag(), 1000));
                channel.sendMessage(user.getAsTag() + " got registered! You start with 1000 coins").queue(msg -> msg.delete().queueAfter(2, TimeUnit.SECONDS));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            channel.sendMessage("You are already registered").queue(msg -> msg.delete().queueAfter(2, TimeUnit.SECONDS));
    }

    public void writePlayersBackToFile(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileRegisteredPlayers, false))) {
            for (Map.Entry<String, Player> entry : registeredPlayers.entrySet()) {
                writer.write(entry.getKey() + ";" + entry.getValue().getMoney() + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
