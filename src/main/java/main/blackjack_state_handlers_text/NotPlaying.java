package main.blackjack_state_handlers_text;

import main.Player;
import main.game_control_files.PlayState;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.concurrent.TimeUnit;

public class NotPlaying implements IGameAction {


    @Override
    public PlayState handleInput(String input, Player player, TextChannel channel) {
        if (input.equals("blackjack")) {
            channel.sendMessage(
                    "BlackJack game started. Player can type join to join the game. Type start to start the round")
                    .setActionRow(Button.success("join","Click to join table"))
                    .queue();
            return PlayState.CHOOSING_PLAYER;
        }

        return PlayState.NOT_PLAYING;
    }
}
