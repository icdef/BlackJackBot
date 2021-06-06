package main.registration;

import main.Main;
import main.util.Player;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RegisterReaction extends ListenerAdapter {
    private final Map<String, Player> registeredPlayers;
    private final File fileRegisteredPlayers;

    public RegisterReaction(Map<String, Player> registeredPlayers, File fileRegisteredPlayers) {
        this.registeredPlayers = registeredPlayers;
        this.fileRegisteredPlayers = fileRegisteredPlayers;
    }

    /**
     * Registers a player for the BlackJack game. Prints the feedback of the registration to the channel.
     *
     * @param userName name of the new Player
     * @param channel  text channel where the answer is written to
     */
    private void registerPlayer(String userName, TextChannel channel) {
        if (!registeredPlayers.containsKey(userName)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileRegisteredPlayers, true))) {
                writer.write(userName + ";1000\n");
                registeredPlayers.put(userName, new Player(userName, 1000));
                channel.sendMessage(userName + " got registered! You start with 1000 coins").queue(msg -> msg.delete().queueAfter(2, TimeUnit.SECONDS));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            channel.sendMessage("You are already registered").queue(msg -> msg.delete().queueAfter(2, TimeUnit.SECONDS));
    }


    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.getUser().isBot())
            return;
        if (!event.getTextChannel().getId().equals(Main.REGISTER_CHANNEL_ID))
            return;
        if (event.getReaction().getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+2705")) {
            // retrieving needed cause otherwise caching issues possible
            event.retrieveUser().queue(user -> registerPlayer(user.getAsTag(), event.getTextChannel()));
        }


    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        if (event.getUser().isBot())
            return;
        if (!event.getTextChannel().getId().equals(Main.REGISTER_CHANNEL_ID))
            return;
        if (event.getReaction().getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+2705")) {
            // retrieving needed cause otherwise caching issues possible
            event.retrieveUser().queue(user -> registerPlayer(user.getAsTag(), event.getTextChannel()));

        }


    }
}
