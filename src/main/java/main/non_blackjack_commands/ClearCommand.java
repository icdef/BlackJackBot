package main.non_blackjack_commands;
;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClearCommand {


   public void clearChannel (String input, TextChannel channel){
       if (input.equals("clear")) {
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
       }

   }
}
