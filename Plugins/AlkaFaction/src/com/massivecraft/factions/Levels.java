package com.massivecraft.factions;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.massivecraft.factions.zcore.persist.EntityCollection;

public class Levels extends EntityCollection<Level> {
    public static Levels i = new Levels();

    P p = P.p;

    private Levels() {
        super(Level.class, new CopyOnWriteArrayList<Level>(), new ConcurrentHashMap<String, Level>(), new File(P.p.getDataFolder(), "levels.json"), P.p.gson);
    }

    @Override
    public Type getMapType() {
        return new TypeToken<Map<String, Level>>() {}.getType();
    }

    @Override
    public boolean loadFromDisc() {
        if (!super.loadFromDisc()) return false;

        if (!this.getNextId().equals("100")) {
            for (int i = 1; i < 101; i++) {
                final Level level = this.create(String.valueOf(i - 1));
                if (i == 1) {
                    final ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
                    final ItemMeta meta = chestplate.getItemMeta();
                    meta.setDisplayName(ChatColor.GREEN + "Example");
                    meta.setLore(new ArrayList<String>() {
                        {
                            this.add("Example lore");
                        }
                    });
                    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, false);
                    chestplate.setItemMeta(meta);
                    level.setItems(new ItemStack[] { new ItemStack(Material.DIAMOND, 15), chestplate });
                    level.setPermissions(new String[] { "example.example", "factions.example" });
                } else {
                    level.setItems(new ItemStack[0]);
                    level.setPermissions(new String[0]);
                }
                level.setXP(100 * (Math.pow(1.2D, i)));
                level.setLevel(i);
                level.setMoney(0);
                level.setMaxClaims(i + 1);
                level.setMaxPower((i + 1) * 10);
                level.setShieldProtector(0.1 * (i / 20) < 0 ? 0 : 0.1 * (i / 20));
            }
        }

        return true;
    }

    // ----------------------------------------------//
    // GET
    // ----------------------------------------------//

    @Override
    public Level get(final String id) {
        // if (!this.exists(id)) this.p.log(java.util.logging.Level.WARNING, "Non existing levelId " + id + " requested!");
        return super.get(id);
    }
}
