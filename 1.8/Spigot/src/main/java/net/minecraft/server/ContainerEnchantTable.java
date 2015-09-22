package net.minecraft.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftInventoryEnchanting;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.plugin.PluginManager;

public class ContainerEnchantTable extends Container
{
  public ContainerEnchantTableInventory enchantSlots = new ContainerEnchantTableInventory(this, "Enchant", true, 2);
  private World world;
  private BlockPosition position;
  private Random k = new Random();
  public int f;
  public int[] costs = new int[3];
  public int[] h = { -1, -1, -1 };

  private CraftInventoryView bukkitEntity = null;
  private Player player;

  public ContainerEnchantTable(PlayerInventory playerinventory, World world, BlockPosition blockposition)
  {
    this.world = world;
    this.position = blockposition;
    this.f = playerinventory.player.ci();
    a(new SlotEnchant(this, this.enchantSlots, 0, 15, 47));
    a(new SlotEnchantLapis(this, this.enchantSlots, 1, 35, 47));

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 9; j++) {
        a(new Slot(playerinventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
      }
    }

    for (int i = 0; i < 9; i++) {
      a(new Slot(playerinventory, i, 8 + i * 18, 142));
    }

    this.player = ((Player)playerinventory.player.getBukkitEntity());
    this.enchantSlots.player = this.player;
  }

  public void addSlotListener(ICrafting icrafting)
  {
    super.addSlotListener(icrafting);
    icrafting.setContainerData(this, 0, this.costs[0]);
    icrafting.setContainerData(this, 1, this.costs[1]);
    icrafting.setContainerData(this, 2, this.costs[2]);
    icrafting.setContainerData(this, 3, this.f & 0xFFFFFFF0);
    icrafting.setContainerData(this, 4, this.h[0]);
    icrafting.setContainerData(this, 5, this.h[1]);
    icrafting.setContainerData(this, 6, this.h[2]);
  }

  public void b() {
    super.b();

    for (int i = 0; i < this.listeners.size(); i++) {
      ICrafting icrafting = (ICrafting)this.listeners.get(i);

      icrafting.setContainerData(this, 0, this.costs[0]);
      icrafting.setContainerData(this, 1, this.costs[1]);
      icrafting.setContainerData(this, 2, this.costs[2]);
      icrafting.setContainerData(this, 3, this.f & 0xFFFFFFF0);
      icrafting.setContainerData(this, 4, this.h[0]);
      icrafting.setContainerData(this, 5, this.h[1]);
      icrafting.setContainerData(this, 6, this.h[2]);
    }
  }

  public void a(IInventory iinventory)
  {
    if (iinventory == this.enchantSlots) {
      ItemStack itemstack = iinventory.getItem(0);

      if (itemstack != null) {
        if (!this.world.isStatic) {
          int i = 0;

          for (int j = -1; j <= 1; j++) {
            for (int k = -1; k <= 1; k++) {
              if (((j != 0) || (k != 0)) && (this.world.isEmpty(this.position.a(k, 0, j))) && (this.world.isEmpty(this.position.a(k, 1, j)))) {
                if (this.world.getType(this.position.a(k * 2, 0, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                  i++;
                }

                if (this.world.getType(this.position.a(k * 2, 1, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                  i++;
                }

                if ((k != 0) && (j != 0)) {
                  if (this.world.getType(this.position.a(k * 2, 0, j)).getBlock() == Blocks.BOOKSHELF) {
                    i++;
                  }

                  if (this.world.getType(this.position.a(k * 2, 1, j)).getBlock() == Blocks.BOOKSHELF) {
                    i++;
                  }

                  if (this.world.getType(this.position.a(k, 0, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                    i++;
                  }

                  if (this.world.getType(this.position.a(k, 1, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                    i++;
                  }
                }
              }
            }
          }

          this.k.setSeed(this.f);

          for (int j = 0; j < 3; j++) {
            this.costs[j] = EnchantmentManager.a(this.k, j, i, itemstack);
            this.h[j] = -1;
            if (this.costs[j] < j + 1) {
              this.costs[j] = 0;
            }

          }

          CraftItemStack item = CraftItemStack.asCraftMirror(itemstack);
          PrepareItemEnchantEvent event = new PrepareItemEnchantEvent(this.player, getBukkitView(), this.world.getWorld().getBlockAt(this.position.getX(), this.position.getY(), this.position.getZ()), item, this.costs, i);
          event.setCancelled(!itemstack.v());
          this.world.getServer().getPluginManager().callEvent(event);

          if (event.isCancelled()) {
            for (i = 0; i < 3; i++) {
              this.costs[i] = 0;
            }
            return;
          }

          for (int j = 0; j < 3; j++) {
            if (this.costs[j] > 0) {
              List list = a(itemstack, j, this.costs[j]);

              if ((list != null) && (!list.isEmpty())) {
                WeightedRandomEnchant weightedrandomenchant = (WeightedRandomEnchant)list.get(this.k.nextInt(list.size()));

                this.h[j] = (weightedrandomenchant.enchantment.id | weightedrandomenchant.level << 8);
              }
            }
          }

          b();
        }
      }
      else for (int i = 0; i < 3; i++) {
          this.costs[i] = 0;
          this.h[i] = -1;
        }
    }
  }

  public boolean a(EntityHuman entityhuman, int i)
  {
    ItemStack itemstack = this.enchantSlots.getItem(0);
    ItemStack itemstack1 = this.enchantSlots.getItem(1);
    int j = i + 1;

    if (((itemstack1 == null) || (itemstack1.count < j)) && (!entityhuman.abilities.canInstantlyBuild))
      return false;
    if ((this.costs[i] > 0) && (itemstack != null) && (((entityhuman.expLevel >= j) && (entityhuman.expLevel >= this.costs[i])) || (entityhuman.abilities.canInstantlyBuild))) {
      if (!this.world.isStatic) {
        List list = a(itemstack, i, this.costs[i]);

        if (list == null) {
          list = new ArrayList();
        }

        boolean flag = itemstack.getItem() == Items.BOOK;

        if (list != null)
        {
          Map enchants = new HashMap();
          for (Iterator localIterator1 = list.iterator(); localIterator1.hasNext(); ) { Object obj = localIterator1.next();
            WeightedRandomEnchant instance = (WeightedRandomEnchant)obj;
            enchants.put(org.bukkit.enchantments.Enchantment.getById(instance.enchantment.id), Integer.valueOf(instance.level));
          }
          CraftItemStack item = CraftItemStack.asCraftMirror(itemstack);

          EnchantItemEvent event = new EnchantItemEvent((Player)entityhuman.getBukkitEntity(), getBukkitView(), this.world.getWorld().getBlockAt(this.position.getX(), this.position.getY(), this.position.getZ()), item, this.costs[i], enchants, i);
          this.world.getServer().getPluginManager().callEvent(event);

          int level = event.getExpLevelCost();
          if ((event.isCancelled()) || ((level > entityhuman.expLevel) && (!entityhuman.abilities.canInstantlyBuild)) || (event.getEnchantsToAdd().isEmpty())) {
            return false;
          }

          if (flag) {
            itemstack.setItem(Items.ENCHANTED_BOOK);
          }

          for (Map.Entry entry : event.getEnchantsToAdd().entrySet()) {
            try {
              if (flag) {
                int enchantId = ((org.bukkit.enchantments.Enchantment)entry.getKey()).getId();
                if (Enchantment.getById(enchantId) != null)
                {
                  WeightedRandomEnchant enchantment = new WeightedRandomEnchant(Enchantment.getById(enchantId), ((Integer)entry.getValue()).intValue());
                  Items.ENCHANTED_BOOK.a(itemstack, enchantment);
                }
              } else { item.addUnsafeEnchantment((org.bukkit.enchantments.Enchantment)entry.getKey(), ((Integer)entry.getValue()).intValue()); }

            }
            catch (IllegalArgumentException localIllegalArgumentException)
            {
            }
          }
          entityhuman.b(level);

          if (!entityhuman.abilities.canInstantlyBuild) {
            itemstack1.count -= j;
            if (itemstack1.count <= 0) {
              this.enchantSlots.setItem(1, null);
            }
          }

          this.enchantSlots.update();
          this.f = entityhuman.ci();
          a(this.enchantSlots);
        }
      }

      return true;
    }
    return false;
  }

  private List a(ItemStack itemstack, int i, int j)
  {
    this.k.setSeed(this.f + i);
    List list = EnchantmentManager.b(this.k, itemstack, j);

    if ((itemstack.getItem() == Items.BOOK) && (list != null) && (list.size() > 1)) {
      list.remove(this.k.nextInt(list.size()));
    }

    return list;
  }

  public void b(EntityHuman entityhuman) {
    super.b(entityhuman);
    if (!this.world.isStatic)
      for (int i = 0; i < this.enchantSlots.getSize(); i++) {
        ItemStack itemstack = this.enchantSlots.splitWithoutUpdate(i);

        if (itemstack != null)
          entityhuman.drop(itemstack, false);
      }
  }

  public boolean a(EntityHuman entityhuman)
  {
    if (!this.checkReachable) return true;
    return this.world.getType(this.position).getBlock() == Blocks.ENCHANTING_TABLE;
  }

  public ItemStack b(EntityHuman entityhuman, int i) {
    ItemStack itemstack = null;
    Slot slot = (Slot)this.c.get(i);

    if ((slot != null) && (slot.hasItem())) {
      ItemStack itemstack1 = slot.getItem();

      itemstack = itemstack1.cloneItemStack();
      if (i == 0) {
        if (!a(itemstack1, 2, 38, true))
          return null;
      }
      else if (i == 1) {
        if (!a(itemstack1, 2, 38, true))
          return null;
      }
      else if ((itemstack1.getItem() == Items.DYE) && (EnumColor.fromInvColorIndex(itemstack1.getData()) == EnumColor.BLUE)) {
        if (!a(itemstack1, 1, 2, true))
          return null;
      }
      else {
        if ((((Slot)this.c.get(0)).hasItem()) || (!((Slot)this.c.get(0)).isAllowed(itemstack1))) {
          return null;
        }

        if ((itemstack1.hasTag()) && (itemstack1.count == 1)) {
          ((Slot)this.c.get(0)).set(itemstack1.cloneItemStack());
          itemstack1.count = 0;
        } else if (itemstack1.count >= 1)
        {
          ItemStack clone = itemstack1.cloneItemStack();
          clone.count = 1;
          ((Slot)this.c.get(0)).set(clone);

          itemstack1.count -= 1;
        }
      }

      if (itemstack1.count == 0)
        slot.set(null);
      else {
        slot.f();
      }

      if (itemstack1.count == itemstack.count) {
        return null;
      }

      slot.a(entityhuman, itemstack1);
    }

    return itemstack;
  }

  public CraftInventoryView getBukkitView()
  {
    if (this.bukkitEntity != null) {
      return this.bukkitEntity;
    }

    CraftInventoryEnchanting inventory = new CraftInventoryEnchanting(this.enchantSlots);
    this.bukkitEntity = new CraftInventoryView(this.player, inventory, this);
    return this.bukkitEntity;
  }
}