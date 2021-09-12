package blackjack_state_handlers_button;


import game_control_files.GameActionsButton;
import game_control_files.PlayState;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import player_entity.Player;



public class PlayingButtonHandlerHandler implements IGameActionButtonHandler {

    private final GameActionsButton gameActions;
    private static final Logger logger = LoggerFactory.getLogger(PlayingButtonHandlerHandler.class);

    public PlayingButtonHandlerHandler(GameActionsButton gameActions) {
        this.gameActions = gameActions;
    }

    @Override
    public PlayState handleInput(String input, Player player, ButtonClickEvent event) {
        logger.trace("Method call handleInput with {} {} {}",input,player,event);
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
