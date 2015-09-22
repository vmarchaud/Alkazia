package net.minecraft.server;

public class EntityDamageSourceIndirect extends EntityDamageSource {

	private Entity owner;

	public EntityDamageSourceIndirect(String s, Entity entity, Entity entity1) {
		super(s, entity);
		owner = entity1;
	}

	@Override
	public Entity i() {
		return p;
	}

	@Override
	public Entity getEntity() {
		return owner;
	}

	@Override
	public IChatBaseComponent getLocalizedDeathMessage(EntityLiving entityliving) {
		IChatBaseComponent ichatbasecomponent = owner == null ? p.getScoreboardDisplayName() : owner.getScoreboardDisplayName();
		ItemStack itemstack = owner instanceof EntityLiving ? ((EntityLiving) owner).be() : null;
		String s = "death.attack." + translationIndex;
		String s1 = s + ".item";

		return itemstack != null && itemstack.hasName() && LocaleI18n.c(s1) ? new ChatMessage(s1, new Object[] { entityliving.getScoreboardDisplayName(), ichatbasecomponent, itemstack.E() }) : new ChatMessage(s, new Object[] { entityliving.getScoreboardDisplayName(), ichatbasecomponent });
	}

	// CraftBukkit start
	public Entity getProximateDamageSource() {
		return super.getEntity();
	}
	// CraftBukkit end
}
