package main.persistence_layer;

import main.Player;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class PlayerPersistent implements IPlayerPersistent{

    private final String fileRegisteredPlayersPath = Paths.get(System.getProperty("user.dir"),"AllPlayers.csv").toString();
    private final Map<String, Player> registeredPlayers = new HashMap<>();
    private final File fileRegisteredPlayers = new File(fileRegisteredPlayersPath);
    private final JDA jda;

    public PlayerPersistent(JDA jda) {
        this.jda = jda;
    }

    public Map<String, Player> readAlreadyRegisteredPlayers() {

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