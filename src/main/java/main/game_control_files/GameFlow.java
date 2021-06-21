package main.game_control_files;

import main.Main;
import main.Player;
import main.blackjack_state_handlers_text.Betting;
import main.blackjack_state_handlers_text.IGameAction;
import main.blackjack_state_handlers_text.NotPlaying;
import main.blackjack_state_handlers_button.ChoosingPlayerButtonHandlerHandler;
import main.blackjack_state_handlers_button.IGameActionButtonHandler;
import main.blackjack_state_handlers_button.PlayingButtonHandlerHandler;
import main.blackjack_state_handlers_button.AllBetButtonHandler;
import main.persistence_layer.IPlayerPersistent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class GameFlow extends ListenerAdapter {

    private final Set<Player> playerSet;
    private final NumberFormat nf = new DecimalFormat("##.###");
    private final GameActionsButton gameActionsButton;
    private final JDA jda;
    private final EnumMap<PlayState, IGameAction> playStateIGameActionMap =
            new EnumMap<>(PlayState.class);
    private final EnumMap<PlayState, IGameActionButtonHandler> playStateIGameActionButtonMap =
            new EnumMap<>(PlayState.class);
    private final Map<String, Player> registeredPlayers;
    private PlayState playState;

    public GameFlow(PlayState playState, Set<Player> playerSet, IPlayerPersistent playerPersistent,
                    JDA jda) {
        this.playState = playState;
        this.playerSet = playerSet;
        this.jda = jda;
        this.registeredPlayers = playerPersistent.readAlreadyRegisteredPlayers();
        this.gameActionsButton = new GameActionsButton(jda);
        initializingMaps();
    }

    private void initializingMaps() {
        IGameAction notPlaying = new NotPlaying();
        IGameAction betting = new Betting(playerSet);
        playStateIGameActionMap.put(PlayState.NOT_PLAYING, notPlaying);
        playStateIGameActionMap.put(PlayState.BETTING, betting);

        IGameActionButtonHandler choosingPlayer = new ChoosingPlayerButtonHandlerHandler(playerSet);
        IGameActionButtonHandler playing = new PlayingButtonHandlerHandler(gameActionsButton);
        IGameActionButtonHandler allBet = new AllBetButtonHandler(playerSet,gameActionsButton);
        playStateIGameActionButtonMap.put(PlayState.CHOOSING_PLAYER,choosingPlayer);
        playStateIGameActionButtonMap.put(PlayState.PLAYING,playing);
        playStateIGameActionButtonMap.put(PlayState.ALL_BETS_IN,allBet);

    }


    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.getChannel().getId().equals(Main.PLAY_CHANNEL_ID)) {
            return;
        }
        String input = event.getMessage().getContentRaw();
        TextChannel channel = event.getChannel();
        Player player = registeredPlayers.get(event.getAuthor().getId());
        IGameAction action = playStateIGameActionMap.get(playState);
        if (action != null) {
            playState = action.handleInput(input, player, channel);

        }


    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        String input = event.getButton().getId();
        TextChannel channel = event.getTextChannel();
        Player player = registeredPlayers.get(event.getUser().getId());
        gameActionsButton.setEvent(event);
        IGameActionButtonHandler buttonAction = playStateIGameActionButtonMap.get(playState);
        if (buttonAction != null) {
            playState = buttonAction.handleInput(input,player,event);
            if (playState == PlayState.ROUND_OVER){
                playState = roundOver(channel);
            }
        }

    }

    /**
     * gets called when the round is over. Prints the Win/Losses of all players in an embed. Afterwards resets the players.
     *
     * @return PlayState choosing player
     */
    private PlayState roundOver(TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(jda.getUserByTag("BlackJackBot#1745").getName(), null,
                jda.getUserByTag("BlackJackBot#1745").getAvatarUrl());
        builder.setTitle("ROUND OVER");

        gameActionsButton.calculatePayout();
        for (Player p : playerSet) {
            String fieldName =
                    p.getNameNoTag() + (p.getWonAmount() > 0 ? " won $" + nf.format(p.getWonAmount()) :
                            p.getWonAmount() < 0 ? " lost $" + nf.format(Math.abs(p.getWonAmount())) : " push");
            builder.addField(fieldName, "Current balance: $" + nf.format(p.getMoney()), false);
        }
        builder.setFooter("Players can join and leave or start the next round");
        channel.sendMessageEmbeds(builder.build()).queue();

        gameActionsButton.resetPlayers();

        channel.sendMessage(
                "BlackJack game started. Press join to join or start or start").
                setActionRow(Button.primary("join","Click to join the game"),
                Button.primary("start","Click to start the round"),
                Button.primary("leave","Leave table"),
                Button.primary("quit","Bot goes into standby"))
                .queue();

        return PlayState.CHOOSING_PLAYER;
    }
}
