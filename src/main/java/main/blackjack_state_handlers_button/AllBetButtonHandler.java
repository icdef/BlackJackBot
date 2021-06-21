package main.blackjack_state_handlers_button;

import main.Player;
import main.game_control_files.GameActionsButton;
import main.game_control_files.PlayState;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java.util.Set;

public class AllBetButtonHandler implements IGameActionButtonHandler{

    private final Set<Player> playerSet;
    private final GameActionsButton gameActionsButton;

    public AllBetButtonHandler(Set<Player> playerSet, GameActionsButton gameActionsButton) {
        this.playerSet = playerSet;
        this.gameActionsButton = gameActionsButton;
    }

    @Override
    public PlayState handleInput(String input, Player player, ButtonClickEvent event) {

        if (input.equals("roundStart")){
            event.deferEdit().queue();
            gameActionsButton.setPlayers(playerSet);
            gameActionsButton.setEvent(event);
            gameActionsButton.setUp();
            if (gameActionsButton.doAllPlayersHaveBlackJack()) {
                return PlayState.ROUND_OVER;
            }
             return PlayState.PLAYING;
        }

        throw new IllegalStateException("This should be only reached when you start a round after betting");
    }
}
