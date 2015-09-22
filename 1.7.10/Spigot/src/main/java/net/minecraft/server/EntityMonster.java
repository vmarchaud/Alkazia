package net.minecraft.server;

import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent; // CraftBukkit

public abstract class EntityMonster extends EntityCreature implements IMonster {

	public EntityMonster(World world) {
		super(world);
		b = 5;
	}

	@Override
	public void e() {
		bb();
		float f = this.d(1.0F);

		if (f > 0.5F) {
			aU += 2;
		}

		super.e();
	}

	@Override
	public void h() {
		super.h();
		if (!world.isStatic && world.difficulty == EnumDifficulty.PEACEFUL) {
			this.die();
		}
	}

	@Override
	protected String H() {
		return "game.hostile.swim";
	}

	@Override
	protected String O() {
		return "game.hostile.swim.splash";
	}

	@Override
	protected Entity findTarget() {
		EntityHuman entityhuman = world.findNearbyVulnerablePlayer(this, 16.0D);

		return entityhuman != null && hasLineOfSight(entityhuman) ? entityhuman : null;
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (isInvulnerable())
			return false;
		else if (super.damageEntity(damagesource, f)) {
			Entity entity = damagesource.getEntity();

			if (passenger != entity && vehicle != entity) {
				if (entity != this) {
					// CraftBukkit start - We still need to call events for entities without goals
					if (entity != target && (this instanceof EntityBlaze || this instanceof EntityEnderman || this instanceof EntitySpider || this instanceof EntityGiantZombie || this instanceof EntitySilverfish)) {
						EntityTargetEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callEntityTargetEvent(this, entity, EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY);

						if (!event.isCancelled()) {
							if (event.getTarget() == null) {
								target = null;
							} else {
								target = ((org.bukkit.craftbukkit.entity.CraftEntity) event.getTarget()).getHandle();
							}
						}
					} else {
						target = entity;
					}
					// CraftBukkit end
				}

				return true;
			} else
				return true;
		} else
			return false;
	}

	@Override
	protected String aT() {
		return "game.hostile.hurt";
	}

	@Override
	protected String aU() {
		return "game.hostile.die";
	}

	@Override
	protected String o(int i) {
		return i > 4 ? "game.hostile.hurt.fall.big" : "game.hostile.hurt.fall.small";
	}

	@Override
	public boolean n(Entity entity) {
		float f = (float) getAttributeInstance(GenericAttributes.e).getValue();
		int i = 0;

		if (entity instanceof EntityLiving) {
			f += EnchantmentManager.a((EntityLiving) this, (EntityLiving) entity);
			i += EnchantmentManager.getKnockbackEnchantmentLevel(this, (EntityLiving) entity);
		}

		boolean flag = entity.damageEntity(DamageSource.mobAttack(this), f);

		if (flag) {
			if (i > 0) {
				entity.g(-MathHelper.sin(yaw * 3.1415927F / 180.0F) * i * 0.5F, 0.1D, MathHelper.cos(yaw * 3.1415927F / 180.0F) * i * 0.5F);
				motX *= 0.6D;
				motZ *= 0.6D;
			}

			int j = EnchantmentManager.getFireAspectEnchantmentLevel(this);

			if (j > 0) {
				// CraftBukkit start - Call a combust event when somebody hits with a fire enchanted item
				EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(getBukkitEntity(), entity.getBukkitEntity(), j * 4);
				org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);

				if (!combustEvent.isCancelled()) {
					entity.setOnFire(combustEvent.getDuration());
				}
				// CraftBukkit end
			}

			if (entity instanceof EntityLiving) {
				EnchantmentManager.a((EntityLiving) entity, (Entity) this);
			}

			EnchantmentManager.b(this, entity);
		}

		return flag;
	}

	@Override
	protected void a(Entity entity, float f) {
		if (attackTicks <= 0 && f < 2.0F && entity.boundingBox.e > boundingBox.b && entity.boundingBox.b < boundingBox.e) {
			attackTicks = 20;
			this.n(entity);
		}
	}

	@Override
	public float a(int i, int j, int k) {
		return 0.5F - world.n(i, j, k);
	}

	protected boolean j_() {
		int i = MathHelper.floor(locX);
		int j = MathHelper.floor(boundingBox.b);
		int k = MathHelper.floor(locZ);

		if (world.b(EnumSkyBlock.SKY, i, j, k) > random.nextInt(32))
			return false;
		else {
			int l = world.getLightLevel(i, j, k);

			if (world.P()) {
				int i1 = world.j;

				world.j = 10;
				l = world.getLightLevel(i, j, k);
				world.j = i1;
			}

			return l <= random.nextInt(8);
		}
	}

	@Override
	public boolean canSpawn() {
		return world.difficulty != EnumDifficulty.PEACEFUL && j_() && super.canSpawn();
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeMap().b(GenericAttributes.e);
	}

	@Override
	protected boolean aG() {
		return true;
	}
}
