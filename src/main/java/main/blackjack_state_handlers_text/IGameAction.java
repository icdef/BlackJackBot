package main.blackjack_state_handlers_text;

import main.Player;
import main.game_control_files.PlayState;
import net.dv8tion.jda.api.entities.TextChannel;

public interface IGameAction {
    /**
     * Depending on current play state the input gets processed and the next play state gets returned
     *
     * @param input   command from player
     * @param player  player who wrote the command
     * @param channel textchannel where the command was written
     * @return the play state after the command was executed
     */
    PlayState handleInput(String input, Player player, TextChannel channel);
}
