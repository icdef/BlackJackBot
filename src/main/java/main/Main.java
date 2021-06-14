package main;

import main.game_control_files.GameActions;
import main.game_control_files.GameFlow;
import main.game_control_files.PlayState;
import main.persistence_layer.IPlayerPersistent;
import main.persistence_layer.PlayerPersistent;
import main.registration.RegisterReaction;
import main.non_blackjack_commands.Clear;
import main.non_blackjack_commands.Help;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.security.auth.login.LoginException;
import java.util.*;


public class Main {
    public static final String REGISTER_CHANNEL_ID = "851209582205468693";
    public static final String PLAY_CHANNEL_ID = "851209654146957312";

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
        if (channel == null) {
            System.out.println("Channel not found!");
            return;
        }
        MessageHistory history = new MessageHistory(channel);
        history.retrievePast(1).queue(msgList -> {
            if (msgList.isEmpty()) {
                createEmbed(jda, channel);
            }
        });
    }



    public static void main(String[] args) throws LoginException, InterruptedException {

        JDABuilder jdaBuilder = JDABuilder.createDefault("");
        JDA jda = jdaBuilder.build();
        jda.awaitReady();
        System.out.println("Bot is on");
        IPlayerPersistent playerPersistent = new PlayerPersistent(jda);
        Thread t1 = new Thread(new Shut(jda, playerPersistent));
        t1.start();
        createEmbedIfNeeded(jda);
        Set<Player> playerSet = new HashSet<>();
        PlayState playState = PlayState.NOT_PLAYING;
        GameActions gameActions = new GameActions(jda);
        jda.addEventListener(new RegisterReaction(playerPersistent),
                new GameFlow(playState, playerSet, playerPersistent, gameActions, jda), new Help(), new Clear(playState));


    }
}
