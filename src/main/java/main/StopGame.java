package main;

import main.game_control_files.PlayState;
import main.util.Player;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Set;

// meanwhile unused. probably not needed


public class StopGame implements IGameAction{


    private Set<Player> playerSet;
    private PlayState currentPlayState;

    public StopGame(Set<Player> playerSet, PlayState currentPlayState) {
        this.playerSet = playerSet;
        this.currentPlayState = currentPlayState;
    }

    public void setCurrentPlayState(PlayState currentPlayState) {
        this.currentPlayState = currentPlayState;
    }

    @Override
    public PlayState handleInput(String input, Player player, TextChannel channel) {
        if (input.equals("quit")) {
            playerSet.clear();
            channel.sendMessage("BlackJack is over! Bot is in standby").queue();
            return PlayState.NOT_PLAYING;
        }
        return currentPlayState;
    }
}
