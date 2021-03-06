package blackjack_state_handlers_text;


import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.Button;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import player_entity.Player;
import game_control_files.PlayState;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Set;

public class Betting implements IGameAction {

    private final NumberFormat nf = new DecimalFormat("##.###");
    private final Set<Player> playerSet;
    private static final Logger logger = LoggerFactory.getLogger(Betting.class);

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
        logger.trace("Method call handle Input with {} {} {}",input,player,channel);
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
           channel.sendMessage("All players entered their bet. Press the button to start").
                   setActionRow(Button.primary("roundStart","start")).queue();
           return PlayState.ALL_BETS_IN;

        }
        return PlayState.BETTING;
    }
}
