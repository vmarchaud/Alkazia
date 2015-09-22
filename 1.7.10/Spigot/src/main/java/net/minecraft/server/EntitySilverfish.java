package net.minecraft.server;

import net.minecraft.util.org.apache.commons.lang3.tuple.ImmutablePair;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class EntitySilverfish extends EntityMonster {

	private int bp;

	public EntitySilverfish(World world) {
		super(world);
		this.a(0.3F, 0.7F);
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeInstance(GenericAttributes.maxHealth).setValue(8.0D);
		getAttributeInstance(GenericAttributes.d).setValue(0.6000000238418579D);
		getAttributeInstance(GenericAttributes.e).setValue(1.0D);
	}

	@Override
	protected boolean g_() {
		return false;
	}

	@Override
	protected Entity findTarget() {
		double d0 = 8.0D;

		return world.findNearbyVulnerablePlayer(this, d0);
	}

	@Override
	protected String t() {
		return "mob.silverfish.say";
	}

	@Override
	protected String aT() {
		return "mob.silverfish.hit";
	}

	@Override
	protected String aU() {
		return "mob.silverfish.kill";
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (isInvulnerable())
			return false;
		else {
			if (bp <= 0 && (damagesource instanceof EntityDamageSource || damagesource == DamageSource.MAGIC)) {
				bp = 20;
			}

			return super.damageEntity(damagesource, f);
		}
	}

	@Override
	protected void a(Entity entity, float f) {
		if (attackTicks <= 0 && f < 1.2F && entity.boundingBox.e > boundingBox.b && entity.boundingBox.b < boundingBox.e) {
			attackTicks = 20;
			this.n(entity);
		}
	}

	@Override
	protected void a(int i, int j, int k, Block block) {
		makeSound("mob.silverfish.step", 0.15F, 1.0F);
	}

	@Override
	protected Item getLoot() {
		return Item.getById(0);
	}

	@Override
	public void h() {
		aM = yaw;
		super.h();
	}

	@Override
	protected void bq() {
		super.bq();
		if (!world.isStatic) {
			int i;
			int j;
			int k;
			int l;

			if (bp > 0) {
				--bp;
				if (bp == 0) {
					i = MathHelper.floor(locX);
					j = MathHelper.floor(locY);
					k = MathHelper.floor(locZ);
					boolean flag = false;

					for (int i1 = 0; !flag && i1 <= 5 && i1 >= -5; i1 = i1 <= 0 ? 1 - i1 : 0 - i1) {
						for (l = 0; !flag && l <= 10 && l >= -10; l = l <= 0 ? 1 - l : 0 - l) {
							for (int j1 = 0; !flag && j1 <= 10 && j1 >= -10; j1 = j1 <= 0 ? 1 - j1 : 0 - j1) {
								if (world.getType(i + l, j + i1, k + j1) == Blocks.MONSTER_EGGS) {
									// CraftBukkit start
									if (CraftEventFactory.callEntityChangeBlockEvent(this, i + l, j + i1, k + j1, Blocks.AIR, 0).isCancelled()) {
										continue;
									}
									// CraftBukkit end
									if (!world.getGameRules().getBoolean("mobGriefing")) {
										int k1 = world.getData(i + l, j + i1, k + j1);
										ImmutablePair immutablepair = BlockMonsterEggs.b(k1);

										world.setTypeAndData(i + l, j + i1, k + j1, (Block) immutablepair.getLeft(), ((Integer) immutablepair.getRight()).intValue(), 3);
									} else {
										world.setAir(i + l, j + i1, k + j1, false);
									}

									Blocks.MONSTER_EGGS.postBreak(world, i + l, j + i1, k + j1, 0);
									if (random.nextBoolean()) {
										flag = true;
										break;
									}
								}
							}
						}
					}
				}
			}

			if (target == null && !bS()) {
				i = MathHelper.floor(locX);
				j = MathHelper.floor(locY + 0.5D);
				k = MathHelper.floor(locZ);
				int l1 = random.nextInt(6);
				Block block = world.getType(i + Facing.b[l1], j + Facing.c[l1], k + Facing.d[l1]);

				l = world.getData(i + Facing.b[l1], j + Facing.c[l1], k + Facing.d[l1]);
				if (BlockMonsterEggs.a(block)) {
					// CraftBukkit start
					if (CraftEventFactory.callEntityChangeBlockEvent(this, i + Facing.b[l1], j + Facing.c[l1], k + Facing.d[l1], Blocks.MONSTER_EGGS, Block.getId(Block.getById(l))).isCancelled())
						return;

					world.setTypeAndData(i + Facing.b[l1], j + Facing.c[l1], k + Facing.d[l1], Blocks.MONSTER_EGGS, BlockMonsterEggs.a(block, l), 3);
					s();
					this.die();
				} else {
					bQ();
				}
			} else if (target != null && !bS()) {
				target = null;
			}
		}
	}

	@Override
	public float a(int i, int j, int k) {
		return world.getType(i, j - 1, k) == Blocks.STONE ? 10.0F : super.a(i, j, k);
	}

	@Override
	protected boolean j_() {
		return true;
	}

	@Override
	public boolean canSpawn() {
		if (super.canSpawn()) {
			EntityHuman entityhuman = world.findNearbyPlayer(this, 5.0D);

			return entityhuman == null;
		} else
			return false;
	}

	@Override
	public EnumMonsterType getMonsterType() {
		return EnumMonsterType.ARTHROPOD;
	}
}
