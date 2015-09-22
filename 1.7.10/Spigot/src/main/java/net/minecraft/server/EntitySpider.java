package net.minecraft.server;

import org.bukkit.event.entity.EntityTargetEvent; // CraftBukkit

public class EntitySpider extends EntityMonster {

	public EntitySpider(World world) {
		super(world);
		this.a(1.4F, 0.9F);
	}

	@Override
	protected void c() {
		super.c();
		datawatcher.a(16, new Byte((byte) 0));
	}

	@Override
	public void h() {
		super.h();
		if (!world.isStatic) {
			this.a(positionChanged);
		}
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeInstance(GenericAttributes.maxHealth).setValue(16.0D);
		getAttributeInstance(GenericAttributes.d).setValue(0.800000011920929D);
	}

	@Override
	protected Entity findTarget() {
		float f = this.d(1.0F);

		if (f < 0.5F) {
			double d0 = 16.0D;

			return world.findNearbyVulnerablePlayer(this, d0);
		} else
			return null;
	}

	@Override
	protected String t() {
		return "mob.spider.say";
	}

	@Override
	protected String aT() {
		return "mob.spider.say";
	}

	@Override
	protected String aU() {
		return "mob.spider.death";
	}

	@Override
	protected void a(int i, int j, int k, Block block) {
		makeSound("mob.spider.step", 0.15F, 1.0F);
	}

	@Override
	protected void a(Entity entity, float f) {
		float f1 = this.d(1.0F);

		if (f1 > 0.5F && random.nextInt(100) == 0) {
			// CraftBukkit start
			EntityTargetEvent event = new EntityTargetEvent(getBukkitEntity(), null, EntityTargetEvent.TargetReason.FORGOT_TARGET);
			world.getServer().getPluginManager().callEvent(event);

			if (!event.isCancelled()) {
				if (event.getTarget() == null) {
					target = null;
				} else {
					target = ((org.bukkit.craftbukkit.entity.CraftEntity) event.getTarget()).getHandle();
				}
				return;
			}
			// CraftBukkit end
		} else {
			if (f > 2.0F && f < 6.0F && random.nextInt(10) == 0) {
				if (onGround) {
					double d0 = entity.locX - locX;
					double d1 = entity.locZ - locZ;
					float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1);

					motX = d0 / f2 * 0.5D * 0.800000011920929D + motX * 0.20000000298023224D;
					motZ = d1 / f2 * 0.5D * 0.800000011920929D + motZ * 0.20000000298023224D;
					motY = 0.4000000059604645D;
				}
			} else {
				super.a(entity, f);
			}
		}
	}

	@Override
	protected Item getLoot() {
		return Items.STRING;
	}

	@Override
	protected void dropDeathLoot(boolean flag, int i) {
		super.dropDeathLoot(flag, i);
		if (flag && (random.nextInt(3) == 0 || random.nextInt(1 + i) > 0)) {
			this.a(Items.SPIDER_EYE, 1);
		}
	}

	@Override
	public boolean h_() {
		return bZ();
	}

	@Override
	public void as() {
	}

	@Override
	public EnumMonsterType getMonsterType() {
		return EnumMonsterType.ARTHROPOD;
	}

	@Override
	public boolean d(MobEffect mobeffect) {
		return mobeffect.getEffectId() == MobEffectList.POISON.id ? false : super.d(mobeffect);
	}

	public boolean bZ() {
		return (datawatcher.getByte(16) & 1) != 0;
	}

	public void a(boolean flag) {
		byte b0 = datawatcher.getByte(16);

		if (flag) {
			b0 = (byte) (b0 | 1);
		} else {
			b0 &= -2;
		}

		datawatcher.watch(16, Byte.valueOf(b0));
	}

	@Override
	public GroupDataEntity prepare(GroupDataEntity groupdataentity) {
		Object object = super.prepare(groupdataentity);

		if (world.random.nextInt(100) == 0) {
			EntitySkeleton entityskeleton = new EntitySkeleton(world);

			entityskeleton.setPositionRotation(locX, locY, locZ, yaw, 0.0F);
			entityskeleton.prepare((GroupDataEntity) null);
			world.addEntity(entityskeleton, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.JOCKEY); // CraftBukkit - add SpawnReason
			entityskeleton.mount(this);
		}

		if (object == null) {
			object = new GroupDataSpider();
			if (world.difficulty == EnumDifficulty.HARD && world.random.nextFloat() < 0.1F * world.b(locX, locY, locZ)) {
				((GroupDataSpider) object).a(world.random);
			}
		}

		if (object instanceof GroupDataSpider) {
			int i = ((GroupDataSpider) object).a;

			if (i > 0 && MobEffectList.byId[i] != null) {
				addEffect(new MobEffect(i, Integer.MAX_VALUE));
			}
		}

		return (GroupDataEntity) object;
	}
}
