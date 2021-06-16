package main.blackjack_state_handlers;

import main.game_control_files.GameActions;
import main.game_control_files.GameFlow;
import main.game_control_files.PlayState;
import main.Player;
import net.dv8tion.jda.api.entities.TextChannel;

public class Playing implements IGameAction {


    private GameActions gameActions;


    public Playing(GameActions gameActions) {
        this.gameActions = gameActions;

    }

    @Override
    public PlayState handleInput(String input, Player player, TextChannel channel) {
        if (gameActions.commandFromCorrectPlayer(player) && input.equals("hit")) {
            if (gameActions.hit()) {
                return PlayState.ROUND_OVER;
            }

        }
        if (gameActions.commandFromCorrectPlayer(player) && input.equals("stand")) {
            if (gameActions.stand()) {
                return PlayState.ROUND_OVER;
            }
        }
        if (gameActions.commandFromCorrectPlayer(player) && input.equals("double")) {
            if (gameActions.allowedToDouble()) {
                if (gameActions.doubleMove()) {
                    return PlayState.ROUND_OVER;
                }
            } else
                channel.sendMessage("You are not allowed to double").queue();
        }
        if (gameActions.commandFromCorrectPlayer(player) && input.equals("split")) {
            if (gameActions.allowedToSplit()) {
                gameActions.split();
            } else
                channel.sendMessage("You are not allowed to split").queue();
        }
        return PlayState.PLAYING;
    }
}
