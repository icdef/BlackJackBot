package main.blackjack_state_handlers_button;

import main.Player;
import main.game_control_files.PlayState;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;


public interface IGameActionButtonHandler {

    PlayState handleInput(String input, Player player, ButtonClickEvent event);
}
