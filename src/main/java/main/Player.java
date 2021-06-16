package main;

import main.playing_cards.Card;
import main.playing_cards.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player {

    private final String name;
    private String uuid = "";

    private double money = 0;
    private double betAmount = 0;
    private double wonAmount = 0;
    private Hand currentHand = new Hand();

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

    public Hand getCurrentHand() {
        return currentHand;
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



    public void reduceMoney(double money) {
        this.money -= money;
    }

    public void addMoney(double money) {
        this.money += money;
    }



    public void resetPlayer() {
        wonAmount = 0;
        betAmount = 0;
        currentHand.resetHand();
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




}
