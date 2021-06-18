package main.blackjack_state_handlers_buttons;

import main.Player;
import main.game_control_files.PlayState;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ChoosingPlayerButtonHandler implements IGameActionButton{

    Set<Player> playerSet;

    public ChoosingPlayerButtonHandler(Set<Player> playerSet) {
        this.playerSet = playerSet;
    }

    @Override
    public PlayState handleInput(String input, Player player, ButtonClickEvent event) {
        if (input.equals("join")) {
            if (player == null) {
                event.editMessage("Please register yourself first").
                        setActionRow(Button.primary("join","Click to join the game"),
                                Button.primary("start","Click to start the round"),
                                Button.primary("leave","Leave table"),
                                Button.primary("quit","Bot goes into standby"))
                        .queue();
            } else if (playerSet.contains(player)) {
                event.editMessage("You already joined the table").
                        setActionRow(Button.primary("join","Click to join the game"),
                                Button.primary("start","Click to start the round"),
                                Button.primary("leave","Leave table"),
                                Button.primary("quit","Bot goes into standby"))
                        .queue();
            } else {
                playerSet.add(player);
                event.editMessage(player.getNameNoTag()+" joined the table").
                        setActionRow(Button.primary("join","Click to join the game"),
                                Button.primary("start","Click to start the round"),
                                Button.primary("leave","Leave table"),
                                Button.primary("quit","Bot goes into standby"))
                        .queue();
            }
            event.getHook().editOriginal(("Press a button to continue")).completeAfter(1, TimeUnit.SECONDS);
        }

        if (input.equals("leave")) {
            if (playerSet.remove(player)) {
                event.editMessage("You left the table").
                        setActionRow(Button.primary("join","Click to join the game"),
                                Button.primary("start","Click to start the round"),
                                Button.primary("leave","Leave table"),
                                Button.primary("quit","Bot goes into standby"))
                        .queue();
            } else {
                event.editMessage("You did not join the table").
                        setActionRow(Button.primary("join","Click to join the game"),
                                Button.primary("start","Click to start the round"),
                                Button.primary("leave","Leave table"),
                                Button.primary("quit","Bot goes into standby"))
                        .queue();
            }
            event.getHook().editOriginal(("Press a button to continue")).completeAfter(1, TimeUnit.SECONDS);
        }


        if (input.equals("start")) {
            if (playerSet.isEmpty()) {
                event.editMessage("No players have joined yet").
                        setActionRow(Button.primary("join","Click to join the game"),
                                Button.primary("start","Click to start the round"),
                                Button.primary("leave","Leave table"),
                                Button.primary("quit","Bot goes into standby"))
                        .queue();
            } else if (!playerSet.contains(player)) {
                event.editMessage("You are not part of the game").
                        setActionRow(Button.primary("join","Click to join the game"),
                                Button.primary("start","Click to start the round"),
                                Button.primary("leave","Leave table"),
                                Button.primary("quit","Bot goes into standby"))
                        .queue();
            } else {
                event.editMessage("Enter bet").
                        setActionRow(Button.primary("minus","-"),Button.success("amount","100"),Button.primary("plus","+")).queue();
                return PlayState.BETTING;
            }
        }

        if (input.equals("quit")) {
            playerSet.clear();
            event.getTextChannel().deleteMessageById(event.getMessageId()).queue();
            event.getTextChannel().sendMessage("Session over! Bot is in standby").queue();
            return PlayState.NOT_PLAYING;
        }
        return PlayState.CHOOSING_PLAYER;
    }

}
