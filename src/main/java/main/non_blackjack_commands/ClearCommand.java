package main.non_blackjack_commands;
;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class ClearCommand {


   public void clearChannel (String input, TextChannel channel){
       if (input.equals("clear")) {
           MessageHistory history = new MessageHistory(channel);
           channel.sendMessage("Bot is working, please wait...").complete();
           history.retrievePast(100).complete();
           while (history.getRetrievedHistory().size() > 1) {
               channel.deleteMessages(history.getRetrievedHistory()).complete();
               channel.sendMessage("Bot is working, please wait...").complete();
               history = new MessageHistory(channel);
               history.retrievePast(100).complete();
           }
           // last Bot is working message needs to be deleted as well
           channel.deleteMessageById(history.getRetrievedHistory().get(0).getId()).queue();
           channel.sendMessage("Messages deleted")
                   .queueAfter(1, TimeUnit.SECONDS, m -> m.delete().queueAfter(500, TimeUnit.MILLISECONDS));
       }

   }
}
