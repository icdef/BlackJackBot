package main.util;

import main.Main;
import main.game_control_files.PlayState;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class Clear extends ListenerAdapter {
    private PlayState playState;

    public Clear(PlayState playState) {
        this.playState = playState;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;
        if (!event.getChannel().getId().equals(Main.PLAY_CHANNEL_ID))
            return;
        String input = event.getMessage().getContentRaw();
        if (input.equals("clear") && playState == PlayState.NOT_PLAYING) {
            TextChannel channel = event.getChannel();
            channel.sendMessage("Bot is working, please wait...").complete();
            MessageHistory history = MessageHistory.getHistoryFromBeginning(channel).limit(100).complete();
            channel.deleteMessages(history.getRetrievedHistory()).queue(success -> channel.sendMessage("Messages deleted")
                    .queue(msg -> msg.delete().queueAfter(500, TimeUnit.MILLISECONDS)));

        }
    }
}
