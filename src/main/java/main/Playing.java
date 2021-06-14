package main;

import main.game_control_files.GameActions;
import main.game_control_files.GameFlow;
import main.game_control_files.PlayState;
import main.util.Player;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Playing implements IGameAction{


    private GameActions gameActions;
    private GameFlow gameFlow;

    public Playing(GameActions gameActions, GameFlow gameFlow) {
        this.gameActions = gameActions;
        this.gameFlow = gameFlow;
    }

    @Override
    public PlayState handleInput(String input, Player player, TextChannel channel) {
        if (gameActions.commandFromCorrectPlayer(player) && input.equals("hit")) {
            if (gameActions.hit()) {
                return gameFlow.roundOver(channel);
            }

        }
        if (gameActions.commandFromCorrectPlayer(player) && input.equals("stand")) {
            if (gameActions.stand()) {
                return gameFlow.roundOver(channel);
            }
        }
        if (gameActions.commandFromCorrectPlayer(player) && input.equals("double")) {
            if (gameActions.allowedToDouble()) {
                if (gameActions.doubleMove()) {
                    return gameFlow.roundOver(channel);
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
