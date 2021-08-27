package text_commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClearCommand {

    private static final Logger logger = LoggerFactory.getLogger(ClearCommand.class);

    /**
     * clears all messages from channel
     * @param channel messages get deleted from this channel
     */
   public void clearChannel(TextChannel channel){
           List<Message> channelMessages;
           MessageHistory history = new MessageHistory(channel);
           channel.sendMessage("Bot is working, please wait...").complete();
           do {
               channelMessages = history.retrievePast(100).complete();
               channel.purgeMessages(channelMessages);
           }
           while (!channelMessages.isEmpty());
           channel.sendMessage("Task done. Thanks for waiting")
                   .queueAfter(1, TimeUnit.SECONDS, m -> m.delete().queueAfter(500, TimeUnit.MILLISECONDS));
           logger.info("Deleted messages in channel {} with id {}", channel, channel.getId());


   }
}
