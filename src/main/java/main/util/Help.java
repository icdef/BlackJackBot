package main.util;

import main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Help extends ListenerAdapter {

    private MessageEmbed commandsEmbed(JDA jda) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(jda.getUserByTag("BlackJackBot#1745").getName(), null, jda.getUserByTag("BlackJackBot#1745").getAvatarUrl());
        builder.setTitle("Commands");
        builder.addField("blackjack", "starts the blackjack session", false);
        builder.addField("quit", "stops the blackjack session. Only possible when you joined the table", false);
        builder.addField("join", "join the table for one(?) blackjack round", false);
        builder.addField("leave", "leaves the table", false);
        builder.addField("start", "when all players joined, \"start\" starts the round", false);
        builder.addField("bet <amount>", "sets your bet", false);
        builder.addField("hit", "you hit (blackjack term)", false);
        builder.addField("stand", "you stand (blackjack term)", false);
        builder.addField("double", "you double (blackjack term)", false);
        builder.addField("split", "you split (blackjack term)", false);
        return builder.build();

    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;
        if (!event.getChannel().getId().equals(Main.PLAY_CHANNEL_ID))
            return;

        String input = event.getMessage().getContentRaw();
        if (input.equals("help")) {
            event.getChannel().sendMessage(commandsEmbed(event.getJDA())).queue();
        }
    }
}
