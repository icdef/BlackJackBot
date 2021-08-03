package main.blackjack_state_handlers_button;

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
        if (gameActions.isCommandFromCorrectPlayer(player)){

            if (input.equals("hit")){
                if (gameActions.hit()) {
                    return PlayState.ROUND_OVER;
                }
            }

            if (input.equals("stand")){
                if (gameActions.stand()) {
                    return PlayState.ROUND_OVER;
                }
            }

            if (input.equals("double")){
                if (gameActions.doubleMove()) {
                    return PlayState.ROUND_OVER;
                }
            }

            if (input.equals("split")){
                gameActions.split();
            }

        }
        else {
            event.reply("It is not your turn").setEphemeral(true).queue();
        }

        return PlayState.PLAYING;
    }

}
