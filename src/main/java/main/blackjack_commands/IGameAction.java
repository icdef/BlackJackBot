package main.blackjack_commands;

import main.game_control_files.PlayState;
import main.Player;
import net.dv8tion.jda.api.entities.TextChannel;

public interface IGameAction {
    PlayState handleInput(String input, Player player, TextChannel channel);
}
