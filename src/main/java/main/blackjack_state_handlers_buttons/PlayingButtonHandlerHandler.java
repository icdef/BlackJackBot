package main.blackjack_state_handlers_buttons;

import main.Player;
import main.game_control_files.GameActionsButton;
import main.game_control_files.PlayState;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class PlayingButtonHandlerHandler implements IGameActionButtonHandler {

    private final GameActionsButton gameActions;

    public PlayingButtonHandlerHandler(GameActionsButton gameActions) {
        this.gameActions = gameActions;
    }

    @Override
    public PlayState handleInput(String input, Player player, ButtonClickEvent event) {
        if (gameActions.isCommandFromCorrectPlayer(player) && input.equals("hit")) {
            if (gameActions.hit()) {
                return PlayState.ROUND_OVER;
            }
        }
        if (gameActions.isCommandFromCorrectPlayer(player) && input.equals("stand")) {
            if (gameActions.stand()) {
                return PlayState.ROUND_OVER;
            }
        }
        if (gameActions.isCommandFromCorrectPlayer(player) && input.equals("double")) {
                if (gameActions.doubleMove()) {
                    return PlayState.ROUND_OVER;
                }
        }
        if (gameActions.isCommandFromCorrectPlayer(player) && input.equals("split")) {
            gameActions.split();
        }

        return PlayState.PLAYING;
    }

}
