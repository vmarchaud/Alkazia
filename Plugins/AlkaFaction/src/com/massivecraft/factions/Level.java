package com.massivecraft.factions;

import org.bukkit.inventory.ItemStack;

import com.massivecraft.factions.zcore.persist.Entity;

public class Level extends Entity {
    private int level;
    private String[] permissions;
    private ItemStack[] items;
    private int money;
    private int maxClaims;
    private int maxMembers;
    private int maxPower;
    private double shieldProtector;
    private double xp;

    public int getLevel() {
        return this.level;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public String[] getPermissions() {
        return this.permissions;
    }

    public void setPermissions(final String[] permissions) {
        this.permissions = permissions;
    }

    public ItemStack[] getItems() {
        return this.items;
    }

    public void setItems(final ItemStack[] items) {
        this.items = items;
    }

    public int getMoney() {
        return this.money;
    }

    public void setMoney(final int money) {
        this.money = money;
    }

    public int getMaxClaims() {
        return this.maxClaims;
    }

    public void setMaxClaims(final int maxClaims) {
        this.maxClaims = maxClaims;
    }

    public int getMaxMembers() {
        return this.maxMembers;
    }

    public void setMaxMembers(final int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public int getMaxPower() {
        return this.maxPower;
    }

    public void setMaxPower(final int maxPower) {
        this.maxPower = maxPower;
    }

    public double getShieldProtector() {
        return this.shieldProtector;
    }

    public void setShieldProtector(final double shieldProtector) {
        this.shieldProtector = shieldProtector;
    }

    public double getXP() {
        return this.xp;
    }

    public void setXP(final double xp) {
        this.xp = xp;
    }
}
