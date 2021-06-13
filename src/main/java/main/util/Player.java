package main.util;

import main.playing_cards.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player {

    private final String name;
    private String uuid = "";
    private final List<Card> currentHand = new ArrayList<>();
    private int amountOfAces = 0;
    private double money = 0;
    private double betAmount = 0;
    private double wonAmount = 0;
    private boolean blackJack = false;
    private boolean busted = false;

    public Player(String uuid, String name, double money) {
        this.uuid = uuid;
        this.name = name;
        this.money = money;
    }

    public Player(String name) {
        this.name = name;
    }
    public Player(String uuid, String name){
        this.uuid = uuid;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public double getWonAmount() {
        return wonAmount;
    }

    public void addWonAmount(double wonAmount) {
        this.wonAmount += wonAmount;
    }


    public double getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(double betAmount) {
        this.betAmount = betAmount;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void reduceMoney(double money) {
        this.money -= money;
    }

    public void addMoney(double money) {
        this.money += money;
    }

    public boolean isBusted() {
        return busted;
    }

    public void setBusted(boolean busted) {
        this.busted = busted;
    }

    public boolean isBlackJack() {
        return blackJack;
    }

    public void setBlackJack(boolean blackJack) {
        this.blackJack = blackJack;
    }

    public void addCardToHand(Card c) {
        if (c.getValue() == 11) {
            amountOfAces++;
        }
        currentHand.add(c);
    }

    // only gets called with players with 2 cards
    public Card removeACardFromHand() {
        Card card = currentHand.remove(0);
        if (card.getValue() == 11) {
            amountOfAces--;
        }
        return card;
    }

    public int getHandSize() {
        return currentHand.size();
    }

    public boolean isHandSplittable() {
        if (currentHand.size() != 2)
            return false;
        return currentHand.get(0).equals(currentHand.get(1));
    }

    public String getCurrentHandString() {
        StringBuilder s = new StringBuilder();
        for (Card c : currentHand) {
            s.append(c).append(", ");
        }
        return s.substring(0, s.length() - 2);
    }

    public int getCurrentHandValue() {
        int sum = 0;
        for (Card c : currentHand) {
            sum += c.getValue();
        }

        int helperAceAmount = amountOfAces;
        while (helperAceAmount > 0 && sum > 21) {
            sum -= 10;
            helperAceAmount--;
        }
        return sum;
    }

    public void resetPlayer() {
        currentHand.clear();
        wonAmount = 0;
        betAmount = 0;
        amountOfAces = 0;
        blackJack = false;
        busted = false;
    }

    public String getNameNoTag() {
        return name.split("#")[0];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return uuid.equals(player.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        if (blackJack) {
            return String.format("Cards: %s (%d). That's BlackJack!", getCurrentHandString(), getCurrentHandValue());
        }
        return busted ?
                String.format("Cards: %s (%d) busted", getCurrentHandString(), getCurrentHandValue())
                : String.format("Cards: %s (%d)", getCurrentHandString(), getCurrentHandValue());

    }


}
