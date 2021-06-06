package main.game_control_files;

import main.Main;
import main.util.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Set;

public class GameFlow extends ListenerAdapter {

    private PlayState playState;
    private Set<Player> playerSet;
    private Map<String, Player> registeredPlayer;
    private NumberFormat nf = new DecimalFormat("##.###");
    private GameActions gameActions;

    public GameFlow(PlayState playState, Set<Player> playerSet, Map<String, Player> registeredPlayer, GameActions gameActions) {
        this.playState = playState;
        this.playerSet = playerSet;
        this.registeredPlayer = registeredPlayer;
        this.gameActions = gameActions;
    }

    /**
     * checks whether all players placed a bet
     *
     * @return true when all placed a bet. Otherwise false
     */
    private boolean allPlayersBet() {
        for (Player player : playerSet) {
            if (player.getBetAmount() == 0)
                return false;
        }
        return true;
    }


    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;
        if (!event.getChannel().getId().equals(Main.PLAY_CHANNEL_ID))
            return;

        String input = event.getMessage().getContentRaw();
        String[] inputSplitted = input.split(" ");
        TextChannel channel = event.getChannel();
        Player player = registeredPlayer.get(event.getAuthor().getAsTag());

        if (playState == PlayState.NOT_PLAYING && input.equals("blackjack")) {
            playState = PlayState.CHOOSING_PLAYER;
            channel.sendMessage("BlackJack game started. Player can type join to join the game. Type start to start the round").queue();
        }
        // quitting should not be possible during betting or an active game. Also only players who are participating should be able to quit the whole blackjack session
        if (playState != PlayState.PLAYING && playState != PlayState.BETTING && (playerSet.contains(player) || playerSet.isEmpty()) && input.equals("quit")) {
            playState = PlayState.NOT_PLAYING;
            playerSet.clear();
            channel.sendMessage("BlackJack is over! Bot is in standby").queue();
        }
        if (playState == PlayState.CHOOSING_PLAYER && input.equals("join")) {
            if (player == null) {
                channel.sendMessage("Please register yourself first").queue();
            } else if (playerSet.contains(player)) {
                channel.sendMessage("You already joined the table").queue();
            } else {
                playerSet.add(player);
                channel.sendMessage("You joined the table").queue();
            }
        }
        if ((playState == PlayState.CHOOSING_PLAYER) && input.equals("leave")) {
            if (playerSet.remove(player)) {
                channel.sendMessage("You left the table").queue();
            } else {
                channel.sendMessage("You were not on the table").queue();
            }
        }
        // initiates the bet state when at least one player joined the table
        if (playState == PlayState.CHOOSING_PLAYER && input.equals("start")) {
            if (playerSet.isEmpty()) {
                channel.sendMessage("No players have joined yet").queue();
            } else if (!playerSet.contains(player)) {
                channel.sendMessage("You did not join the game").queue();
            } else {
                channel.sendMessage("Round starts").queue();
                channel.sendMessage("Enter your bets with bet <amount>").queue();
                playState = PlayState.BETTING;
            }
        }
        if (playState == PlayState.BETTING && inputSplitted[0].equals("bet")) {
            if (player != null && playerSet.contains(player)) {
                try {
                    double bet = Double.parseDouble(inputSplitted[1]);
                    if (bet > player.getMoney()) {
                        channel.sendMessage("You do not have enough money for that bet").queue();
                    } else if (bet > 0) {
                        player.setBetAmount(bet);
                        player.reduceMoney(bet);
                        channel.sendMessage(event.getAuthor().getName() + " bet " + nf.format(player.getBetAmount())).queue();
                    } else {
                        channel.sendMessage("Invalid Bet").queue();
                    }
                } catch (NumberFormatException e) {
                    channel.sendMessage("Invalid Bet").queue();
                }
            } else {
                channel.sendMessage("You are not allowed to bet").queue();
            }
            if (allPlayersBet()) {
                channel.sendMessage("All players bet. Starting round").queue();
                playState = PlayState.PLAYING;
                gameActions.setChannel(channel);
                gameActions.setPlayers(playerSet);
                gameActions.setUp();
                if (gameActions.checkForBlackJacks()) {
                    roundOver(channel, event.getJDA());
                }

            }
        }
        if (playState == PlayState.PLAYING && gameActions.commandFromCorrectPlayer(player) && input.equals("hit")) {
            if (gameActions.hit()) {
                roundOver(channel, event.getJDA());
            }

        }
        if (playState == PlayState.PLAYING && gameActions.commandFromCorrectPlayer(player) && input.equals("stand")) {
            if (gameActions.stand()) {
                roundOver(channel, event.getJDA());
            }
        }
        if (playState == PlayState.PLAYING && gameActions.commandFromCorrectPlayer(player) && input.equals("double")) {
            if (gameActions.allowedToDouble()) {
                if (gameActions.doubleMove()) {
                    roundOver(channel, event.getJDA());
                }
            } else
                channel.sendMessage("You are not allowed to double").queue();
        }
        if (playState == PlayState.PLAYING && gameActions.commandFromCorrectPlayer(player) && input.equals("split")) {
            if (gameActions.allowedToSplit()) {
                gameActions.split();
            } else
                channel.sendMessage("You are not allowed to split").queue();
        }


    }

    /**
     * gets called when the round is over. Prints the Win/Losses of all players in an embed. Afterwards resets the players.
     *
     * @param channel textchannel where the game is printed to
     * @param jda     the JDA of the event
     */
    public void roundOver(TextChannel channel, JDA jda) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(jda.getUserByTag("BlackJackBot#1745").getName(), null, jda.getUserByTag("BlackJackBot#1745").getAvatarUrl());
        builder.setTitle("ROUND OVER");

        gameActions.calculatePayout();
        for (Player p : playerSet) {
            String fieldName = p.getNameNoTag() + (p.getWonAmount() > 0 ? " won " + nf.format(p.getWonAmount()) + "$" :
                    p.getWonAmount() < 0 ? " lost " + nf.format(Math.abs(p.getWonAmount())) + "$" : " push");
            builder.addField(fieldName, "Current balance: " + nf.format(p.getMoney()) + "$", false);
        }
        builder.setFooter("Players can join and leave or start the next round");
        channel.sendMessage(builder.build()).queue();
        playState = PlayState.CHOOSING_PLAYER;
        gameActions.resetPlayers();
    }
}
