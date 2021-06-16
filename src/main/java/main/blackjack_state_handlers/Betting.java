package main.blackjack_state_handlers;

import main.Player;
import main.game_control_files.GameActions;
import main.game_control_files.PlayState;
import net.dv8tion.jda.api.entities.TextChannel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Set;

public class Betting implements IGameAction {

    private final NumberFormat nf = new DecimalFormat("##.###");
    private final Set<Player> playerSet;
    private final GameActions gameActions;


    public Betting(Set<Player> playerSet, GameActions gameActions) {
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
    public PlayState handleInput(String input, Player player, TextChannel channel) {
        String[] inputSplitted = input.split(" ");
        if (inputSplitted.length != 2) {
            channel.sendMessage("Need to enter: bet <amount>").queue();
            return PlayState.BETTING;
        }
        if (player != null && playerSet.contains(player)) {
            try {
                double bet = Double.parseDouble(inputSplitted[1]);
                if (bet > player.getMoney()) {
                    channel.sendMessage("You do not have enough money for that bet").queue();
                } else if (bet > 0) {
                    player.setBetAmount(bet);
                    player.reduceMoney(bet);
                    channel.sendMessage(player.getNameNoTag() + " bet " + nf.format(player.getBetAmount()))
                            .queue();
                } else {
                    channel.sendMessage("Invalid Bet").queue();
                }
            } catch (NumberFormatException e) {
                channel.sendMessage("Invalid Bet").queue();
            }
        } else {
            channel.sendMessage("You are not allowed to bet").queue();
        }

        if (didAllPlayersBet()) {
            channel.sendMessage("All players bet. Starting round").queue();
            gameActions.setChannel(channel);
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
