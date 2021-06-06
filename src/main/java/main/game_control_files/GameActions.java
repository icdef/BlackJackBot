package main.game_control_files;

import main.playing_cards.Card;
import main.playing_cards.DeckUtility;
import main.util.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GameActions {
    private final Player dealer = new Player("Dealer");
    private final Deque<Player> playersInGame = new ArrayDeque<>();
    private final JDA jda;
    private Set<Player> players;
    private final Map<Player, Player> splitPlayers = new HashMap<>();
    private TextChannel channel;
    private Deque<Card> deck;
    private Player activePlayer;

    public GameActions(JDA jda) {
        this.jda = jda;
    }

    public void setPlayers(Set<Player> players) {
        this.players = players;
    }

    public void setChannel(TextChannel channel) {
        this.channel = channel;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    /**
     * checks if activePlayer is currently playing a split hand
     *
     * @param activePlayer
     * @return true when active is playing a split hand. Otherwise false
     */
    private boolean playerCurrentlySplitting(Player activePlayer) {
        return splitPlayers.containsValue(activePlayer) || splitPlayers.containsKey(activePlayer);
    }

    /**
     * checks whether the active player is the same player as the one given in param
     *
     * @param player
     * @return true when the param player equals to active player. Otherwise false
     */
    public boolean commandFromCorrectPlayer(Player player) {
        if (player == null)
            return false;
        return player == getActivePlayer() || (player.getNameNoTag() + " split").equals(activePlayer.getNameNoTag());
    }

    /**
     * Creates embed of the current round
     *
     * @return MessageEmbed with all the info of the current round
     */
    private MessageEmbed currentRound() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Current Table");
        builder.setAuthor(jda.getUserByTag("BlackJackBot#1745").getName(), null, jda.getUserByTag("BlackJackBot#1745").getAvatarUrl());
        builder.addField(addActiveToNameWhenActivePlayer(dealer), dealer.toString(), false);
        for (Player p : players) {
            builder.addField(addActiveToNameWhenActivePlayer(p), p.toString(), false);
            builder.setColor(Color.black);
            Player splitPlayer = splitPlayers.get(p);
            if (splitPlayer != null) {
                builder.addField(addActiveToNameWhenActivePlayer(splitPlayer), splitPlayer.toString(), false);
            }
        }
        return builder.build();
    }


    private String addActiveToNameWhenActivePlayer(Player player) {
        return player.equals(activePlayer) ? player.getNameNoTag() + " active" : player.getNameNoTag();
    }

    /**
     * Creates embed of the current round. Active Player also have "active" next to his/her name
     *
     * @return MessageEmbed with all the info of the current round together with the info of who is the active player
     */
    private Message printCurrentGameWithActivePlayer() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Current Table");
        builder.setAuthor(jda.getUserByTag("BlackJackBot#1745").getName(), null, jda.getUserByTag("BlackJackBot#1745").getAvatarUrl());
        if (activePlayer != dealer) {
            String split = allowedToSplit() ? ", split" : "";
            String doubleAble = allowedToDouble() ? ", double" : "";
            String fieldValue = "hit, stand" + split + doubleAble;
            builder.addField("Possible moves", fieldValue, false);
        }
        builder.addField(addActiveToNameWhenActivePlayer(dealer), dealer.toString(), false);
        for (Player p : players) {
            builder.addField(addActiveToNameWhenActivePlayer(p), p.toString(), false);
            Player splitPlayer = splitPlayers.get(p);
            if (splitPlayer != null) {
                builder.addField(addActiveToNameWhenActivePlayer(splitPlayer), splitPlayer.toString(), false);
            }
        }
        return channel.sendMessage(builder.build()).complete();
    }

    /**
     * resets hand, bet amount, won amount, busted state, etc of all players and dealer
     */
    public void resetPlayers() {
        for (Player player : players) {
            player.resetPlayer();
        }
        dealer.resetPlayer();
        splitPlayers.clear();
    }

    /**
     * hands out cards to players and dealer. Also sets up the playing order of round
     */
    public void setUp() {
        deck = new DeckUtility().generatePlayingDeck();
        for (Player player : players) {
            player.addCardToHand(deck.pop());
        }
        dealer.addCardToHand(deck.pop());

        for (Player player : players) {
            player.addCardToHand(deck.pop());
        }

        // for debugging
       /* players.get(0).removeACardFromHand();
        players.get(0).removeACardFromHand();
        players.get(0).addCardToHand(new Card(10, "K"));
        players.get(0).addCardToHand(new Card(10, "K"));
        dealer.removeACardFromHand();
        dealer.addCardToHand(new Card(10, "K"));*/


        playersInGame.push(dealer);
        List<Player> playerSetAsList = new LinkedList<>(players);
        Collections.reverse(playerSetAsList);
        for (Player player : playerSetAsList) {
            playersInGame.push(player);
        }
        activePlayer = playersInGame.pop();

    }

    /**
     * checks if any player has a blackjack
     *
     * @return true when all player have blackjack, meaning its the turn of the dealer. Otherwise false
     */
    public boolean checkForBlackJacks() {
        while (activePlayer != dealer) {
            if (activePlayer.getHandSize() == 2 && activePlayer.getCurrentHandValue() == 21) {
                activePlayer.setBlackJack(true);
                activePlayer = playersInGame.pop();
            } else
                break;
        }
        Message message = printCurrentGameWithActivePlayer();
        if (activePlayer == dealer) {
            dealerPlay(message);
            return true;
        }
        return false;
    }

    /**
     * dealer plays till he gets 17 or higher. Should only be called when the activePlayer is the dealer.
     *
     * @param message embed which describes the current round.
     */
    public void dealerPlay(Message message) {
        while (activePlayer.getCurrentHandValue() < 17) {
            try {
                Thread.sleep(1000);
                activePlayer.addCardToHand(deck.pop());
                if (activePlayer.getHandSize() == 2 && activePlayer.getCurrentHandValue() == 21) {
                    activePlayer.setBlackJack(true);
                }
                if (activePlayer.getCurrentHandValue() > 21) {
                    activePlayer.setBusted(true);
                }
                message.editMessage(currentRound()).complete();
            }
            catch (InterruptedException e ) {
                System.out.println("Dealer got interrupted");
            }
        }


    }

    /**
     * Player gets another card. Prints the current round state afterwards
     *
     * @return true when all players played. Otherwise false
     */
    public boolean hit() {
        activePlayer.addCardToHand(deck.pop());
        if (activePlayer.getCurrentHandValue() > 21) {
            activePlayer.setBusted(true);
            activePlayer = playersInGame.pop();
        }
        Message message = printCurrentGameWithActivePlayer();
        if (activePlayer == dealer) {
            dealerPlay(message);
            return true;
        }
        return false;
    }

    /**
     * Player stands. Prints the current round state afterwards
     *
     * @return true when all players played. Otherwise false
     */
    public boolean stand() {
        activePlayer = playersInGame.pop();
        Message message = printCurrentGameWithActivePlayer();
        if (activePlayer == dealer) {
            dealerPlay(message);
            return true;
        }
        return false;
    }

    /**
     * checks whether the active player is allowed to double.
     *
     * @return true if allowed. Otherwise false
     */
    public boolean allowedToDouble() {
        return activePlayer.getHandSize() == 2 && !playerCurrentlySplitting(activePlayer);
    }

    /**
     * Player gets another card and doubles his/her bet. Prints the current round state afterwards
     *
     * @return true when all players played. Otherwise false
     */
    public boolean doubleMove() {
        activePlayer.reduceMoney(activePlayer.getBetAmount());
        activePlayer.setBetAmount(activePlayer.getBetAmount() * 2);
        activePlayer.addCardToHand(deck.pop());
        if (activePlayer.getCurrentHandValue() > 21) {
            activePlayer.setBusted(true);
        }
        activePlayer = playersInGame.pop();
        Message message = printCurrentGameWithActivePlayer();
        if (activePlayer == dealer) {
            dealerPlay(message);
            return true;
        }
        return false;
    }

    /**
     * checks whether the active player is allowed to split his/her hand
     *
     * @return true when the play can split. Otherwise false
     */
    public boolean allowedToSplit() {
        return activePlayer.isHandSplittable() && !playerCurrentlySplitting(activePlayer);
    }

    /**
     * Player splits his/her hand. Player will be put into the game as playerName split right after the first hand, so he/she can play both of the hands subsequently. Prints the current round state afterwards
     */
    public void split() {
        activePlayer.reduceMoney(activePlayer.getBetAmount());
        Player fakePlayer = new Player(activePlayer.getNameNoTag() + " split");
        fakePlayer.addCardToHand(activePlayer.removeACardFromHand());
        activePlayer.addCardToHand(deck.pop());
        fakePlayer.addCardToHand(deck.pop());
        splitPlayers.put(activePlayer, fakePlayer);
        playersInGame.push(fakePlayer);
        printCurrentGameWithActivePlayer();
    }

    /**
     * calculates the win/loss for all players and saves them into the player instances.
     */
    public void calculatePayout() {
        // all the cases where a player split
        for (Map.Entry<Player, Player> entry : splitPlayers.entrySet()) {
            Player fakePlayer = entry.getValue();
            Player realPlayer = entry.getKey();
            // won
            if ((!fakePlayer.isBusted() && dealer.isBusted()) || (!fakePlayer.isBusted() && !dealer.isBusted() && fakePlayer.getCurrentHandValue() > dealer.getCurrentHandValue())) {
                realPlayer.addMoney(realPlayer.getBetAmount() * 2);
                realPlayer.addWonAmount(realPlayer.getBetAmount() * 2);
                if (fakePlayer.isBlackJack()) {
                    realPlayer.addMoney(realPlayer.getBetAmount() / 2);
                    realPlayer.addWonAmount(realPlayer.getBetAmount() / 2);
                }
            }
            // push
            else if (!fakePlayer.isBusted() && !dealer.isBusted() && fakePlayer.getCurrentHandValue() == dealer.getCurrentHandValue()) {
                realPlayer.addMoney(realPlayer.getBetAmount());
            }
            // lost
            else {
                realPlayer.addWonAmount(realPlayer.getBetAmount() * (-1));
            }


        }
        for (Player p : players) {
            // won
            if ((!p.isBusted() && dealer.isBusted()) || (!p.isBusted() && !dealer.isBusted() && p.getCurrentHandValue() > dealer.getCurrentHandValue())) {
                p.addMoney(p.getBetAmount() * 2);
                p.addWonAmount(p.getBetAmount() * 2);
                if (p.isBlackJack()) {
                    p.addWonAmount(p.getBetAmount() / 2);
                    p.addMoney(p.getBetAmount() / 2);
                }
            }
            // push
            else if (!p.isBusted() && !dealer.isBusted() && p.getCurrentHandValue() == dealer.getCurrentHandValue()) {
                p.addMoney(p.getBetAmount());
            }
            // lost
            else {
                p.addWonAmount(p.getBetAmount() * (-1));
            }
        }
    }


}
