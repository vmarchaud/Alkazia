package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

// CraftBukkit end

public class PlayerInteractManager {

	public World world;
	public EntityPlayer player;
	private EnumGamemode gamemode;
	private boolean d;
	private int lastDigTick;
	private int f;
	private int g;
	private int h;
	private int currentTick;
	private boolean j;
	private int k;
	private int l;
	private int m;
	private int n;
	private int o;

	public PlayerInteractManager(World world) {
		gamemode = EnumGamemode.NONE;
		o = -1;
		this.world = world;
	}

	public void setGameMode(EnumGamemode enumgamemode) {
		gamemode = enumgamemode;
		enumgamemode.a(player.abilities);
		player.updateAbilities();
	}

	public EnumGamemode getGameMode() {
		return gamemode;
	}

	public boolean isCreative() {
		return gamemode.d();
	}

	public void b(EnumGamemode enumgamemode) {
		if (gamemode == EnumGamemode.NONE) {
			gamemode = enumgamemode;
		}

		setGameMode(gamemode);
	}

	public void a() {
		currentTick = MinecraftServer.currentTick; // CraftBukkit
		float f;
		int i;

		if (j) {
			int j = currentTick - n;
			Block block = world.getType(k, l, m);

			if (block.getMaterial() == Material.AIR) {
				this.j = false;
			} else {
				f = block.getDamage(player, player.world, k, l, m) * (j + 1);
				i = (int) (f * 10.0F);
				if (i != o) {
					world.d(player.getId(), k, l, m, i);
					o = i;
				}

				if (f >= 1.0F) {
					this.j = false;
					breakBlock(k, l, m);
				}
			}
		} else if (d) {
			Block block1 = world.getType(this.f, g, h);

			if (block1.getMaterial() == Material.AIR) {
				world.d(player.getId(), this.f, g, h, -1);
				o = -1;
				d = false;
			} else {
				int k = currentTick - lastDigTick;

				f = block1.getDamage(player, player.world, this.f, g, h) * (k + 1);
				i = (int) (f * 10.0F);
				if (i != o) {
					world.d(player.getId(), this.f, g, h, i);
					o = i;
				}
			}
		}
	}

	public void dig(int i, int j, int k, int l) {
		// CraftBukkit start
		PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, i, j, k, l, player.inventory.getItemInHand());
		if (!gamemode.isAdventure() || player.d(i, j, k)) {
			if (event.isCancelled()) {
				// Let the client know the block still exists
				player.playerConnection.sendPacket(new PacketPlayOutBlockChange(i, j, k, world));
				// Update any tile entity data for this block
				TileEntity tileentity = world.getTileEntity(i, j, k);
				if (tileentity != null) {
					player.playerConnection.sendPacket(tileentity.getUpdatePacket());
				}
				return;
			}
			// CraftBukkit end
			if (isCreative()) {
				if (!world.douseFire((EntityHuman) null, i, j, k, l)) {
					breakBlock(i, j, k);
				}
			} else {
				// this.world.douseFire((EntityHuman) null, i, j, k, l); // CraftBukkit - Moved down
				lastDigTick = currentTick;
				float f = 1.0F;
				Block block = world.getType(i, j, k);
				// CraftBukkit start - Swings at air do *NOT* exist.
				if (event.useInteractedBlock() == Event.Result.DENY) {
					// If we denied a door from opening, we need to send a correcting update to the client, as it already opened the door.
					if (block == Blocks.WOODEN_DOOR) {
						// For some reason *BOTH* the bottom/top part have to be marked updated.
						boolean bottom = (world.getData(i, j, k) & 8) == 0;
						player.playerConnection.sendPacket(new PacketPlayOutBlockChange(i, j, k, world));
						player.playerConnection.sendPacket(new PacketPlayOutBlockChange(i, j + (bottom ? 1 : -1), k, world));
					} else if (block == Blocks.TRAP_DOOR) {
						player.playerConnection.sendPacket(new PacketPlayOutBlockChange(i, j, k, world));
					}
				} else if (block.getMaterial() != Material.AIR) {
					block.attack(world, i, j, k, player);
					f = block.getDamage(player, player.world, i, j, k);
					// Allow fire punching to be blocked
					world.douseFire((EntityHuman) null, i, j, k, l);
				}

				if (event.useItemInHand() == Event.Result.DENY) {
					// If we 'insta destroyed' then the client needs to be informed.
					if (f > 1.0f) {
						player.playerConnection.sendPacket(new PacketPlayOutBlockChange(i, j, k, world));
					}
					return;
				}
				org.bukkit.event.block.BlockDamageEvent blockEvent = CraftEventFactory.callBlockDamageEvent(player, i, j, k, player.inventory.getItemInHand(), f >= 1.0f);

				if (blockEvent.isCancelled()) {
					// Let the client know the block still exists
					player.playerConnection.sendPacket(new PacketPlayOutBlockChange(i, j, k, world));
					return;
				}

				if (blockEvent.getInstaBreak()) {
					f = 2.0f;
				}
				// CraftBukkit end

				if (block.getMaterial() != Material.AIR && f >= 1.0F) {
					breakBlock(i, j, k);
				} else {
					d = true;
					this.f = i;
					g = j;
					h = k;
					int i1 = (int) (f * 10.0F);

					world.d(player.getId(), i, j, k, i1);
					o = i1;
				}
			}
			world.spigotConfig.antiXrayInstance.updateNearbyBlocks(world, i, j, k); // Spigot
		}
	}

	public void a(int i, int j, int k) {
		if (i == f && j == g && k == h) {
			currentTick = MinecraftServer.currentTick; // CraftBukkit
			int l = currentTick - lastDigTick;
			Block block = world.getType(i, j, k);

			if (block.getMaterial() != Material.AIR) {
				float f = block.getDamage(player, player.world, i, j, k) * (l + 1);

				if (f >= 0.7F) {
					d = false;
					world.d(player.getId(), i, j, k, -1);
					breakBlock(i, j, k);
				} else if (!this.j) {
					d = false;
					this.j = true;
					this.k = i;
					this.l = j;
					m = k;
					n = lastDigTick;
				}
			}
			// CraftBukkit start - Force block reset to client
		} else {
			player.playerConnection.sendPacket(new PacketPlayOutBlockChange(i, j, k, world));
			// CraftBukkit end
		}
	}

	public void c(int i, int j, int k) {
		d = false;
		world.d(player.getId(), f, g, h, -1);
	}

	private boolean d(int i, int j, int k) {
		Block block = world.getType(i, j, k);
		int l = world.getData(i, j, k);

		block.a(world, i, j, k, l, player);
		boolean flag = world.setAir(i, j, k);

		if (flag) {
			block.postBreak(world, i, j, k, l);
		}

		return flag;
	}

	public boolean breakBlock(int i, int j, int k) {
		// CraftBukkit start - fire BlockBreakEvent
		BlockBreakEvent event = null;

		if (player instanceof EntityPlayer) {
			org.bukkit.block.Block block = world.getWorld().getBlockAt(i, j, k);

			// Tell client the block is gone immediately then process events
			if (world.getTileEntity(i, j, k) == null) {
				PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(i, j, k, world);
				packet.block = Blocks.AIR;
				packet.data = 0;
				player.playerConnection.sendPacket(packet);
			}

			event = new BlockBreakEvent(block, player.getBukkitEntity());

			// Adventure mode pre-cancel
			event.setCancelled(gamemode.isAdventure() && !player.d(i, j, k));

			// Sword + Creative mode pre-cancel
			event.setCancelled(event.isCancelled() || gamemode.d() && player.be() != null && player.be().getItem() instanceof ItemSword);

			// Calculate default block experience
			Block nmsBlock = world.getType(i, j, k);

			if (nmsBlock != null && !event.isCancelled() && !isCreative() && player.a(nmsBlock)) {
				// Copied from block.a(world, entityhuman, int, int, int, int)
				if (!(nmsBlock.E() && EnchantmentManager.hasSilkTouchEnchantment(player))) {
					int data = block.getData();
					int bonusLevel = EnchantmentManager.getBonusBlockLootEnchantmentLevel(player);

					event.setExpToDrop(nmsBlock.getExpDrop(world, data, bonusLevel));
				}
			}

			world.getServer().getPluginManager().callEvent(event);

			if (event.isCancelled()) {
				// Let the client know the block still exists
				player.playerConnection.sendPacket(new PacketPlayOutBlockChange(i, j, k, world));
				// Update any tile entity data for this block
				TileEntity tileentity = world.getTileEntity(i, j, k);
				if (tileentity != null) {
					player.playerConnection.sendPacket(tileentity.getUpdatePacket());
				}
				return false;
			}
		}

		if (false && gamemode.isAdventure() && !player.d(i, j, k))
			// CraftBukkit end
			return false;
		else if (false && gamemode.d() && player.be() != null && player.be().getItem() instanceof ItemSword)
			return false;
		else {
			Block block = world.getType(i, j, k);
			if (block == Blocks.AIR)
				return false; // CraftBukkit - A plugin set block to air without cancelling
			int l = world.getData(i, j, k);

			// CraftBukkit start - Special case skulls, their item data comes from a tile entity
			if (block == Blocks.SKULL && !isCreative()) {
				block.dropNaturally(world, i, j, k, l, 1.0F, 0);
				return d(i, j, k);
			}
			// CraftBukkit end

			world.a(player, 2001, i, j, k, Block.getId(block) + (world.getData(i, j, k) << 12));
			boolean flag = d(i, j, k);

			if (isCreative()) {
				player.playerConnection.sendPacket(new PacketPlayOutBlockChange(i, j, k, world));
			} else {
				ItemStack itemstack = player.bF();
				boolean flag1 = player.a(block);

				if (itemstack != null) {
					itemstack.a(world, block, i, j, k, player);
					if (itemstack.count == 0) {
						player.bG();
					}
				}

				if (flag && flag1) {
					block.a(world, player, i, j, k, l);
				}
			}

			// CraftBukkit start - Drop event experience
			if (flag && event != null) {
				block.dropExperience(world, i, j, k, event.getExpToDrop());
			}
			// CraftBukkit end

			return flag;
		}
	}

	public boolean useItem(EntityHuman entityhuman, World world, ItemStack itemstack) {
		int i = itemstack.count;
		int j = itemstack.getData();
		ItemStack itemstack1 = itemstack.a(world, entityhuman);

		// Spigot start - protocol patch
		if (itemstack1 != null && itemstack1.getItem() == Items.WRITTEN_BOOK) {
			player.playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|BOpen", new byte[0]));
		}
		// Spigot end

		if (itemstack1 == itemstack && (itemstack1 == null || itemstack1.count == i && itemstack1.n() <= 0 && itemstack1.getData() == j))
			return false;
		else {
			entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = itemstack1;
			if (isCreative()) {
				itemstack1.count = i;
				if (itemstack1.g()) {
					itemstack1.setData(j);
				}
			}

			if (itemstack1.count == 0) {
				entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
			}

			if (!entityhuman.by()) {
				((EntityPlayer) entityhuman).updateInventory(entityhuman.defaultContainer);
			}

			return true;
		}
	}

	public boolean interact(EntityHuman entityhuman, World world, ItemStack itemstack, int i, int j, int k, int l, float f, float f1, float f2) {
		/* CraftBukkit start - whole method
		if ((!entityhuman.isSneaking() || entityhuman.be() == null) && world.getType(i, j, k).interact(world, i, j, k, entityhuman, l, f, f1, f2)) {
		    return true;
		} else if (itemstack == null) {
		    return false;
		} else if (this.isCreative()) {
		    int i1 = itemstack.getData();
		    int j1 = itemstack.count;
		    boolean flag = itemstack.placeItem(entityhuman, world, i, j, k, l, f, f1, f2);

		    itemstack.setData(i1);
		    itemstack.count = j1;
		    return flag;
		} else {
		    return itemstack.placeItem(entityhuman, world, i, j, k, l, f, f1, f2);
		}
		// Interract event */
		Block block = world.getType(i, j, k);
		boolean result = false;
		if (block != Blocks.AIR) {
			PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(entityhuman, Action.RIGHT_CLICK_BLOCK, i, j, k, l, itemstack);
			if (event.useInteractedBlock() == Event.Result.DENY) {
				// If we denied a door from opening, we need to send a correcting update to the client, as it already opened the door.
				if (block == Blocks.WOODEN_DOOR) {
					boolean bottom = (world.getData(i, j, k) & 8) == 0;
					((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutBlockChange(i, j + (bottom ? 1 : -1), k, world));
				}
				result = event.useItemInHand() != Event.Result.ALLOW;
			} else if (!entityhuman.isSneaking() || itemstack == null) {
				result = block.interact(world, i, j, k, entityhuman, l, f, f1, f2);
			}

			if (itemstack != null && !result) {
				int j1 = itemstack.getData();
				int k1 = itemstack.count;

				result = itemstack.placeItem(entityhuman, world, i, j, k, l, f, f1, f2);

				// The item count should not decrement in Creative mode.
				if (isCreative()) {
					itemstack.setData(j1);
					itemstack.count = k1;
				}
			}

			// If we have 'true' and no explicit deny *or* an explicit allow -- run the item part of the hook
			if (itemstack != null && (!result && event.useItemInHand() != Event.Result.DENY || event.useItemInHand() == Event.Result.ALLOW)) {
				useItem(entityhuman, world, itemstack);
			}
		}
		return result;
		// CraftBukkit end
	}

	public void a(WorldServer worldserver) {
		world = worldserver;
	}
}
