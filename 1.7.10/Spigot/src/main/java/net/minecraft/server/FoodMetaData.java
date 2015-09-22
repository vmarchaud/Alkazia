package net.minecraft.server;

public class FoodMetaData {

	// CraftBukkit start - All made public
	public int foodLevel = 20;
	public float saturationLevel = 5.0F;
	public float exhaustionLevel;
	public int foodTickTimer;
	private EntityHuman entityhuman;
	// CraftBukkit end
	private int e = 20;

	public FoodMetaData() {
		throw new AssertionError("Whoopsie, we missed the bukkit.");
	} // CraftBukkit start - throw an error

	// CraftBukkit start - added EntityHuman constructor
	public FoodMetaData(EntityHuman entityhuman) {
		org.apache.commons.lang.Validate.notNull(entityhuman);
		this.entityhuman = entityhuman;
	}

	// CraftBukkit end

	public void eat(int i, float f) {
		foodLevel = Math.min(i + foodLevel, 20);
		saturationLevel = Math.min(saturationLevel + i * f * 2.0F, foodLevel);
	}

	public void a(ItemFood itemfood, ItemStack itemstack) {
		// CraftBukkit start
		int oldFoodLevel = foodLevel;

		org.bukkit.event.entity.FoodLevelChangeEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callFoodLevelChangeEvent(entityhuman, itemfood.getNutrition(itemstack) + oldFoodLevel);

		if (!event.isCancelled()) {
			eat(event.getFoodLevel() - oldFoodLevel, itemfood.getSaturationModifier(itemstack));
		}

		((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutUpdateHealth(((EntityPlayer) entityhuman).getBukkitEntity().getScaledHealth(), entityhuman.getFoodData().foodLevel, entityhuman.getFoodData().saturationLevel));
		// CraftBukkit end
	}

	public void a(EntityHuman entityhuman) {
		EnumDifficulty enumdifficulty = entityhuman.world.difficulty;

		e = foodLevel;
		if (exhaustionLevel > 4.0F) {
			exhaustionLevel -= 4.0F;
			if (saturationLevel > 0.0F) {
				saturationLevel = Math.max(saturationLevel - 1.0F, 0.0F);
			} else if (enumdifficulty != EnumDifficulty.PEACEFUL) {
				// CraftBukkit start
				org.bukkit.event.entity.FoodLevelChangeEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callFoodLevelChangeEvent(entityhuman, Math.max(foodLevel - 1, 0));

				if (!event.isCancelled()) {
					foodLevel = event.getFoodLevel();
				}

				((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutUpdateHealth(((EntityPlayer) entityhuman).getBukkitEntity().getScaledHealth(), foodLevel, saturationLevel));
				// CraftBukkit end
			}
		}

		if (entityhuman.world.getGameRules().getBoolean("naturalRegeneration") && foodLevel >= 18 && entityhuman.bR()) {
			++foodTickTimer;
			if (foodTickTimer >= 80) {
				// CraftBukkit - added RegainReason
				entityhuman.heal(1.0F, org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.SATIATED);
				this.a(entityhuman.world.spigotConfig.regenExhaustion); // Spigot - Change to use configurable value
				foodTickTimer = 0;
			}
		} else if (foodLevel <= 0) {
			++foodTickTimer;
			if (foodTickTimer >= 80) {
				if (entityhuman.getHealth() > 10.0F || enumdifficulty == EnumDifficulty.HARD || entityhuman.getHealth() > 1.0F && enumdifficulty == EnumDifficulty.NORMAL) {
					entityhuman.damageEntity(DamageSource.STARVE, 1.0F);
				}

				foodTickTimer = 0;
			}
		} else {
			foodTickTimer = 0;
		}
	}

	public void a(NBTTagCompound nbttagcompound) {
		if (nbttagcompound.hasKeyOfType("foodLevel", 99)) {
			foodLevel = nbttagcompound.getInt("foodLevel");
			foodTickTimer = nbttagcompound.getInt("foodTickTimer");
			saturationLevel = nbttagcompound.getFloat("foodSaturationLevel");
			exhaustionLevel = nbttagcompound.getFloat("foodExhaustionLevel");
		}
	}

	public void b(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInt("foodLevel", foodLevel);
		nbttagcompound.setInt("foodTickTimer", foodTickTimer);
		nbttagcompound.setFloat("foodSaturationLevel", saturationLevel);
		nbttagcompound.setFloat("foodExhaustionLevel", exhaustionLevel);
	}

	public int getFoodLevel() {
		return foodLevel;
	}

	public boolean c() {
		return foodLevel < 20;
	}

	public void a(float f) {
		exhaustionLevel = Math.min(exhaustionLevel + f, 40.0F);
	}

	public float getSaturationLevel() {
		return saturationLevel;
	}
}
