package blackjack_state_handlers_button;


import game_control_files.PlayState;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import player_entity.Player;


public interface IGameActionButtonHandler {

    PlayState handleInput(String input, Player player, ButtonClickEvent event);
}
