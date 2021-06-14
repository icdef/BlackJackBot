package main;

import main.game_control_files.PlayState;
import main.util.Player;
import net.dv8tion.jda.api.entities.TextChannel;

public interface IGameAction {
    PlayState handleInput(String input, Player player, TextChannel channel);
}
