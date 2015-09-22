package net.minecraft.server;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class EntityFallingBlock extends Entity {

	public Block id; // CraftBukkit - private -> public
	public int data;
	public int ticksLived;
	public boolean dropItem;
	private boolean f;
	private boolean hurtEntities;
	private int fallHurtMax;
	private float fallHurtAmount;
	public NBTTagCompound tileEntityData;

	public EntityFallingBlock(World world) {
		super(world);
		dropItem = true;
		fallHurtMax = 40;
		fallHurtAmount = 2.0F;
	}

	public EntityFallingBlock(World world, double d0, double d1, double d2, Block block) {
		this(world, d0, d1, d2, block, 0);
	}

	public EntityFallingBlock(World world, double d0, double d1, double d2, Block block, int i) {
		super(world);
		dropItem = true;
		fallHurtMax = 40;
		fallHurtAmount = 2.0F;
		id = block;
		data = i;
		k = true;
		this.a(0.98F, 0.98F);
		height = length / 2.0F;
		setPosition(d0, d1, d2);
		motX = 0.0D;
		motY = 0.0D;
		motZ = 0.0D;
		lastX = d0;
		lastY = d1;
		lastZ = d2;
	}

	@Override
	protected boolean g_() {
		return false;
	}

	@Override
	protected void c() {
	}

	@Override
	public boolean R() {
		return !dead;
	}

	@Override
	public void h() {
		if (id.getMaterial() == Material.AIR) {
			die();
		} else {
			lastX = locX;
			lastY = locY;
			lastZ = locZ;
			++ticksLived;
			motY -= 0.03999999910593033D;
			move(motX, motY, motZ);
			// PaperSpigot start - Remove entities in unloaded chunks
			if (inUnloadedChunk && world.paperSpigotConfig.removeUnloadedFallingBlocks) {
				die();
			}
			// PaperSpigot end

			// PaperSpigot start - Drop falling blocks above the specified height
			if (world.paperSpigotConfig.fallingBlockHeightNerf != 0 && locY > world.paperSpigotConfig.fallingBlockHeightNerf) {
				if (dropItem) {
					this.a(new ItemStack(id, 1, id.getDropData(data)), 0.0F);
				}

				die();
			}
			// PaperSpigot end

			motX *= 0.9800000190734863D;
			motY *= 0.9800000190734863D;
			motZ *= 0.9800000190734863D;
			if (!world.isStatic) {
				int i = MathHelper.floor(locX);
				int j = MathHelper.floor(locY);
				int k = MathHelper.floor(locZ);

				if (ticksLived == 1) {
					// CraftBukkit - compare data and call event
					if (ticksLived != 1 || world.getType(i, j, k) != id || world.getData(i, j, k) != data || CraftEventFactory.callEntityChangeBlockEvent(this, i, j, k, Blocks.AIR, 0).isCancelled()) {
						die();
						return;
					}

					world.setAir(i, j, k);
					world.spigotConfig.antiXrayInstance.updateNearbyBlocks(world, i, j, k); // Spigot
				}

				if (onGround) {
					motX *= 0.699999988079071D;
					motZ *= 0.699999988079071D;
					motY *= -0.5D;
					if (world.getType(i, j, k) != Blocks.PISTON_MOVING) {
						die();
						// CraftBukkit start - fire EntityChangeBlockEvent
						if (!f && world.mayPlace(id, i, j, k, true, 1, (Entity) null, (ItemStack) null) && !BlockFalling.canFall(world, i, j - 1, k) /* mimic the false conditions of setTypeIdAndData */&& i >= -30000000 && k >= -30000000 && i < 30000000 && k < 30000000 && j > 0 && j < 256 && !(world.getType(i, j, k) == id && world.getData(i, j, k) == data)) {
							if (CraftEventFactory.callEntityChangeBlockEvent(this, i, j, k, id, data).isCancelled())
								return;
							world.setTypeAndData(i, j, k, id, data, 3);
							// CraftBukkit end
							world.spigotConfig.antiXrayInstance.updateNearbyBlocks(world, i, j, k); // Spigot

							if (id instanceof BlockFalling) {
								((BlockFalling) id).a(world, i, j, k, data);
							}

							if (tileEntityData != null && id instanceof IContainer) {
								TileEntity tileentity = world.getTileEntity(i, j, k);

								if (tileentity != null) {
									NBTTagCompound nbttagcompound = new NBTTagCompound();

									tileentity.b(nbttagcompound);
									Iterator iterator = tileEntityData.c().iterator();

									while (iterator.hasNext()) {
										String s = (String) iterator.next();
										NBTBase nbtbase = tileEntityData.get(s);

										if (!s.equals("x") && !s.equals("y") && !s.equals("z")) {
											nbttagcompound.set(s, nbtbase.clone());
										}
									}

									tileentity.a(nbttagcompound);
									tileentity.update();
								}
							}
						} else if (dropItem && !f) {
							this.a(new ItemStack(id, 1, id.getDropData(data)), 0.0F);
						}
					}
				} else if (ticksLived > 100 && !world.isStatic && (j < 1 || j > 256) || ticksLived > 600) {
					if (dropItem) {
						this.a(new ItemStack(id, 1, id.getDropData(data)), 0.0F);
					}

					die();
				}
			}
		}
	}

	@Override
	protected void b(float f) {
		if (hurtEntities) {
			int i = MathHelper.f(f - 1.0F);

			if (i > 0) {
				ArrayList arraylist = new ArrayList(world.getEntities(this, boundingBox));
				boolean flag = id == Blocks.ANVIL;
				DamageSource damagesource = flag ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;
				Iterator iterator = arraylist.iterator();

				while (iterator.hasNext()) {
					Entity entity = (Entity) iterator.next();

					CraftEventFactory.entityDamage = this; // CraftBukkit
					entity.damageEntity(damagesource, Math.min(MathHelper.d(i * fallHurtAmount), fallHurtMax));
					CraftEventFactory.entityDamage = null; // CraftBukkit
				}

				if (flag && random.nextFloat() < 0.05000000074505806D + i * 0.05D) {
					int j = data >> 2;
					int k = data & 3;

					++j;
					if (j > 2) {
						this.f = true;
					} else {
						data = k | j << 2;
					}
				}
			}
		}
	}

	@Override
	protected void b(NBTTagCompound nbttagcompound) {
		nbttagcompound.setByte("Tile", (byte) Block.getId(id));
		nbttagcompound.setInt("TileID", Block.getId(id));
		nbttagcompound.setByte("Data", (byte) data);
		nbttagcompound.setByte("Time", (byte) ticksLived);
		nbttagcompound.setBoolean("DropItem", dropItem);
		nbttagcompound.setBoolean("HurtEntities", hurtEntities);
		nbttagcompound.setFloat("FallHurtAmount", fallHurtAmount);
		nbttagcompound.setInt("FallHurtMax", fallHurtMax);
		if (tileEntityData != null) {
			nbttagcompound.set("TileEntityData", tileEntityData);
		}
	}

	@Override
	protected void a(NBTTagCompound nbttagcompound) {
		if (nbttagcompound.hasKeyOfType("TileID", 99)) {
			id = Block.getById(nbttagcompound.getInt("TileID"));
		} else {
			id = Block.getById(nbttagcompound.getByte("Tile") & 255);
		}

		data = nbttagcompound.getByte("Data") & 255;
		ticksLived = nbttagcompound.getByte("Time") & 255;
		if (nbttagcompound.hasKeyOfType("HurtEntities", 99)) {
			hurtEntities = nbttagcompound.getBoolean("HurtEntities");
			fallHurtAmount = nbttagcompound.getFloat("FallHurtAmount");
			fallHurtMax = nbttagcompound.getInt("FallHurtMax");
		} else if (id == Blocks.ANVIL) {
			hurtEntities = true;
		}

		if (nbttagcompound.hasKeyOfType("DropItem", 99)) {
			dropItem = nbttagcompound.getBoolean("DropItem");
		}

		if (nbttagcompound.hasKeyOfType("TileEntityData", 10)) {
			tileEntityData = nbttagcompound.getCompound("TileEntityData");
		}

		if (id.getMaterial() == Material.AIR) {
			id = Blocks.SAND;
		}
	}

	public void a(boolean flag) {
		hurtEntities = flag;
	}

	@Override
	public void a(CrashReportSystemDetails crashreportsystemdetails) {
		super.a(crashreportsystemdetails);
		crashreportsystemdetails.a("Immitating block ID", Integer.valueOf(Block.getId(id)));
		crashreportsystemdetails.a("Immitating block data", Integer.valueOf(data));
	}

	public Block f() {
		return id;
	}
}
