package blackjack_state_handlers_button;

import game_control_files.PlayState;
import main.Main;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import player_entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ChoosingPlayerButtonHandlerHandler implements IGameActionButtonHandler {

    private final Set<Player> playerSet;
    private static final Logger logger = LoggerFactory.getLogger(ChoosingPlayerButtonHandlerHandler.class);
    public ChoosingPlayerButtonHandlerHandler(Set<Player> playerSet) {
        this.playerSet = playerSet;
    }

    /**
     * Generates 4 buttons for the user to interact with.
     * @return a list of buttons
     */
    private List<Button> buttonsForChoosingPlayerStage(){
        logger.trace("Method call buttonsForChoosingPlayerStage");
        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.primary("join","Click to join the game"));
        buttons.add(Button.primary("start","Click to start the round"));
        buttons.add(Button.primary("leave","Leave table"));
        buttons.add(Button.primary("quit","Bot goes into standby"));
        return buttons;
    }

    @Override
    public PlayState handleInput(String input, Player player, ButtonClickEvent event) {
        logger.trace("Method call handleInput with {} {} {}",input,player,event);
        if (input.equals("join")) {
            if (player == null) {
                event.editMessage("Please register yourself first").
                        setActionRow(buttonsForChoosingPlayerStage())
                        .queue();
            } else if (playerSet.contains(player)) {
                event.editMessage("You already joined the table").
                        setActionRow(buttonsForChoosingPlayerStage())
                        .queue();
            } else {
                playerSet.add(player);
                event.editMessage(player.getNameNoTag()+" joined the table").
                        setActionRow(buttonsForChoosingPlayerStage())
                        .queue();
            }
            event.getHook().editOriginal(("Press a button to continue")).completeAfter(1, TimeUnit.SECONDS);
        }

        if (input.equals("leave")) {
            if (playerSet.remove(player)) {
                event.editMessage("You left the table").
                        setActionRow(buttonsForChoosingPlayerStage())
                        .queue();
            } else {
                event.editMessage("You did not join the table").
                        setActionRow(buttonsForChoosingPlayerStage())
                        .queue();
            }
            event.getHook().editOriginal(("Press a button to continue")).completeAfter(1, TimeUnit.SECONDS);
        }


        if (input.equals("start")) {
            if (playerSet.isEmpty()) {
                event.editMessage("No players have joined yet").
                        setActionRow(buttonsForChoosingPlayerStage())
                        .queue();
            } else if (!playerSet.contains(player)) {
                event.editMessage("You are not part of the game").
                        setActionRow(buttonsForChoosingPlayerStage())
                        .queue();
            } else {
                event.editMessage("Everyone enter your bet amount (just a number, e.g. 100)").setActionRows().queue();
                return PlayState.BETTING;
            }
        }

        if (input.equals("quit")) {
            playerSet.clear();
            event.getTextChannel().deleteMessageById(event.getMessageId()).queue();
            event.getTextChannel().sendMessage("Session over! Bot is in standby").queue();
            return PlayState.NOT_PLAYING;
        }
        return PlayState.CHOOSING_PLAYER;
    }

}
