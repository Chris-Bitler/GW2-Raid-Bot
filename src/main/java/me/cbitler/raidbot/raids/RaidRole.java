package me.cbitler.raidbot.raids;

public class RaidRole {
    int amount;
    String name;

    public RaidRole(int amount, String name) {
        this.amount = amount;
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }
}
