package main.game_control_files;

import main.Main;
import main.Player;
import main.blackjack_state_handlers.*;
import main.persistence_layer.IPlayerPersistent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class GameFlow extends ListenerAdapter {

    private final Set<Player> playerSet;
    private final NumberFormat nf = new DecimalFormat("##.###");
    private final GameActions gameActions;
    private final JDA jda;
    private final EnumMap<PlayState, IGameAction> playStateIGameActionMap = new EnumMap<>(PlayState.class);
    private final Map<String, Player> registeredPlayers;
    private PlayState playState;

    public GameFlow(PlayState playState, Set<Player> playerSet, IPlayerPersistent playerPersistent, GameActions gameActions, JDA jda) {
        this.playState = playState;
        this.playerSet = playerSet;
        this.gameActions = gameActions;
        this.jda = jda;
        this.registeredPlayers = playerPersistent.readAlreadyRegisteredPlayers();
        initializingMap();
    }

    private void initializingMap() {
        IGameAction notPlaying = new NotPlaying();
        IGameAction choosingPlayer = new ChoosingPlayer(playerSet);
        IGameAction betting = new Betting(playerSet, gameActions);
        IGameAction playing = new Playing(gameActions);
        playStateIGameActionMap.put(PlayState.NOT_PLAYING, notPlaying);
        playStateIGameActionMap.put(PlayState.CHOOSING_PLAYER, choosingPlayer);
        playStateIGameActionMap.put(PlayState.BETTING, betting);
        playStateIGameActionMap.put(PlayState.PLAYING, playing);
    }


    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;
        if (!event.getChannel().getId().equals(Main.PLAY_CHANNEL_ID))
            return;
        String input = event.getMessage().getContentRaw();
        TextChannel channel = event.getChannel();
        Player player = registeredPlayers.get(event.getAuthor().getId());
        playState = playStateIGameActionMap.get(playState).handleInput(input, player, channel);
        if (playState == PlayState.ROUND_OVER) {
            roundOver(channel);
            playState = PlayState.CHOOSING_PLAYER;
        }


    }

    /**
     * gets called when the round is over. Prints the Win/Losses of all players in an embed. Afterwards resets the players.
     *
     * @param channel textchannel where the game is printed to
     * @return Playstate choosing player
     */
    public PlayState roundOver(TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(jda.getUserByTag("BlackJackBot#1745").getName(), null, jda.getUserByTag("BlackJackBot#1745").getAvatarUrl());
        builder.setTitle("ROUND OVER");

        gameActions.calculatePayout();
        for (Player p : playerSet) {
            String fieldName = p.getNameNoTag() + (p.getWonAmount() > 0 ? " won $" + nf.format(p.getWonAmount()) :
                    p.getWonAmount() < 0 ? " lost $" + nf.format(Math.abs(p.getWonAmount())) : " push");
            builder.addField(fieldName, "Current balance: $" + nf.format(p.getMoney()), false);
        }
        builder.setFooter("Players can join and leave or start the next round");
        channel.sendMessage(builder.build()).queue();
        gameActions.resetPlayers();
        return PlayState.CHOOSING_PLAYER;
    }
}
