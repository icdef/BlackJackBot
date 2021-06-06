package main.playing_cards;

import java.util.*;

public class DeckUtility {


    public Deque<Card> generatePlayingDeck() {
        List<Card> cards = new ArrayList<>();
        for (int i = 2; i <= 10; i++) {
            for (int j = 0; j < 4 * 4; j++) {
                cards.add(new Card(i, "" + i));
            }
        }
        for (int i = 0; i < 4 * 4; i++) {
            cards.add(new Card(10, "J"));
            cards.add(new Card(10, "Q"));
            cards.add(new Card(10, "K"));
            cards.add(new Card(11, "A"));
        }
        Collections.shuffle(cards);
        return new ArrayDeque<>(cards);
    }
}
