package main;

import main.game_control_files.GameActions;
import main.game_control_files.GameFlow;
import main.game_control_files.PlayState;
import main.util.Player;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Set;

public class Betting implements IGameAction {

    private NumberFormat nf = new DecimalFormat("##.###");
    private Set<Player> playerSet;
    private GameActions gameActions;
    private GameFlow gameFlow;

    public Betting(Set<Player> playerSet, GameActions gameActions, GameFlow gameFlow) {
        this.playerSet = playerSet;
        this.gameActions = gameActions;
        this.gameFlow = gameFlow;
    }

    /**
     * checks whether all players placed a bet
     *
     * @return true when all placed a bet. Otherwise false
     */
    private boolean didAllPlayersBet() {
        for (Player player : playerSet) {
            if (player.getBetAmount() == 0)
                return false;
        }
        return true;
    }

    @Override
    public PlayState handleInput(String input, Player player, TextChannel channel) {
        String[] inputSplitted = input.split(" ");
        if (player != null && playerSet.contains(player)) {
            try {
                double bet = Double.parseDouble(inputSplitted[1]);
                if (bet > player.getMoney()) {
                    channel.sendMessage("You do not have enough money for that bet").queue();
                } else if (bet > 0) {
                    player.setBetAmount(bet);
                    player.reduceMoney(bet);
                    channel.sendMessage(player.getNameNoTag() + " bet " + nf.format(player.getBetAmount())).queue();
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
                return gameFlow.roundOver(channel);
            }
            return PlayState.PLAYING;

        }
        return PlayState.BETTING;
    }
}
