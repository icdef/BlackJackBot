package registration;



import main.Main;
import persistence_layer.IPlayerPersistent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterReactionListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RegisterReactionListener.class);
    private static final String REACTION_EMOTE_UNI_CODE = "U+2705";
    private final IPlayerPersistent playerPersistent;

    public RegisterReactionListener(IPlayerPersistent playerPersistent) {
        this.playerPersistent = playerPersistent;
    }


    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.retrieveUser().complete().isBot()) {
            return;
        }
        if (!event.getTextChannel().getId().equals(Main.REGISTER_CHANNEL_ID)) {
            return;
        }
        if (event.getReaction().getReactionEmote().getAsCodepoints()
                .equalsIgnoreCase(REACTION_EMOTE_UNI_CODE)) {
            // retrieving needed cause otherwise caching issues possible
            event.retrieveUser()
                    .queue(user -> {
                        playerPersistent.registerPlayer(user, event.getTextChannel());
                        logger.info("User {} registered", user);
                    });
        }


    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        if (event.retrieveUser().complete().isBot()) {
            return;
        }
        if (!event.getTextChannel().getId().equals(Main.REGISTER_CHANNEL_ID)) {
            return;
        }
        if (event.getReaction().getReactionEmote().getAsCodepoints()
                .equalsIgnoreCase(REACTION_EMOTE_UNI_CODE)) {
            // retrieving needed cause otherwise caching issues possible
            event.retrieveUser()
                    .queue(user -> {
                        playerPersistent.registerPlayer(user, event.getTextChannel());
                        logger.info("User {} registered", user);
                    });

        }


    }
}
