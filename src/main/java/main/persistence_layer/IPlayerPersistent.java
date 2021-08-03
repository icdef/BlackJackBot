package main.persistence_layer;

import main.Player;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Map;

public interface IPlayerPersistent {

    /**
     * reads the input file and returns a map with key: userName and value: Player instance with balance and name from file
     *
     * @return Map with Key=UserName Value=Player instance with name and balance from file
     */
    Map<String, Player> readAlreadyRegisteredPlayers();

    /**
     * Registers a player for the BlackJack game. Prints the feedback of the registration to the channel.
     *
     * @param user    the new Player
     * @param channel text channel where the answer is written to
     */
    void registerPlayer(User user, TextChannel channel);

    /**
     * writes all registered players back to the file
     */
    void writePlayersBackToFile();

    /**
     * gets balance from specified player
     * @param player whose balance it should return
     * @return balance in string format
     */
    String readPlayerBalance(Player player);
}
