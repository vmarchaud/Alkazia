package net.minecraft.server;

import org.bukkit.event.player.PlayerShearEntityEvent; // CraftBukkit

public class EntityMushroomCow extends EntityCow {

	public EntityMushroomCow(World world) {
		super(world);
		this.a(0.9F, 1.3F);
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		ItemStack itemstack = entityhuman.inventory.getItemInHand();

		if (itemstack != null && itemstack.getItem() == Items.BOWL && getAge() >= 0) {
			if (itemstack.count == 1) {
				entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, new ItemStack(Items.MUSHROOM_SOUP));
				return true;
			}

			if (entityhuman.inventory.pickup(new ItemStack(Items.MUSHROOM_SOUP)) && !entityhuman.abilities.canInstantlyBuild) {
				entityhuman.inventory.splitStack(entityhuman.inventory.itemInHandIndex, 1);
				return true;
			}
		}

		if (itemstack != null && itemstack.getItem() == Items.SHEARS && getAge() >= 0) {
			// CraftBukkit start
			PlayerShearEntityEvent event = new PlayerShearEntityEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), getBukkitEntity());
			world.getServer().getPluginManager().callEvent(event);

			if (event.isCancelled())
				return false;

			this.die();
			world.addParticle("largeexplode", locX, locY + length / 2.0F, locZ, 0.0D, 0.0D, 0.0D);
			if (!world.isStatic) {
				EntityCow entitycow = new EntityCow(world);

				entitycow.setPositionRotation(locX, locY, locZ, yaw, pitch);
				entitycow.setHealth(getHealth());
				entitycow.aM = aM;
				world.addEntity(entitycow);

				for (int i = 0; i < 5; ++i) {
					world.addEntity(new EntityItem(world, locX, locY + length, locZ, new ItemStack(Blocks.RED_MUSHROOM)));
				}

				itemstack.damage(1, entityhuman);
				makeSound("mob.sheep.shear", 1.0F, 1.0F);
			}

			return true;
		} else
			return super.a(entityhuman);
	}

	public EntityMushroomCow c(EntityAgeable entityageable) {
		return new EntityMushroomCow(world);
	}

	@Override
	public EntityCow b(EntityAgeable entityageable) {
		return this.c(entityageable);
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityageable) {
		return this.c(entityageable);
	}
}
