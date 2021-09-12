package blackjack_state_handlers_button;


import game_control_files.PlayState;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import player_entity.Player;


public interface IGameActionButtonHandler {

    /**
     * Depending on current play state the input gets processed and the next play state gets returned
     *@param input command from player
     * @param player player who wrote the command
     * @param event the button event which was triggered by the player
     * @return the play state after the command was executed
     */
    PlayState handleInput(String input, Player player, ButtonClickEvent event);
}
