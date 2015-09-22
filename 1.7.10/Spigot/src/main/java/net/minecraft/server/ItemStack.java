package net.minecraft.server;

import java.text.DecimalFormat;
// CraftBukkit start
import java.util.List;
import java.util.Random;

import net.minecraft.util.com.google.common.collect.HashMultimap;
import net.minecraft.util.com.google.common.collect.Multimap;

import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.world.StructureGrowEvent;

// CraftBukkit end

public final class ItemStack {

	public static final DecimalFormat a = new DecimalFormat("#.###");
	public int count;
	public int c;
	private Item item;
	public NBTTagCompound tag;
	private int damage;
	private EntityItemFrame g;

	public ItemStack(Block block) {
		this(block, 1);
	}

	public ItemStack(Block block, int i) {
		this(block, i, 0);
	}

	public ItemStack(Block block, int i, int j) {
		this(Item.getItemOf(block), i, j);
	}

	public ItemStack(Item item) {
		this(item, 1);
	}

	public ItemStack(Item item, int i) {
		this(item, i, 0);
	}

	public ItemStack(Item item, int i, int j) {
		this.item = item;
		count = i;
		// CraftBukkit start - Pass to setData to do filtering
		setData(j);
		//this.damage = j;
		//if (this.damage < 0) {
		//    this.damage = 0;
		//}
		// CraftBukkit end
	}

	public static ItemStack createStack(NBTTagCompound nbttagcompound) {
		ItemStack itemstack = new ItemStack();

		itemstack.c(nbttagcompound);
		return itemstack.getItem() != null ? itemstack : null;
	}

	private ItemStack() {
	}

	public ItemStack a(int i) {
		ItemStack itemstack = new ItemStack(item, i, damage);

		if (tag != null) {
			itemstack.tag = (NBTTagCompound) tag.clone();
		}

		count -= i;
		return itemstack;
	}

	public Item getItem() {
		return item;
	}

	public boolean placeItem(EntityHuman entityhuman, World world, int i, int j, int k, int l, float f, float f1, float f2) {
		// CraftBukkit start - handle all block place event logic here
		int data = getData();
		int count = this.count;

		if (!(getItem() instanceof ItemBucket)) { // if not bucket
			world.captureBlockStates = true;
			// special case bonemeal
			if (getItem() instanceof ItemDye && getData() == 15) {
				Block block = world.getType(i, j, k);
				if (block == Blocks.SAPLING || block instanceof BlockMushroom) {
					world.captureTreeGeneration = true;
				}
			}
		}
		boolean flag = getItem().interactWith(this, entityhuman, world, i, j, k, l, f, f1, f2);
		int newData = getData();
		int newCount = this.count;
		this.count = count;
		setData(data);
		world.captureBlockStates = false;
		if (flag && world.captureTreeGeneration && world.capturedBlockStates.size() > 0) {
			world.captureTreeGeneration = false;
			Location location = new Location(world.getWorld(), i, j, k);
			TreeType treeType = BlockSapling.treeType;
			BlockSapling.treeType = null;
			List<BlockState> blocks = (List<BlockState>) world.capturedBlockStates.clone();
			world.capturedBlockStates.clear();
			StructureGrowEvent event = null;
			if (treeType != null) {
				event = new StructureGrowEvent(location, treeType, false, (Player) entityhuman.getBukkitEntity(), blocks);
				org.bukkit.Bukkit.getPluginManager().callEvent(event);
			}
			if (event == null || !event.isCancelled()) {
				// Change the stack to its new contents if it hasn't been tampered with.
				if (this.count == count && getData() == data) {
					setData(newData);
					this.count = newCount;
				}
				for (BlockState blockstate : blocks) {
					blockstate.update(true);
				}
			}

			return flag;
		}
		world.captureTreeGeneration = false;

		if (flag) {
			org.bukkit.event.block.BlockPlaceEvent placeEvent = null;
			List<BlockState> blocks = (List<BlockState>) world.capturedBlockStates.clone();
			world.capturedBlockStates.clear();
			if (blocks.size() > 1) {
				placeEvent = org.bukkit.craftbukkit.event.CraftEventFactory.callBlockMultiPlaceEvent(world, entityhuman, blocks, i, j, k);
			} else if (blocks.size() == 1) {
				placeEvent = org.bukkit.craftbukkit.event.CraftEventFactory.callBlockPlaceEvent(world, entityhuman, blocks.get(0), i, j, k);
			}

			if (placeEvent != null && (placeEvent.isCancelled() || !placeEvent.canBuild())) {
				flag = false; // cancel placement
				// revert back all captured blocks
				for (BlockState blockstate : blocks) {
					blockstate.update(true, false);
				}
			} else {
				// Change the stack to its new contents if it hasn't been tampered with.
				if (this.count == count && getData() == data) {
					setData(newData);
					this.count = newCount;
				}
				for (BlockState blockstate : blocks) {
					int x = blockstate.getX();
					int y = blockstate.getY();
					int z = blockstate.getZ();
					int updateFlag = ((CraftBlockState) blockstate).getFlag();
					org.bukkit.Material mat = blockstate.getType();
					Block oldBlock = CraftMagicNumbers.getBlock(mat);
					Block block = world.getType(x, y, z);

					if (block != null && !(block instanceof BlockContainer)) { // Containers get placed automatically
						block.onPlace(world, x, y, z);
					}

					world.notifyAndUpdatePhysics(x, y, z, null, oldBlock, block, updateFlag); // send null chunk as chunk.k() returns false by this point
				}
				entityhuman.a(StatisticList.USE_ITEM_COUNT[Item.getId(item)], 1);
			}
		}
		world.capturedBlockStates.clear();
		// CraftBukkit end

		return flag;
	}

	public float a(Block block) {
		return getItem().getDestroySpeed(this, block);
	}

	public ItemStack a(World world, EntityHuman entityhuman) {
		return getItem().a(this, world, entityhuman);
	}

	public ItemStack b(World world, EntityHuman entityhuman) {
		return getItem().b(this, world, entityhuman);
	}

	public NBTTagCompound save(NBTTagCompound nbttagcompound) {
		nbttagcompound.setShort("id", (short) Item.getId(item));
		nbttagcompound.setByte("Count", (byte) count);
		nbttagcompound.setShort("Damage", (short) damage);
		if (tag != null) {
			nbttagcompound.set("tag", tag.clone()); // CraftBukkit - make defensive copy, data is going to another thread
		}

		return nbttagcompound;
	}

	public void c(NBTTagCompound nbttagcompound) {
		item = Item.getById(nbttagcompound.getShort("id"));
		count = nbttagcompound.getByte("Count");
		/* CraftBukkit start - Route through setData for filtering
		this.damage = nbttagcompound.getShort("Damage");
		if (this.damage < 0) {
		    this.damage = 0;
		}
		*/
		setData(nbttagcompound.getShort("Damage"));
		// CraftBukkit end

		if (nbttagcompound.hasKeyOfType("tag", 10)) {
			// CraftBukkit - make defensive copy as this data may be coming from the save thread
			tag = (NBTTagCompound) nbttagcompound.getCompound("tag").clone();
			validateSkullSkin(); // Spigot
		}
	}

	// Spigot start - make sure the tag is given the full gameprofile if it's a skull (async lookup)
	public void validateSkullSkin() {
		if (item == Items.SKULL && getData() == 3) {
			String owner;
			if (tag.hasKeyOfType("SkullOwner", 8)) {
				owner = tag.getString("SkullOwner");
			} else if (tag.hasKeyOfType("SkullOwner", 10)) {
				net.minecraft.util.com.mojang.authlib.GameProfile profile = GameProfileSerializer.deserialize(tag.getCompound("SkullOwner"));
				if (profile == null || !profile.getProperties().isEmpty())
					return;
				else {
					owner = profile.getName();
				}
			} else
				return;

			final String finalOwner = owner;
			TileEntitySkull.executor.execute(new Runnable() {
				@Override
				public void run() {

					final net.minecraft.util.com.mojang.authlib.GameProfile profile = TileEntitySkull.skinCache.getUnchecked(finalOwner.toLowerCase());
					if (profile != null) {
						MinecraftServer.getServer().processQueue.add(new Runnable() {
							@Override
							public void run() {
								NBTTagCompound nbtProfile = new NBTTagCompound();
								GameProfileSerializer.serialize(nbtProfile, profile);
								tag.set("SkullOwner", nbtProfile);
							}
						});
					}
				}
			});
		}
	}

	// Spigot end

	public int getMaxStackSize() {
		return getItem().getMaxStackSize();
	}

	public boolean isStackable() {
		return getMaxStackSize() > 1 && (!g() || !i());
	}

	public boolean g() {
		// Spigot Start
		if (item.getMaxDurability() <= 0)
			return false;
		return !hasTag() || !getTag().getBoolean("Unbreakable");
		// Spigot End
	}

	public boolean usesData() {
		return item.n();
	}

	public boolean i() {
		return g() && damage > 0;
	}

	public int j() {
		return damage;
	}

	public int getData() {
		return damage;
	}

	public void setData(int i) {
		// CraftBukkit start - Filter out data for items that shouldn't have it
		// The crafting system uses this value for a special purpose so we have to allow it
		if (i == 32767) {
			damage = i;
			return;
		}

		// Is this a block?
		if (CraftMagicNumbers.getBlock(CraftMagicNumbers.getId(getItem())) != Blocks.AIR) {
			// If vanilla doesn't use data on it don't allow any
			if (!(usesData() || getItem().usesDurability())) {
				i = 0;
			}
		}

		// Filter invalid plant data
		if (CraftMagicNumbers.getBlock(CraftMagicNumbers.getId(getItem())) == Blocks.DOUBLE_PLANT && (i > 5 || i < 0)) {
			i = 0;
		}
		// CraftBukkit end

		damage = i;
		if (damage < -1) { // CraftBukkit - don't filter -1, we use it
			damage = 0;
		}
	}

	public int l() {
		return item.getMaxDurability();
	}

	// Spigot start
	public boolean isDamaged(int i, Random random) {
		return isDamaged(i, random, null);
	}

	public boolean isDamaged(int i, Random random, EntityLiving entityliving) {
		// Spigot end
		if (!g())
			return false;
		else {
			if (i > 0) {
				int j = EnchantmentManager.getEnchantmentLevel(Enchantment.DURABILITY.id, this);
				int k = 0;

				for (int l = 0; j > 0 && l < i; ++l) {
					if (EnchantmentDurability.a(this, j, random)) {
						++k;
					}
				}

				i -= k;
				// Spigot start
				if (entityliving instanceof EntityPlayer) {
					org.bukkit.craftbukkit.inventory.CraftItemStack item = org.bukkit.craftbukkit.inventory.CraftItemStack.asCraftMirror(this);
					org.bukkit.event.player.PlayerItemDamageEvent event = new org.bukkit.event.player.PlayerItemDamageEvent((org.bukkit.entity.Player) entityliving.getBukkitEntity(), item, i);
					org.bukkit.Bukkit.getServer().getPluginManager().callEvent(event);
					if (event.isCancelled())
						return false;
					i = event.getDamage();
				}
				// Spigot end
				if (i <= 0)
					return false;
			}

			damage += i;
			return damage > l();
		}
	}

	public void damage(int i, EntityLiving entityliving) {
		if (!(entityliving instanceof EntityHuman) || !((EntityHuman) entityliving).abilities.canInstantlyBuild) {
			if (g()) {
				if (this.isDamaged(i, entityliving.aI(), entityliving)) { // Spigot
					entityliving.a(this);
					--count;
					if (entityliving instanceof EntityHuman) {
						EntityHuman entityhuman = (EntityHuman) entityliving;

						entityhuman.a(StatisticList.BREAK_ITEM_COUNT[Item.getId(item)], 1);
						if (count == 0 && getItem() instanceof ItemBow) {
							entityhuman.bG();
						}
					}

					if (count < 0) {
						count = 0;
					}

					// CraftBukkit start - Check for item breaking
					if (count == 0 && entityliving instanceof EntityHuman) {
						org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerItemBreakEvent((EntityHuman) entityliving, this);
					}
					// CraftBukkit end

					damage = 0;
				}
			}
		}
	}

	public void a(EntityLiving entityliving, EntityHuman entityhuman) {
		boolean flag = item.a(this, entityliving, entityhuman);

		if (flag) {
			entityhuman.a(StatisticList.USE_ITEM_COUNT[Item.getId(item)], 1);
		}
	}

	public void a(World world, Block block, int i, int j, int k, EntityHuman entityhuman) {
		boolean flag = item.a(this, world, block, i, j, k, entityhuman);

		if (flag) {
			entityhuman.a(StatisticList.USE_ITEM_COUNT[Item.getId(item)], 1);
		}
	}

	public boolean b(Block block) {
		return item.canDestroySpecialBlock(block);
	}

	public boolean a(EntityHuman entityhuman, EntityLiving entityliving) {
		return item.a(this, entityhuman, entityliving);
	}

	public ItemStack cloneItemStack() {
		ItemStack itemstack = new ItemStack(item, count, damage);

		if (tag != null) {
			itemstack.tag = (NBTTagCompound) tag.clone();
		}

		return itemstack;
	}

	public static boolean equals(ItemStack itemstack, ItemStack itemstack1) {
		return itemstack == null && itemstack1 == null ? true : itemstack != null && itemstack1 != null ? itemstack.tag == null && itemstack1.tag != null ? false : itemstack.tag == null || itemstack.tag.equals(itemstack1.tag) : false;
	}

	public static boolean matches(ItemStack itemstack, ItemStack itemstack1) {
		return itemstack == null && itemstack1 == null ? true : itemstack != null && itemstack1 != null ? itemstack.d(itemstack1) : false;
	}

	private boolean d(ItemStack itemstack) {
		return count != itemstack.count ? false : item != itemstack.item ? false : damage != itemstack.damage ? false : tag == null && itemstack.tag != null ? false : tag == null || tag.equals(itemstack.tag);
	}

	public boolean doMaterialsMatch(ItemStack itemstack) {
		return item == itemstack.item && damage == itemstack.damage;
	}

	public String a() {
		return item.a(this);
	}

	public static ItemStack b(ItemStack itemstack) {
		return itemstack == null ? null : itemstack.cloneItemStack();
	}

	@Override
	public String toString() {
		return count + "x" + item.getName() + "@" + damage;
	}

	public void a(World world, Entity entity, int i, boolean flag) {
		if (c > 0) {
			--c;
		}

		item.a(this, world, entity, i, flag);
	}

	public void a(World world, EntityHuman entityhuman, int i) {
		entityhuman.a(StatisticList.CRAFT_BLOCK_COUNT[Item.getId(item)], i);
		item.d(this, world, entityhuman);
	}

	public int n() {
		return getItem().d_(this);
	}

	public EnumAnimation o() {
		return getItem().d(this);
	}

	public void b(World world, EntityHuman entityhuman, int i) {
		getItem().a(this, world, entityhuman, i);
	}

	public boolean hasTag() {
		return tag != null;
	}

	public NBTTagCompound getTag() {
		return tag;
	}

	public NBTTagList getEnchantments() {
		return tag == null ? null : tag.getList("ench", 10);
	}

	public void setTag(NBTTagCompound nbttagcompound) {
		tag = nbttagcompound;
		validateSkullSkin(); // Spigot
	}

	public String getName() {
		String s = getItem().n(this);

		if (tag != null && tag.hasKeyOfType("display", 10)) {
			NBTTagCompound nbttagcompound = tag.getCompound("display");

			if (nbttagcompound.hasKeyOfType("Name", 8)) {
				s = nbttagcompound.getString("Name");
			}
		}

		return s;
	}

	public ItemStack c(String s) {
		if (tag == null) {
			tag = new NBTTagCompound();
		}

		if (!tag.hasKeyOfType("display", 10)) {
			tag.set("display", new NBTTagCompound());
		}

		tag.getCompound("display").setString("Name", s);
		return this;
	}

	public void t() {
		if (tag != null) {
			if (tag.hasKeyOfType("display", 10)) {
				NBTTagCompound nbttagcompound = tag.getCompound("display");

				nbttagcompound.remove("Name");
				if (nbttagcompound.isEmpty()) {
					tag.remove("display");
					if (tag.isEmpty()) {
						setTag((NBTTagCompound) null);
					}
				}
			}
		}
	}

	public boolean hasName() {
		return tag == null ? false : !tag.hasKeyOfType("display", 10) ? false : tag.getCompound("display").hasKeyOfType("Name", 8);
	}

	public EnumItemRarity w() {
		return getItem().f(this);
	}

	public boolean x() {
		return !getItem().e_(this) ? false : !hasEnchantments();
	}

	public void addEnchantment(Enchantment enchantment, int i) {
		if (tag == null) {
			setTag(new NBTTagCompound());
		}

		if (!tag.hasKeyOfType("ench", 9)) {
			tag.set("ench", new NBTTagList());
		}

		NBTTagList nbttaglist = tag.getList("ench", 10);
		NBTTagCompound nbttagcompound = new NBTTagCompound();

		nbttagcompound.setShort("id", (short) enchantment.id);
		nbttagcompound.setShort("lvl", (byte) i);
		nbttaglist.add(nbttagcompound);
	}

	public boolean hasEnchantments() {
		return tag != null && tag.hasKeyOfType("ench", 9);
	}

	public void a(String s, NBTBase nbtbase) {
		if (tag == null) {
			setTag(new NBTTagCompound());
		}

		tag.set(s, nbtbase);
	}

	public boolean z() {
		return getItem().v();
	}

	public boolean A() {
		return g != null;
	}

	public void a(EntityItemFrame entityitemframe) {
		g = entityitemframe;
	}

	public EntityItemFrame B() {
		return g;
	}

	public int getRepairCost() {
		return hasTag() && tag.hasKeyOfType("RepairCost", 3) ? tag.getInt("RepairCost") : 0;
	}

	public void setRepairCost(int i) {
		if (!hasTag()) {
			tag = new NBTTagCompound();
		}

		tag.setInt("RepairCost", i);
	}

	public Multimap D() {
		Object object;

		if (hasTag() && tag.hasKeyOfType("AttributeModifiers", 9)) {
			object = HashMultimap.create();
			NBTTagList nbttaglist = tag.getList("AttributeModifiers", 10);

			for (int i = 0; i < nbttaglist.size(); ++i) {
				NBTTagCompound nbttagcompound = nbttaglist.get(i);
				AttributeModifier attributemodifier = GenericAttributes.a(nbttagcompound);

				if (attributemodifier.a().getLeastSignificantBits() != 0L && attributemodifier.a().getMostSignificantBits() != 0L) {
					((Multimap) object).put(nbttagcompound.getString("AttributeName"), attributemodifier);
				}
			}
		} else {
			object = getItem().k();
		}

		return (Multimap) object;
	}

	public void setItem(Item item) {
		this.item = item;
		setData(getData()); // CraftBukkit - Set data again to ensure it is filtered properly
	}

	public IChatBaseComponent E() {
		IChatBaseComponent ichatbasecomponent = new ChatComponentText("[").a(getName()).a("]");

		if (item != null) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();

			save(nbttagcompound);
			ichatbasecomponent.getChatModifier().a(new ChatHoverable(EnumHoverAction.SHOW_ITEM, new ChatComponentText(nbttagcompound.toString())));
			ichatbasecomponent.getChatModifier().setColor(w().e);
		}

		return ichatbasecomponent;
	}
}
