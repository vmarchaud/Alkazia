package me.rellynn.exchange.utils;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * ItemFactory.java
 *
 * @author Rellynn
 */
public class ItemFactory implements Listener {

    private final int amount;
    private final short damage;
    private final Material material;
    private final List<String> lores = new ArrayList<>();
    private ItemStack item;
    private String title;

    /**
     * Constuire un item avec seulement un matériel
     *
     * @param material Le matériel
     */
    public ItemFactory(Material material) {
        this(material, 1, (short) 0);
    }

    /**
     * Construire un item avec un matériel et un nombre
     *
     * @param material Le matériel
     * @param amount   Le nombre
     */
    public ItemFactory(Material material, int amount) {
        this(material, amount, (short) 0);
    }

    /**
     * Construire un item avec un matériel et une durabilité
     *
     * @param material   Le matériel
     * @param durability La durabilité
     */
    public ItemFactory(Material material, short durability) {
        this(material, 1, durability);
    }

    /**
     * Construire un item avec un matériel, un nombre et une durabilité
     *
     * @param material Le matériel
     * @param amount   Le nombre
     * @param damage   La durabilité
     */
    public ItemFactory(Material material, int amount, short damage) {
        this.material = material;
        this.amount = amount;
        this.damage = damage;
    }

    /**
     * Construire l'item
     *
     * @return l'item construit
     */
    public ItemStack build() {
        if (material == null) {
            throw new NullPointerException("Material cannot be NULL!");
        } else if (item == null) {
            item = new ItemStack(material, amount, damage);
            ItemMeta meta = item.getItemMeta();
            if (title != null) {
                meta.setDisplayName(title);
            }
            if (!lores.isEmpty()) {
                meta.setLore(lores);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Définir un titre à l'item
     *
     * @param title Le titre à donner
     * @return Cette instance
     */
    public ItemFactory setTitle(String title) {
        this.title = title;
        return this;
    }

}
