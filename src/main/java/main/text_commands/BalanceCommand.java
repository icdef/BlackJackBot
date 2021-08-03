package main.text_commands;

import main.Player;
import main.persistence_layer.IPlayerPersistent;
import main.persistence_layer.PlayerFilePersistent;
import net.dv8tion.jda.api.JDA;


public class BalanceCommand {

    private final IPlayerPersistent playerPersistent;
    private final JDA jda;

    public BalanceCommand(JDA jda) {
        this.jda = jda;
        playerPersistent = new PlayerFilePersistent(jda);
    }
    public void showBalance(Player player){
        String balance = playerPersistent.readPlayerBalance(player);
        String message = balance != null ? String.format("You have a balance of $%s", balance) : "Your balance was not found!";
        jda.openPrivateChannelById(player.getUuid()).queue( conn -> conn.sendMessage(message).queue());
    }
}
