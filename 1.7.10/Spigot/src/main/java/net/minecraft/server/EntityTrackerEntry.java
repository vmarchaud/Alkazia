package net.minecraft.server;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// CraftBukkit start
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;

// CraftBukkit end

public class EntityTrackerEntry {

	private static final Logger p = LogManager.getLogger();
	public Entity tracker;
	public int b;
	public int c;
	public int xLoc;
	public int yLoc;
	public int zLoc;
	public int yRot;
	public int xRot;
	public int i;
	public double j;
	public double k;
	public double l;
	public int m;
	private double q;
	private double r;
	private double s;
	private boolean isMoving;
	private boolean u;
	private int v;
	private Entity w;
	private boolean x;
	public boolean n;
	public Set trackedPlayers = new HashSet();

	public EntityTrackerEntry(Entity entity, int i, int j, boolean flag) {
		tracker = entity;
		b = i;
		c = j;
		u = flag;
		xLoc = MathHelper.floor(entity.locX * 32.0D);
		yLoc = MathHelper.floor(entity.locY * 32.0D);
		zLoc = MathHelper.floor(entity.locZ * 32.0D);
		yRot = MathHelper.d(entity.yaw * 256.0F / 360.0F);
		xRot = MathHelper.d(entity.pitch * 256.0F / 360.0F);
		this.i = MathHelper.d(entity.getHeadRotation() * 256.0F / 360.0F);
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof EntityTrackerEntry ? ((EntityTrackerEntry) object).tracker.getId() == tracker.getId() : false;
	}

	@Override
	public int hashCode() {
		return tracker.getId();
	}

	public void track(List list) {
		n = false;
		if (!isMoving || tracker.e(q, r, s) > 16.0D) {
			q = tracker.locX;
			r = tracker.locY;
			s = tracker.locZ;
			isMoving = true;
			n = true;
			scanPlayers(list);
		}

		if (w != tracker.vehicle || tracker.vehicle != null && m % 60 == 0) {
			w = tracker.vehicle;
			broadcast(new PacketPlayOutAttachEntity(0, tracker, tracker.vehicle));
		}

		if (tracker instanceof EntityItemFrame /*&& this.m % 10 == 0*/) { // CraftBukkit - Moved below, should always enter this block
			EntityItemFrame i3 = (EntityItemFrame) tracker;
			ItemStack i4 = i3.getItem();

			if (m % 10 == 0 && i4 != null && i4.getItem() instanceof ItemWorldMap) { // CraftBukkit - Moved this.m % 10 logic here so item frames do not enter the other blocks
				WorldMap i6 = Items.MAP.getSavedMap(i4, tracker.world);
				Iterator i7 = trackedPlayers.iterator(); // CraftBukkit

				while (i7.hasNext()) {
					EntityHuman i8 = (EntityHuman) i7.next();
					EntityPlayer i9 = (EntityPlayer) i8;

					i6.a(i9, i4);
					Packet j0 = Items.MAP.c(i4, tracker.world, i9);

					if (j0 != null) {
						i9.playerConnection.sendPacket(j0);
					}
				}
			}

			b();
		} else if (m % c == 0 || tracker.al || tracker.getDataWatcher().a()) {
			int i;
			int j;

			if (tracker.vehicle == null) {
				++v;
				i = tracker.as.a(tracker.locX);
				j = MathHelper.floor(tracker.locY * 32.0D);
				int k = tracker.as.a(tracker.locZ);
				int l = MathHelper.d(tracker.yaw * 256.0F / 360.0F);
				int i1 = MathHelper.d(tracker.pitch * 256.0F / 360.0F);
				int j1 = i - xLoc;
				int k1 = j - yLoc;
				int l1 = k - zLoc;
				Object object = null;
				boolean flag = Math.abs(j1) >= 4 || Math.abs(k1) >= 4 || Math.abs(l1) >= 4 || m % 60 == 0;
				boolean flag1 = Math.abs(l - yRot) >= 4 || Math.abs(i1 - xRot) >= 4;

				// CraftBukkit start - Code moved from below
				if (flag) {
					xLoc = i;
					yLoc = j;
					zLoc = k;
				}

				if (flag1) {
					yRot = l;
					xRot = i1;
				}
				// CraftBukkit end

				if (m > 0 || tracker instanceof EntityArrow) {
					if (j1 >= -128 && j1 < 128 && k1 >= -128 && k1 < 128 && l1 >= -128 && l1 < 128 && v <= 400 && !x) {
						if (flag && flag1) {
							object = new PacketPlayOutRelEntityMoveLook(tracker.getId(), (byte) j1, (byte) k1, (byte) l1, (byte) l, (byte) i1, tracker.onGround); // Spigot - protocol patch
						} else if (flag) {
							object = new PacketPlayOutRelEntityMove(tracker.getId(), (byte) j1, (byte) k1, (byte) l1, tracker.onGround); // Spigot - protocol patch
						} else if (flag1) {
							object = new PacketPlayOutEntityLook(tracker.getId(), (byte) l, (byte) i1, tracker.onGround); // Spigot - protocol patch
						}
					} else {
						v = 0;
						// CraftBukkit start - Refresh list of who can see a player before sending teleport packet
						if (tracker instanceof EntityPlayer) {
							scanPlayers(new java.util.ArrayList(trackedPlayers));
						}
						// CraftBukkit end
						object = new PacketPlayOutEntityTeleport(this.tracker.getId(), i, j, k, (byte) l, (byte) i1, tracker.onGround, tracker instanceof EntityFallingBlock || tracker instanceof EntityTNTPrimed); // Spigot - protocol patch // Spigot Update - 20140916a
					}
				}

				if (u) {
					double d0 = tracker.motX - this.j;
					double d1 = tracker.motY - this.k;
					double d2 = tracker.motZ - this.l;
					double d3 = 0.02D;
					double d4 = d0 * d0 + d1 * d1 + d2 * d2;

					if (d4 > d3 * d3 || d4 > 0.0D && tracker.motX == 0.0D && tracker.motY == 0.0D && tracker.motZ == 0.0D) {
						this.j = tracker.motX;
						this.k = tracker.motY;
						this.l = tracker.motZ;
						broadcast(new PacketPlayOutEntityVelocity(tracker.getId(), this.j, this.k, this.l));
					}
				}

				if (object != null) {
					broadcast((Packet) object);
				}

				b();
				/* CraftBukkit start - Code moved up
				if (flag) {
				    this.xLoc = i;
				    this.yLoc = j;
				    this.zLoc = k;
				}

				if (flag1) {
				    this.yRot = l;
				    this.xRot = i1;
				}
				// CraftBukkit end */

				x = false;
			} else {
				i = MathHelper.d(tracker.yaw * 256.0F / 360.0F);
				j = MathHelper.d(tracker.pitch * 256.0F / 360.0F);
				boolean flag2 = Math.abs(i - yRot) >= 4 || Math.abs(j - xRot) >= 4;

				if (flag2) {
					broadcast(new PacketPlayOutEntityLook(tracker.getId(), (byte) i, (byte) j, tracker.onGround)); // Spigot - protocol patch
					yRot = i;
					xRot = j;
				}

				xLoc = tracker.as.a(tracker.locX);
				yLoc = MathHelper.floor(tracker.locY * 32.0D);
				zLoc = tracker.as.a(tracker.locZ);
				b();
				x = true;
			}

			i = MathHelper.d(tracker.getHeadRotation() * 256.0F / 360.0F);
			if (Math.abs(i - this.i) >= 4) {
				broadcast(new PacketPlayOutEntityHeadRotation(tracker, (byte) i));
				this.i = i;
			}

			tracker.al = false;
		}

		++m;
		if (tracker.velocityChanged) {
			// CraftBukkit start - Create PlayerVelocity event
			boolean cancelled = false;

			if (tracker instanceof EntityPlayer) {
				Player player = (Player) tracker.getBukkitEntity();
				org.bukkit.util.Vector velocity = player.getVelocity();

				PlayerVelocityEvent event = new PlayerVelocityEvent(player, velocity);
				tracker.world.getServer().getPluginManager().callEvent(event);

				if (event.isCancelled()) {
					cancelled = true;
				} else if (!velocity.equals(event.getVelocity())) {
					player.setVelocity(velocity);
				}
			}

			if (!cancelled) {
				broadcastIncludingSelf(new PacketPlayOutEntityVelocity(tracker));
			}
			// CraftBukkit end

			tracker.velocityChanged = false;
		}
	}

	private void b() {
		DataWatcher datawatcher = tracker.getDataWatcher();

		if (datawatcher.a()) {
			broadcastIncludingSelf(new PacketPlayOutEntityMetadata(tracker.getId(), datawatcher, false));
		}

		if (tracker instanceof EntityLiving) {
			AttributeMapServer attributemapserver = (AttributeMapServer) ((EntityLiving) tracker).getAttributeMap();
			Set set = attributemapserver.getAttributes();

			if (!set.isEmpty()) {
				// CraftBukkit start - Send scaled max health
				if (tracker instanceof EntityPlayer) {
					((EntityPlayer) tracker).getBukkitEntity().injectScaledMaxHealth(set, false);
				}
				// CraftBukkit end
				broadcastIncludingSelf(new PacketPlayOutUpdateAttributes(tracker.getId(), set));
			}

			set.clear();
		}
	}

	public void broadcast(Packet packet) {
		Iterator iterator = trackedPlayers.iterator();

		while (iterator.hasNext()) {
			EntityPlayer entityplayer = (EntityPlayer) iterator.next();

			entityplayer.playerConnection.sendPacket(packet);
		}
	}

	public void broadcastIncludingSelf(Packet packet) {
		broadcast(packet);
		if (tracker instanceof EntityPlayer) {
			((EntityPlayer) tracker).playerConnection.sendPacket(packet);
		}
	}

	public void a() {
		Iterator iterator = trackedPlayers.iterator();

		while (iterator.hasNext()) {
			EntityPlayer entityplayer = (EntityPlayer) iterator.next();

			entityplayer.d(tracker);
		}
	}

	public void a(EntityPlayer entityplayer) {
		if (trackedPlayers.contains(entityplayer)) {
			entityplayer.d(tracker);
			trackedPlayers.remove(entityplayer);
		}
	}

	public void updatePlayer(EntityPlayer entityplayer) {
		org.spigotmc.AsyncCatcher.catchOp("player tracker update"); // Spigot
		if (entityplayer != tracker) {
			double d0 = entityplayer.locX - xLoc / 32;
			double d1 = entityplayer.locZ - zLoc / 32;

			if (d0 >= -b && d0 <= b && d1 >= -b && d1 <= b) {
				if (!trackedPlayers.contains(entityplayer) && (d(entityplayer) || tracker.attachedToPlayer)) {
					// CraftBukkit start - respect vanish API
					if (tracker instanceof EntityPlayer) {
						Player player = ((EntityPlayer) tracker).getBukkitEntity();
						if (!entityplayer.getBukkitEntity().canSee(player))
							return;
					}

					entityplayer.removeQueue.remove(Integer.valueOf(tracker.getId()));
					// CraftBukkit end

					trackedPlayers.add(entityplayer);
					Packet packet = c();

					// Spigot start - protocol patch
					if (tracker instanceof EntityPlayer) {
						entityplayer.playerConnection.sendPacket(PacketPlayOutPlayerInfo.addPlayer((EntityPlayer) tracker));
					}
					// Spigot end

					entityplayer.playerConnection.sendPacket(packet);
					if (!tracker.getDataWatcher().d()) {
						entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityMetadata(tracker.getId(), tracker.getDataWatcher(), true));
					}

					if (tracker instanceof EntityLiving) {
						AttributeMapServer attributemapserver = (AttributeMapServer) ((EntityLiving) tracker).getAttributeMap();
						Collection collection = attributemapserver.c();

						// CraftBukkit start - If sending own attributes send scaled health instead of current maximum health
						if (tracker.getId() == entityplayer.getId()) {
							((EntityPlayer) tracker).getBukkitEntity().injectScaledMaxHealth(collection, false);
						}
						// CraftBukkit end
						if (!collection.isEmpty()) {
							entityplayer.playerConnection.sendPacket(new PacketPlayOutUpdateAttributes(tracker.getId(), collection));
						}
					}

					j = tracker.motX;
					k = tracker.motY;
					l = tracker.motZ;
					if (u && !(packet instanceof PacketPlayOutSpawnEntityLiving)) {
						entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityVelocity(tracker.getId(), tracker.motX, tracker.motY, tracker.motZ));
					}

					if (tracker.vehicle != null) {
						entityplayer.playerConnection.sendPacket(new PacketPlayOutAttachEntity(0, tracker, tracker.vehicle));
					}

					// CraftBukkit start
					if (tracker.passenger != null) {
						entityplayer.playerConnection.sendPacket(new PacketPlayOutAttachEntity(0, tracker.passenger, tracker));
					}
					// CraftBukkit end

					if (tracker instanceof EntityInsentient && ((EntityInsentient) tracker).getLeashHolder() != null) {
						entityplayer.playerConnection.sendPacket(new PacketPlayOutAttachEntity(1, tracker, ((EntityInsentient) tracker).getLeashHolder()));
					}

					if (tracker instanceof EntityLiving) {
						for (int i = 0; i < 5; ++i) {
							ItemStack itemstack = ((EntityLiving) tracker).getEquipment(i);

							if (itemstack != null) {
								entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityEquipment(tracker.getId(), i, itemstack));
							}
						}
					}

					if (tracker instanceof EntityHuman) {
						EntityHuman entityhuman = (EntityHuman) tracker;

						if (entityhuman.isSleeping()) {
							entityplayer.playerConnection.sendPacket(new PacketPlayOutBed(entityhuman, MathHelper.floor(tracker.locX), MathHelper.floor(tracker.locY), MathHelper.floor(tracker.locZ)));
						}
					}

					// CraftBukkit start - Fix for nonsensical head yaw
					i = MathHelper.d(tracker.getHeadRotation() * 256.0F / 360.0F);
					broadcast(new PacketPlayOutEntityHeadRotation(tracker, (byte) i));
					// CraftBukkit end

					if (tracker instanceof EntityLiving) {
						EntityLiving entityliving = (EntityLiving) tracker;
						Iterator iterator = entityliving.getEffects().iterator();

						while (iterator.hasNext()) {
							MobEffect mobeffect = (MobEffect) iterator.next();

							entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityEffect(tracker.getId(), mobeffect));
						}
					}
				}
			} else if (trackedPlayers.contains(entityplayer)) {
				trackedPlayers.remove(entityplayer);
				entityplayer.d(tracker);
			}
		}
	}

	private boolean d(EntityPlayer entityplayer) {
		return entityplayer.r().getPlayerChunkMap().a(entityplayer, tracker.ah, tracker.aj);
	}

	public void scanPlayers(List list) {
		for (int i = 0; i < list.size(); ++i) {
			updatePlayer((EntityPlayer) list.get(i));
		}
	}

	private Packet c() {
		if (tracker.dead)
			// CraftBukkit start - Remove useless error spam, just return
			// p.warn("Fetching addPacket for removed entity");
			return null;
		// CraftBukkit end

		if (tracker instanceof EntityItem)
			return new PacketPlayOutSpawnEntity(tracker, 2, 1);
		else if (tracker instanceof EntityPlayer)
			return new PacketPlayOutNamedEntitySpawn((EntityHuman) tracker);
		else if (tracker instanceof EntityMinecartAbstract) {
			EntityMinecartAbstract entityminecartabstract = (EntityMinecartAbstract) tracker;

			return new PacketPlayOutSpawnEntity(tracker, 10, entityminecartabstract.m());
		} else if (tracker instanceof EntityBoat)
			return new PacketPlayOutSpawnEntity(tracker, 1);
		else if (!(tracker instanceof IAnimal) && !(tracker instanceof EntityEnderDragon)) {
			if (tracker instanceof EntityFishingHook) {
				EntityHuman entityhuman = ((EntityFishingHook) tracker).owner;

				return new PacketPlayOutSpawnEntity(tracker, 90, entityhuman != null ? entityhuman.getId() : tracker.getId());
			} else if (tracker instanceof EntityArrow) {
				Entity entity = ((EntityArrow) tracker).shooter;

				return new PacketPlayOutSpawnEntity(tracker, 60, entity != null ? entity.getId() : tracker.getId());
			} else if (tracker instanceof EntitySnowball)
				return new PacketPlayOutSpawnEntity(tracker, 61);
			else if (tracker instanceof EntityPotion)
				return new PacketPlayOutSpawnEntity(tracker, 73, ((EntityPotion) tracker).getPotionValue());
			else if (tracker instanceof EntityThrownExpBottle)
				return new PacketPlayOutSpawnEntity(tracker, 75);
			else if (tracker instanceof EntityEnderPearl)
				return new PacketPlayOutSpawnEntity(tracker, 65);
			else if (tracker instanceof EntityEnderSignal)
				return new PacketPlayOutSpawnEntity(tracker, 72);
			else if (tracker instanceof EntityFireworks)
				return new PacketPlayOutSpawnEntity(tracker, 76);
			else {
				PacketPlayOutSpawnEntity packetplayoutspawnentity;

				if (tracker instanceof EntityFireball) {
					EntityFireball entityfireball = (EntityFireball) tracker;

					packetplayoutspawnentity = null;
					byte b0 = 63;

					if (tracker instanceof EntitySmallFireball) {
						b0 = 64;
					} else if (tracker instanceof EntityWitherSkull) {
						b0 = 66;
					}

					if (entityfireball.shooter != null) {
						packetplayoutspawnentity = new PacketPlayOutSpawnEntity(tracker, b0, ((EntityFireball) tracker).shooter.getId());
					} else {
						packetplayoutspawnentity = new PacketPlayOutSpawnEntity(tracker, b0, 0);
					}

					packetplayoutspawnentity.d((int) (entityfireball.dirX * 8000.0D));
					packetplayoutspawnentity.e((int) (entityfireball.dirY * 8000.0D));
					packetplayoutspawnentity.f((int) (entityfireball.dirZ * 8000.0D));
					return packetplayoutspawnentity;
				} else if (tracker instanceof EntityEgg)
					return new PacketPlayOutSpawnEntity(tracker, 62);
				else if (tracker instanceof EntityTNTPrimed)
					return new PacketPlayOutSpawnEntity(tracker, 50);
				else if (tracker instanceof EntityEnderCrystal)
					return new PacketPlayOutSpawnEntity(tracker, 51);
				else if (tracker instanceof EntityFallingBlock) {
					EntityFallingBlock entityfallingblock = (EntityFallingBlock) tracker;

					return new PacketPlayOutSpawnEntity(tracker, 70, Block.getId(entityfallingblock.f()) | entityfallingblock.data << 16);
				} else if (tracker instanceof EntityPainting)
					return new PacketPlayOutSpawnEntityPainting((EntityPainting) tracker);
				else if (tracker instanceof EntityItemFrame) {
					EntityItemFrame entityitemframe = (EntityItemFrame) tracker;

					packetplayoutspawnentity = new PacketPlayOutSpawnEntity(tracker, 71, entityitemframe.direction);
					packetplayoutspawnentity.a(MathHelper.d(entityitemframe.x * 32));
					packetplayoutspawnentity.b(MathHelper.d(entityitemframe.y * 32));
					packetplayoutspawnentity.c(MathHelper.d(entityitemframe.z * 32));
					return packetplayoutspawnentity;
				} else if (tracker instanceof EntityLeash) {
					EntityLeash entityleash = (EntityLeash) tracker;

					packetplayoutspawnentity = new PacketPlayOutSpawnEntity(tracker, 77);
					packetplayoutspawnentity.a(MathHelper.d(entityleash.x * 32));
					packetplayoutspawnentity.b(MathHelper.d(entityleash.y * 32));
					packetplayoutspawnentity.c(MathHelper.d(entityleash.z * 32));
					return packetplayoutspawnentity;
				} else if (tracker instanceof EntityExperienceOrb)
					return new PacketPlayOutSpawnEntityExperienceOrb((EntityExperienceOrb) tracker);
				else
					throw new IllegalArgumentException("Don\'t know how to add " + tracker.getClass() + "!");
			}
		} else {
			i = MathHelper.d(tracker.getHeadRotation() * 256.0F / 360.0F);
			return new PacketPlayOutSpawnEntityLiving((EntityLiving) tracker);
		}
	}

	public void clear(EntityPlayer entityplayer) {
		org.spigotmc.AsyncCatcher.catchOp("player tracker clear"); // Spigot
		if (trackedPlayers.contains(entityplayer)) {
			trackedPlayers.remove(entityplayer);
			entityplayer.d(tracker);
		}
	}
}
