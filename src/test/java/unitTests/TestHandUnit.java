package java.unitTests;



import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import playing_cards.Card;
import playing_cards.Hand;


import static org.junit.jupiter.api.Assertions.*;

public class TestHandUnit {

    private static Hand hand;
    private static Card jack;
    private static Card ace;
    private static Card nine;

    @BeforeAll
    public static void setUp() {
        jack = new Card(10,"J");
        ace = new Card(11,"A");
        nine = new Card(9,"9");
    }
    @BeforeEach
    public void initHand(){
        hand = new Hand();
    }

    @Test
    void addingStarterCardsToHand_ReturnsCorrectValues(){
        hand.addCardToHand(jack);
        assertEquals(10,hand.getCurrentHandValue());
        hand.addCardToHand(nine);
        assertEquals(19,hand.getCurrentHandValue());
    }
    @Test
    void addingAceToHandValueOver21_returnsCorrectValue(){
        hand.addCardToHand(jack);
        hand.addCardToHand(nine);
        hand.addCardToHand(ace);
        assertEquals(20,hand.getCurrentHandValue());
    }

    @Test
    void removeACardFromHand_ReturnsRemovedCard(){
        hand.addCardToHand(jack);
        hand.addCardToHand(nine);
        assertEquals(nine,hand.removeACardFromHand());
    }

    @Test
    void removeACardFromHandWithOnlyOneCardInHand_shouldThrowIllegalStateException(){
        hand.addCardToHand(jack);
        assertThrows(IllegalStateException.class, () -> hand.removeACardFromHand());
    }

    @Test
    void returnCorrectHandSize(){
        assertEquals(0,hand.getHandSize());
        hand.addCardToHand(jack);
        assertEquals(1,hand.getHandSize());
        hand.addCardToHand(jack);
        assertEquals(2,hand.getHandSize());
    }

    @Test
    void handSplittableWithOneCard_returnsFalse(){
        hand.addCardToHand(jack);
        assertFalse(hand.isHandSplittable());
    }
    @Test
    void handSplittableWithTwoDifferentCards_returnsFalse(){
        hand.addCardToHand(jack);
        hand.addCardToHand(nine);
        assertFalse(hand.isHandSplittable());
    }
    @Test
    void handSplittableWithTwoIdenticalCards_returnsTrue(){
        hand.addCardToHand(jack);
        hand.addCardToHand(jack);
        assertTrue(hand.isHandSplittable());
    }
    @Test
    void currentHandString_returnsCorrectString(){
        hand.addCardToHand(jack);
        assertEquals("J",hand.getCurrentHandString());
        hand.addCardToHand(nine);
        assertEquals("J, 9",hand.getCurrentHandString());
    }
    
    @Test
    void handResetCorrectly(){
        hand.addCardToHand(ace);
        hand.addCardToHand(nine);
        assertEquals(2,hand.getHandSize());
        assertEquals(20,hand.getCurrentHandValue());
        hand.setBlackJack(true);
        hand.setBusted(true);

        hand.resetHand();
        assertFalse(hand.isBlackJack());
        assertFalse(hand.isBusted());
        assertEquals(0,hand.getHandSize());

        hand.addCardToHand(jack);
        hand.addCardToHand(jack);
        hand.addCardToHand(nine);
        // checks if amountOfAces also got reset
        assertEquals(29,hand.getCurrentHandValue());
    }

}
