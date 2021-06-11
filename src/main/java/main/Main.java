package main;

import main.game_control_files.GameActions;
import main.game_control_files.GameFlow;
import main.game_control_files.PlayState;
import main.registration.RegisterReaction;
import main.util.Clear;
import main.util.Help;
import main.util.Player;
import main.util.Shut;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;


public class Main {
    public static final String REGISTER_CHANNEL_ID = "851209582205468693";
    public static final String PLAY_CHANNEL_ID = "851209654146957312";
    public static final String FILE_REGISTERED_PLAYERS_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + File.separator + "java" + File.separator + "main" + File.separator + "AllPlayers.csv";
    private static final Map<String, Player> registeredPlayers = new HashMap<>();

    private static void createEmbed(JDA jda, TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Let's Play BlackJack");
        builder.addField("How?", "If you wanna play blackjack you need to be registered!\n React with the :white_check_mark: emote to register.", false);
        builder.setAuthor(jda.getUserByTag("BlackJackBot#1745").getName(), null, jda.getUserByTag("BlackJackBot#1745").getAvatarUrl());
        channel.sendMessage(builder.build()).queue(msg -> msg.addReaction("U+2705").queue());
    }

    /**
     * when register embed got deleted, create new one. Only works when the channel has no messages in them
     */
    private static void createEmbedIfNeeded(JDA jda) {
        TextChannel channel = jda.getTextChannelById(REGISTER_CHANNEL_ID);
        assert channel != null;
        MessageHistory history = new MessageHistory(channel);
        history.retrievePast(1).queue(msgList -> {
            if (msgList.isEmpty()) {
                createEmbed(jda, channel);
            }
        });
    }

    /**
     * reads the input file and returns a map with key: userName and value: Player instance with balance and name from file
     *
     * @param fileRegisteredPlayers file with registered people in from of playername;balance
     * @return Map with Key=UserName Value=Player instance with name and balance from file
     */
    private static Map<String, Player> readAlreadyRegisteredPlayers(File fileRegisteredPlayers) {

        // will need to refactor when moving file
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileRegisteredPlayers)))) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] lineSplitted = line.split(";");
                registeredPlayers.put(lineSplitted[0], new Player(lineSplitted[0], Double.parseDouble(lineSplitted[1])));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return registeredPlayers;
    }

    public static void main(String[] args) throws LoginException, InterruptedException {

        File fileRegisteredPlayers = new File(FILE_REGISTERED_PLAYERS_PATH);
        JDABuilder jdaBuilder = JDABuilder.createDefault("");
        JDA jda = jdaBuilder.build();
        jda.awaitReady();
        System.out.println("Bot is on");
        Thread t1 = new Thread(new Shut(jda, fileRegisteredPlayers, registeredPlayers));
        t1.start();
        createEmbedIfNeeded(jda);
        Map<String, Player> alreadyRegisteredPlayers = readAlreadyRegisteredPlayers(fileRegisteredPlayers);
        Set<Player> playerSet = new HashSet<>();
        PlayState playState = PlayState.NOT_PLAYING;
        GameActions gameActions = new GameActions(jda);
        jda.addEventListener(new RegisterReaction(alreadyRegisteredPlayers, fileRegisteredPlayers),
                new GameFlow(playState, playerSet, alreadyRegisteredPlayers, gameActions), new Help(), new Clear(playState));


    }
}
