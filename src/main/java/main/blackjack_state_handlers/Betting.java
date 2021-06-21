package main.blackjack_state_handlers;

import main.Player;
import main.game_control_files.PlayState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.Button;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Set;

public class Betting implements IGameAction {

    private final NumberFormat nf = new DecimalFormat("##.###");
    private final Set<Player> playerSet;


    public Betting(Set<Player> playerSet) {
        this.playerSet = playerSet;

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
        if (player != null && playerSet.contains(player)) {
            try {
                double bet = Double.parseDouble(input);
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
           channel.sendMessage("All players entered their bet. You can change your bet or press the button to start").
                   setActionRow(Button.primary("roundStart","start")).queue();
           return PlayState.ALL_BETS_IN;

        }
        return PlayState.BETTING;
    }
}
