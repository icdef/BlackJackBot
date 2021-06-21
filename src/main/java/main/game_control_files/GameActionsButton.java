package main.game_control_files;

import main.Player;
import main.playing_cards.Card;
import main.playing_cards.DeckUtility;
import main.playing_cards.Hand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GameActionsButton {

    private static final String BOT_TAG = "BlackJackBot#1745";
    private static final Logger logger = LoggerFactory.getLogger(GameActionsButton.class);
    private final Player dealer = new Player("Dealer");
    private final Hand dealerHand = dealer.getCurrentHand();
    private final Deque<Player> playersInGame = new ArrayDeque<>();
    private final JDA jda;
    private Set<Player> players;
    private final Map<Player, Player> splitPlayers = new HashMap<>();
    private Deque<Card> deck;
    private Player activePlayer;
    private boolean allHadBlackjack = false;
    private int nrOfBustedPlayers = 0;
    private ButtonClickEvent event;

    public GameActionsButton(JDA jda) {
        this.jda = jda;
    }

    public void setEvent(ButtonClickEvent event) {
        this.event = event;
    }

    public void setPlayers(Set<Player> players) {
        this.players = players;
    }


    /**
     * checks if activePlayer is currently playing a split hand
     *
     * @return true when active is playing a split hand. Otherwise false
     */
    private boolean isPlayerCurrentlySplitting() {
        return splitPlayers.containsValue(activePlayer) || splitPlayers.containsKey(activePlayer);
    }

    /**
     * checks whether the active player is the same player as the one given in param
     * @return true when the param player equals to active player. Otherwise false
     */
    public boolean isCommandFromCorrectPlayer(Player player) {
        if (player == null) {
            return false;
        }
        return player.equals(activePlayer);
    }

    /**
     * Creates embed of the current round
     *
     * @return MessageEmbed with all the info of the current round
     */
    private MessageEmbed createCurrentRoundEmbed() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Current Table");
        builder.setAuthor(jda.getUserByTag(BOT_TAG).getName(), null,
                jda.getUserByTag(BOT_TAG).getAvatarUrl());
        builder.addField(addActiveToNameWhenActivePlayer(dealer), dealer.getCurrentHand().toString(),
                false);
        for (Player p : players) {
            builder.addField(addActiveToNameWhenActivePlayer(p), p.getCurrentHand().toString(), false);
            Player splitPlayer = splitPlayers.get(p);
            if (splitPlayer != null) {
                builder.addField(addActiveToNameWhenActivePlayer(splitPlayer),
                        splitPlayer.getCurrentHand().toString(), false);
            }
        }
        return builder.build();
    }


    private String addActiveToNameWhenActivePlayer(Player player) {
        return player == activePlayer ? player.getNameNoTag() + " active" : player.getNameNoTag();
    }


    private ActionRow currentGameButtons(){
        List<Button> buttons = new ArrayList<>();
            Button doubleButton = Button.primary("double", "double");
            if (!allowedToDouble()) {
                doubleButton = doubleButton.withDisabled(true);
            }
            Button splitButton = Button.primary("split", "split");
            if (!allowedToSplit()) {
                splitButton = splitButton.withDisabled(true);
            }
            Button hitButton = Button.primary("hit", "hit");
            Button standButton = Button.primary("stand", "stand");

            buttons.add(doubleButton);
            buttons.add(splitButton);
            buttons.add(hitButton);
            buttons.add(standButton);
            return ActionRow.of(buttons);
    }
    /**
     * Creates embed of the current round. Active Player also have "active" next to his/her name
     */
    private void printCurrentGameAsDealer() {
        if (!event.isAcknowledged()){
            event.deferEdit().queue();
        }
        MessageEmbed messageEmbed = createCurrentRoundEmbed();
        event.getHook().editOriginalEmbeds(messageEmbed).setActionRows().completeAfter(1, TimeUnit.SECONDS);
    }
    private void printCurrentGameAsAcknowledgmentEvent(){
        MessageEmbed messageEmbed = createCurrentRoundEmbed();
        List<Button> buttons = new ArrayList<>();

            Button doubleButton = Button.primary("double", "double");
            if (!allowedToDouble()) {
                doubleButton = doubleButton.withDisabled(true);
            }
            Button splitButton = Button.primary("split", "split");
            if (!allowedToSplit()) {
                splitButton = splitButton.withDisabled(true);
            }
            Button hitButton = Button.primary("hit", "hit");
            Button standButton = Button.primary("stand", "stand");

            buttons.add(doubleButton);
            buttons.add(splitButton);
            buttons.add(hitButton);
            buttons.add(standButton);
        event.editMessageEmbeds(messageEmbed).setActionRow(buttons).complete();
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
        allHadBlackjack = false;
    }

    /**
     * hands out cards to players and dealer. Also sets up the playing order of round
     */
    public void setUp() {
        deck = new DeckUtility().generatePlayingDeck();
        for (Player player : players) {
            player.getCurrentHand().addCardToHand(deck.pop());
        }
        dealer.getCurrentHand().addCardToHand(deck.pop());

        for (Player player : players) {
            player.getCurrentHand().addCardToHand(deck.pop());
        }

        // for debugging
/*
        Player[] playerTestArray = players.toArray(new Player[0]);
        Hand playerHand = playerTestArray[0].getCurrentHand();
        playerHand.removeACardFromHand();
        playerHand.removeACardFromHand();
        playerHand.addCardToHand(new Card(10, "K"));
        playerHand.addCardToHand(new Card(11, "A"));
*/


        playersInGame.push(dealer);
        Player[] playerArray = players.toArray(new Player[0]);
        for (int i = playerArray.length - 1; i >= 0; i--) {
            playersInGame.push(playerArray[i]);
        }
        activePlayer = playersInGame.pop();

    }

    /**
     * checks if any player has a blackjack
     *
     * @return true when all player have blackjack, meaning its the turn of the dealer. Otherwise false
     */
    public boolean doAllPlayersHaveBlackJack() {
        int nrOfBlackjacks = 0;
        while (activePlayer != dealer) {
            Hand activePlayerCurrentHand = activePlayer.getCurrentHand();
            if (activePlayerCurrentHand.getHandSize() == 2 &&
                    activePlayerCurrentHand.getCurrentHandValue() == 21) {
                activePlayerCurrentHand.setBlackJack(true);
                activePlayer = playersInGame.pop();
                nrOfBlackjacks++;
            } else {
                break;
            }
        }
        if (nrOfBlackjacks == players.size()) {
            allHadBlackjack = true;
        }

        if (activePlayer == dealer) {
            dealerPlay();
            return true;
        }
        event.getHook().editOriginal("").setEmbeds(createCurrentRoundEmbed()).setActionRows(currentGameButtons()).complete();
        return false;
    }

    /**
     * dealer plays till he gets 17 or higher. Should only be called when the activePlayer is the dealer.
     * @throws IllegalStateException when function is not called with dealer as active player
     */

    public void dealerPlay() {
        if (activePlayer != dealer) {
            throw new IllegalStateException("Active Player should be dealer right now");
        }
        if (allHadBlackjack){
            printCurrentGameAsDealer();
        }
        Hand activePlayerCurrentHand = activePlayer.getCurrentHand();
        while (activePlayerCurrentHand.getCurrentHandValue() < 17) {
            try {
                activePlayerCurrentHand.addCardToHand(deck.pop());

                if (activePlayerCurrentHand.getHandSize() == 2 &&
                        activePlayerCurrentHand.getCurrentHandValue() == 21) {
                    activePlayerCurrentHand.setBlackJack(true);
                }

                if (activePlayerCurrentHand.getCurrentHandValue() > 21) {
                    activePlayerCurrentHand.setBusted(true);
                }
                printCurrentGameAsDealer();

                // everyone already won or lost
                if (allHadBlackjack || nrOfBustedPlayers == (splitPlayers.size() + players.size())) {
                    Thread.sleep(500);
                    printCurrentGameAsDealer();
                    return;
                }

                Thread.sleep(500);
            } catch (InterruptedException e) {
                logger.error("Dealer got interrupted");
                Thread.currentThread().interrupt();
            }
        }


    }


    /**
     * next players turn. when next player has black jack recursively goes to next player till its dealer's turn
     */
    public void nextPlayersTurn() {
        activePlayer = playersInGame.pop();
        if (activePlayer == dealer) {
            return;
        }
        Hand activePlayerCurrentHand = activePlayer.getCurrentHand();
        if (activePlayerCurrentHand.getHandSize() == 2 &&
                activePlayerCurrentHand.getCurrentHandValue() == 21) {
            activePlayerCurrentHand.setBlackJack(true);
            nextPlayersTurn();
        }

    }

    /**
     * Player gets another card. Prints the current round state afterwards
     *
     * @return true when all players played. Otherwise false
     */
    public boolean hit() {
        Hand activePlayerCurrentHand = activePlayer.getCurrentHand();
        activePlayerCurrentHand.addCardToHand(deck.pop());

        if (activePlayerCurrentHand.getCurrentHandValue() > 21) {
            activePlayerCurrentHand.setBusted(true);
            nrOfBustedPlayers++;
            nextPlayersTurn();
        } else if (activePlayerCurrentHand.getCurrentHandValue() == 21) {
            activePlayer = playersInGame.pop();
        }

        if (activePlayer == dealer) {
            printCurrentGameAsDealer();
            dealerPlay();
            return true;
        }
        printCurrentGameAsAcknowledgmentEvent();
        return false;
    }

    /**
     * Player stands. Prints the current round state afterwards
     *
     * @return true when all players played. Otherwise false
     */
    public boolean stand() {
        nextPlayersTurn();
        if (activePlayer == dealer) {
            printCurrentGameAsDealer();
            dealerPlay();
            return true;
        }
        printCurrentGameAsAcknowledgmentEvent();
        return false;
    }

    /**
     * checks whether the active player is allowed to double.
     *
     * @return true if allowed. Otherwise false
     */
    public boolean allowedToDouble() {
        return activePlayer.getCurrentHand().getHandSize() == 2 && !isPlayerCurrentlySplitting();
    }

    /**
     * Player gets another card and doubles his/her bet. Prints the current round state afterwards
     *
     * @return true when all players played. Otherwise false
     */
    public boolean doubleMove() {
        activePlayer.reduceMoney(activePlayer.getBetAmount());
        activePlayer.setBetAmount(activePlayer.getBetAmount() * 2);
        Hand activePlayerCurrentHand = activePlayer.getCurrentHand();
        activePlayerCurrentHand.addCardToHand(deck.pop());
        if (activePlayerCurrentHand.getCurrentHandValue() > 21) {
            activePlayerCurrentHand.setBusted(true);
            nrOfBustedPlayers++;
        }
        nextPlayersTurn();
        if (activePlayer == dealer) {
            printCurrentGameAsDealer();
            dealerPlay();
            return true;
        }
        printCurrentGameAsAcknowledgmentEvent();
        return false;
    }

    /**
     * checks whether the active player is allowed to split his/her hand
     *
     * @return true when the play can split. Otherwise false
     */
    public boolean allowedToSplit() {
        return activePlayer.getCurrentHand().isHandSplittable() && !isPlayerCurrentlySplitting();
    }

    /**
     * Player splits his/her hand. Player will be put into the game as playerName split right after the first hand, so he/she can play both of the hands subsequently. Prints the current round state afterwards
     */
    public void split() {
        Hand activePlayerCurrentHand = activePlayer.getCurrentHand();
        activePlayer.reduceMoney(activePlayer.getBetAmount());

        Player fakePlayer = new Player(activePlayer.getUuid(), activePlayer.getName());
        fakePlayer.getCurrentHand().addCardToHand(activePlayerCurrentHand.removeACardFromHand());
        activePlayerCurrentHand.addCardToHand(deck.pop());
        fakePlayer.getCurrentHand().addCardToHand(deck.pop());
        splitPlayers.put(activePlayer, fakePlayer);
        playersInGame.push(fakePlayer);

        if (activePlayerCurrentHand.getCurrentHandValue() == 21) {
            activePlayerCurrentHand.setBlackJack(true);
            nextPlayersTurn();
        }
        printCurrentGameAsAcknowledgmentEvent();
    }

    /**
     * calculates the win/loss for all players and saves them into the player instances.
     */
    public void calculatePayout() {
        calculateSplitPlayersPayout();
        calculateNonSplitPlayersPayout();
    }

    private void calculateSplitPlayersPayout() {
        // all the cases where a player split
        for (Map.Entry<Player, Player> entry : splitPlayers.entrySet()) {
            Player fakePlayer = entry.getValue();
            Hand fakePlayerHand = fakePlayer.getCurrentHand();
            Player realPlayer = entry.getKey();

            // blackjack
            if (fakePlayerHand.isBlackJack() && !dealerHand.isBlackJack()) {
                realPlayer.addMoney(realPlayer.getBetAmount() * 2.5);
                realPlayer.addWonAmount(realPlayer.getBetAmount() * 2.5);

            }
            // won
            else if ((!fakePlayerHand.isBusted() && dealerHand.isBusted()) ||
                    (!fakePlayerHand.isBusted() && !dealerHand.isBusted() &&
                            fakePlayerHand.getCurrentHandValue() > dealerHand.getCurrentHandValue())) {
                realPlayer.addMoney(realPlayer.getBetAmount() * 2);
                realPlayer.addWonAmount(realPlayer.getBetAmount() * 2);

            }
            // push
            else if (!fakePlayerHand.isBusted() && !dealerHand.isBusted() &&
                    fakePlayerHand.getCurrentHandValue() == dealerHand.getCurrentHandValue()) {
                realPlayer.addMoney(realPlayer.getBetAmount());
            }
            // lost
            else {
                realPlayer.addWonAmount(realPlayer.getBetAmount() * (-1));
            }


        }
    }

    private void calculateNonSplitPlayersPayout() {
        for (Player p : players) {
            Hand pHand = p.getCurrentHand();
            //blackjack
            if (pHand.isBlackJack() && !dealerHand.isBlackJack()) {
                p.addWonAmount(p.getBetAmount() * 2.5);
                p.addMoney(p.getBetAmount() * 2.5);
            }
            // won
            else if ((!pHand.isBusted() && dealerHand.isBusted()) ||
                    (!pHand.isBusted() && !dealerHand.isBusted() &&
                            pHand.getCurrentHandValue() > dealerHand.getCurrentHandValue())) {
                p.addMoney(p.getBetAmount() * 2);
                p.addWonAmount(p.getBetAmount() * 2);

            }
            // push
            else if (!pHand.isBusted() && !dealerHand.isBusted() &&
                    pHand.getCurrentHandValue() == dealerHand.getCurrentHandValue()) {
                p.addMoney(p.getBetAmount());
            }
            // lost
            else {
                p.addWonAmount(p.getBetAmount() * (-1));
            }
        }
    }


}
