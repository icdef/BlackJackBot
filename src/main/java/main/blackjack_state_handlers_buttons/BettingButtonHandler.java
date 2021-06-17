package main.blackjack_state_handlers_buttons;

import main.Player;
import main.game_control_files.GameActionsButton;
import main.game_control_files.PlayState;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BettingButtonHandler implements IGameActionButton{

    private final NumberFormat nf = new DecimalFormat("##.###");
    private final Set<Player> playerSet;
    private final GameActionsButton gameActions;

    public BettingButtonHandler(Set<Player> playerSet, GameActionsButton gameActions) {
        this.playerSet = playerSet;
        this.gameActions = gameActions;
    }

    /**
     * checks whether all players placed a bet
     *
     * @return true when all placed a bet. Otherwise false
     */
    private boolean didAllPlayersBet() {
        for (Player player : playerSet) {
            if (player.getBetAmount() == 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public PlayState handleInput(String input, Player player, ButtonClickEvent event) {
            if (player != null && playerSet.contains(player)) {
                try {
                    double bet = Double.parseDouble(input);
                    if (bet > player.getMoney()) {
                        event.editMessage("You do not have enough money for that bet").
                                setActionRow(event.getMessage().getButtons())
                                .queue();
                    } else if (bet > 0) {
                        player.setBetAmount(bet);
                        player.reduceMoney(bet);
                        event.editMessage(player.getNameNoTag() + " bet " + nf.format(player.getBetAmount())).
                                setActionRow(event.getMessage().getButtons())
                                .queue();
                    } else {
                        event.editMessage("Invalid Bet").
                                setActionRow(event.getMessage().getButtons()).queue();
                    }
                } catch (NumberFormatException e) {
                    event.editMessage("Invalid Bet").
                            setActionRow(event.getMessage().getButtons()).queue();
                }
            } else {
                event.editMessage("You are not allowed to bet").
                        setActionRow(event.getMessage().getButtons()).queue();
            }

            if (didAllPlayersBet()) {
                event.getHook().editOriginal(("All players bet. Starting round")).completeAfter(1, TimeUnit.SECONDS);
                gameActions.setPlayers(playerSet);
                gameActions.setUp();
                if (gameActions.doAllPlayersHaveBlackJack()) {
                    return PlayState.ROUND_OVER;
                }
                return PlayState.PLAYING;

            }
            return PlayState.BETTING;
    }
}
