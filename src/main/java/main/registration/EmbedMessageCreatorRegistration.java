package main.registration;

import main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbedMessageCreatorRegistration {

    private static final Logger logger = LoggerFactory.getLogger(EmbedMessageCreatorRegistration.class);
    private final JDA jda;

    public EmbedMessageCreatorRegistration(JDA jda) {
        this.jda = jda;
    }

    /**
     * creates the register embed in @param channel
     * @param channel channel the embed is send to
     */
    private void createEmbed(TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Let's Play BlackJack");
        builder.addField("How?",
                "If you wanna play blackjack you need to be registered!\n React with the :white_check_mark: emote to register.",
                false);
        builder.setAuthor(jda.getUserByTag("BlackJackBot#1745").getName(), null,
                jda.getUserByTag("BlackJackBot#1745").getAvatarUrl());
        channel.sendMessageEmbeds(builder.build()).queue(msg -> msg.addReaction("U+2705").queue());
    }

    /**
     * when register embed got deleted, create new one. Only works when there are no messages in the channel
     */
    public void createRegisterEmbedIfNeeded() {
        TextChannel channel = jda.getTextChannelById(Main.REGISTER_CHANNEL_ID);
        if (channel == null) {
           logger.error("TextChannel with id {} not found. ID should be equal to the registration channel",Main.REGISTER_CHANNEL_ID);
           jda.shutdown();
           return;
        }

        MessageHistory history = new MessageHistory(channel);
        history.retrievePast(1).queue(msgList -> {
            if (msgList.isEmpty()) {
                this.createEmbed(channel);
            }
        });
    }
}
