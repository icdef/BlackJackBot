package blackjack_state_handlers_button;


import main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import player_entity.Player;
import game_control_files.GameActionsButton;
import game_control_files.PlayState;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;



import java.util.Set;

public class AllBetButtonHandler implements IGameActionButtonHandler{

    private final Set<Player> playerSet;
    private final GameActionsButton gameActionsButton;

    private static final Logger logger = LoggerFactory.getLogger(AllBetButtonHandler.class);
    public AllBetButtonHandler(Set<Player> playerSet, GameActionsButton gameActionsButton) {
        this.playerSet = playerSet;
        this.gameActionsButton = gameActionsButton;
    }

    @Override
    public PlayState handleInput(String input, Player player, ButtonClickEvent event) {
        logger.trace("Method call handleInput with {} {} {}",input,player,event);
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
