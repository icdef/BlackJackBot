package main;

import main.game_control_files.PlayState;
import main.util.Player;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Set;

public class ChoosingPlayer implements IGameAction{

    private Set<Player> playerSet;

    public ChoosingPlayer(Set<Player> playerSet) {

        this.playerSet = playerSet;
    }

    @Override
    public PlayState handleInput(String input, Player player, TextChannel channel) {
            if (input.equals("join")) {
                if (player == null) {
                    channel.sendMessage("Please register yourself first").queue();
                } else if (playerSet.contains(player)) {
                    channel.sendMessage("You already joined the table").queue();
                } else {
                    playerSet.add(player);
                    channel.sendMessage("You joined the table").queue();
                }
            }
            if (input.equals("leave")){
                if (playerSet.remove(player)) {
                    channel.sendMessage("You left the table").queue();
                } else {
                    channel.sendMessage("You were not on the table").queue();
                }
            }
            // initiates the bet state when at least one player joined the table
            if (input.equals("start")){
                if (playerSet.isEmpty()) {
                    channel.sendMessage("No players have joined yet").queue();
                } else if (!playerSet.contains(player)) {
                    channel.sendMessage("You did not join the game").queue();
                } else {
                    channel.sendMessage("Round starts").queue();
                    channel.sendMessage("Enter your bets with bet <amount>").queue();
                    return PlayState.BETTING;
                }
            }
            return PlayState.CHOOSING_PLAYER;
    }
}