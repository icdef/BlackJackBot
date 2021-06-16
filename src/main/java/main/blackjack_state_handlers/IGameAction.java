package main.blackjack_state_handlers;

import main.Player;
import main.game_control_files.PlayState;
import net.dv8tion.jda.api.entities.TextChannel;

public interface IGameAction {
    PlayState handleInput(String input, Player player, TextChannel channel);
}
