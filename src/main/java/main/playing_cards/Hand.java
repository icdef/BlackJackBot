package main.playing_cards;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private final List<Card> currentHand = new ArrayList<>();
    private int amountOfAces = 0;
    private boolean blackJack = false;
    private boolean busted = false;


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
        if (currentHand.size() != 2){
            throw new IllegalStateException("Current Hand from player should be 2 but it is "+ currentHand.size());
        }
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
        if (currentHand.size() != 2) {
            return false;
        }
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

    public void resetHand() {
        currentHand.clear();
        amountOfAces = 0;
        blackJack = false;
        busted = false;
    }

    @Override
    public String toString() {
        if (blackJack) {
            return String.format("Cards: %s (%d). That's BlackJack!", getCurrentHandString(),
                    getCurrentHandValue());
        }
        return busted ?
                String.format("Cards: %s (%d) busted", getCurrentHandString(), getCurrentHandValue())
                : String.format("Cards: %s (%d)", getCurrentHandString(), getCurrentHandValue());

    }
}
