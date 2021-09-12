package text_commands;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import main.Main;

public class HelpCommandListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HelpCommandListener.class);

    /**
     * build embed with all commands regarding blackjack
     * @param jda jda from bot
     * @return MessageEmbed
     */
    private MessageEmbed commandsEmbed(JDA jda) {
        logger.trace("method call commandsEmbed with {}",jda);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(jda.getUserByTag("BlackJackBot#1745").getName(), null,
                jda.getUserByTag("BlackJackBot#1745").getAvatarUrl());
        builder.setTitle("Commands for blackjack-table");
        builder.addField("blackjack", "starts the blackjack session", false);
        builder.addField("balance", "get your current balance", false);
        builder.addField("stop",
                "stops the blackjack session and bot goes to standby. (only possible when people are able to join and leave)",
                false);
        builder.addField("join", "join the table for one or more blackjack round(s)", false);
        builder.addField("leave", "leaving the table", false);
        builder.addField("start", "when all players joined, \"start\" starts the round", false);
        builder.addField("bet <amount>", "sets your bet", false);
        builder.addField("hit", "take another card", false);
        builder.addField("stand", "do not take another card", false);
        builder.addField("double", "double your wager and draw one last card", false);
        builder.addField("split", "double your wager by playing two hands ", false);
        builder.addField("clear", "clears all messages from chat", false);
        return builder.build();

    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.getChannel().getId().equals(Main.PLAY_CHANNEL_ID)) {
            return;
        }

        String input = event.getMessage().getContentRaw();
        if (input.equals("help")) {
            event.getAuthor().openPrivateChannel().queue(
                    privateChannel -> privateChannel.sendMessageEmbeds(commandsEmbed(event.getJDA())).queue());
            logger.info("Send help message to {}",event.getAuthor().getAsTag());
        }
    }


}
