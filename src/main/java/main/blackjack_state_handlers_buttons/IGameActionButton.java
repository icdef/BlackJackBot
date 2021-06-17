package main.blackjack_state_handlers_buttons;

import main.Player;
import main.game_control_files.PlayState;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;


public interface IGameActionButton {

    PlayState handleInput(String input, Player player, ButtonClickEvent event);
}
