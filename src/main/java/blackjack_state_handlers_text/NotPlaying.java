package blackjack_state_handlers_text;


import game_control_files.PlayState;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import player_entity.Player;
import text_commands.BalanceCommand;
import text_commands.ClearCommand;

public class NotPlaying implements IGameAction {

    private static final Logger logger = LoggerFactory.getLogger(NotPlaying.class);
    private final ClearCommand clearCommand = new ClearCommand();
    private final BalanceCommand balanceCommand;

    public NotPlaying(JDA jda) {
        balanceCommand = new BalanceCommand(jda);
    }

    @Override
    public PlayState handleInput(String input, Player player, TextChannel channel) {
        if (input.equals("blackjack")) {
            channel.sendMessage(
                    "BlackJack game started. PlayerEntity.Player can type join to join the game. Type start to start the round")
                    .setActionRow(Button.success("join","Click to join table"))
                    .queue();
            logger.debug("Started blackjack");
            return PlayState.CHOOSING_PLAYER;
        }

        if (input.equals("clear")) {
           clearCommand.clearChannel(channel);
        }
        if (input.equals("balance")) {
            balanceCommand.showBalance(player);
        }

        return PlayState.NOT_PLAYING;
    }
}
