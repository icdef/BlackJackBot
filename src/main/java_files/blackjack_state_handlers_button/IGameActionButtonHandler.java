package java_files.blackjack_state_handlers_button;


import java_files.game_control_files.PlayState;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java_files.Player;


public interface IGameActionButtonHandler {

    PlayState handleInput(String input, Player player, ButtonClickEvent event);
}
