package main.registration;

import main.persistence_layer.IPlayerPersistent;
import main.persistence_layer.PlayerPersistent;
import main.Main;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class RegisterReactionListener extends ListenerAdapter {

   private IPlayerPersistent playerPersistent;

    public RegisterReactionListener(IPlayerPersistent playerPersistent) {
        this.playerPersistent = playerPersistent;
    }



    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.getUser().isBot())
            return;
        if (!event.getTextChannel().getId().equals(Main.REGISTER_CHANNEL_ID))
            return;
        if (event.getReaction().getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+2705")) {
            // retrieving needed cause otherwise caching issues possible
            event.retrieveUser().queue(user -> playerPersistent.registerPlayer(user, event.getTextChannel()));
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
            event.retrieveUser().queue(user -> playerPersistent.registerPlayer(user, event.getTextChannel()));

        }


    }
}
