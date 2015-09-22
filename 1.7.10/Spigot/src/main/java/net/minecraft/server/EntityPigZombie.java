package net.minecraft.server;

import java.util.List;
import java.util.UUID;

import org.bukkit.event.entity.EntityTargetEvent; // CraftBukkit

public class EntityPigZombie extends EntityZombie {

	private static final UUID bq = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
	private static final AttributeModifier br = new AttributeModifier(bq, "Attacking speed boost", 0.45D, 0).a(false);
	public int angerLevel; // CraftBukkit - private -> public
	private int soundDelay;
	private Entity bu;

	public EntityPigZombie(World world) {
		super(world);
		fireProof = true;
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeInstance(bp).setValue(0.0D);
		getAttributeInstance(GenericAttributes.d).setValue(0.5D);
		getAttributeInstance(GenericAttributes.e).setValue(5.0D);
	}

	@Override
	protected boolean bk() {
		return false;
	}

	@Override
	public void h() {
		if (bu != target && !world.isStatic) {
			AttributeInstance attributeinstance = getAttributeInstance(GenericAttributes.d);

			attributeinstance.b(br);
			if (target != null) {
				attributeinstance.a(br);
			}
		}

		bu = target;
		if (soundDelay > 0 && --soundDelay == 0) {
			makeSound("mob.zombiepig.zpigangry", bf() * 2.0F, ((random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F) * 1.8F);
		}

		super.h();
	}

	@Override
	public boolean canSpawn() {
		return world.difficulty != EnumDifficulty.PEACEFUL && world.b(boundingBox) && world.getCubes(this, boundingBox).isEmpty() && !world.containsLiquid(boundingBox);
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setShort("Anger", (short) angerLevel);
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		angerLevel = nbttagcompound.getShort("Anger");
	}

	@Override
	protected Entity findTarget() {
		return angerLevel == 0 ? null : super.findTarget();
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (isInvulnerable())
			return false;
		else {
			Entity entity = damagesource.getEntity();

			if (entity instanceof EntityHuman) {
				List list = world.getEntities(this, boundingBox.grow(32.0D, 32.0D, 32.0D));

				for (int i = 0; i < list.size(); ++i) {
					Entity entity1 = (Entity) list.get(i);

					if (entity1 instanceof EntityPigZombie) {
						EntityPigZombie entitypigzombie = (EntityPigZombie) entity1;

						entitypigzombie.c(entity, EntityTargetEvent.TargetReason.PIG_ZOMBIE_TARGET);
					}
				}

				this.c(entity, EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY);
			}

			return super.damageEntity(damagesource, f);
		}
	}

	// CraftBukkit start
	private void c(Entity entity, EntityTargetEvent.TargetReason reason) { // add TargetReason
		EntityTargetEvent event = new EntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), reason);
		world.getServer().getPluginManager().callEvent(event);

		if (event.isCancelled())
			return;

		if (event.getTarget() == null) {
			target = null;
			return;
		}
		entity = ((org.bukkit.craftbukkit.entity.CraftEntity) event.getTarget()).getHandle();
		// CraftBukkit end

		target = entity;
		angerLevel = 400 + random.nextInt(400);
		soundDelay = random.nextInt(40);
	}

	@Override
	protected String t() {
		return "mob.zombiepig.zpig";
	}

	@Override
	protected String aT() {
		return "mob.zombiepig.zpighurt";
	}

	@Override
	protected String aU() {
		return "mob.zombiepig.zpigdeath";
	}

	@Override
	protected void dropDeathLoot(boolean flag, int i) {
		int j = random.nextInt(2 + i);

		int k;

		for (k = 0; k < j; ++k) {
			this.a(Items.ROTTEN_FLESH, 1);
		}

		j = random.nextInt(2 + i);

		for (k = 0; k < j; ++k) {
			this.a(Items.GOLD_NUGGET, 1);
		}
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		return false;
	}

	@Override
	protected void getRareDrop(int i) {
		this.a(Items.GOLD_INGOT, 1);
	}

	@Override
	protected void bC() {
		setEquipment(0, new ItemStack(Items.GOLD_SWORD));
	}

	@Override
	public GroupDataEntity prepare(GroupDataEntity groupdataentity) {
		super.prepare(groupdataentity);
		setVillager(false);
		return groupdataentity;
	}
}
