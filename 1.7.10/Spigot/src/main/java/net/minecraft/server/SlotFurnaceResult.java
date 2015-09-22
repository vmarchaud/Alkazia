package net.minecraft.server;

// CraftBukkit start
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.FurnaceExtractEvent;

// CraftBukkit end

public class SlotFurnaceResult extends Slot {

	private EntityHuman a;
	private int b;

	public SlotFurnaceResult(EntityHuman entityhuman, IInventory iinventory, int i, int j, int k) {
		super(iinventory, i, j, k);
		a = entityhuman;
	}

	@Override
	public boolean isAllowed(ItemStack itemstack) {
		return false;
	}

	@Override
	public ItemStack a(int i) {
		if (hasItem()) {
			b += Math.min(i, getItem().count);
		}

		return super.a(i);
	}

	@Override
	public void a(EntityHuman entityhuman, ItemStack itemstack) {
		b(itemstack);
		super.a(entityhuman, itemstack);
	}

	@Override
	protected void a(ItemStack itemstack, int i) {
		b += i;
		b(itemstack);
	}

	@Override
	protected void b(ItemStack itemstack) {
		itemstack.a(a.world, a, b);
		if (!a.world.isStatic) {
			int i = b;
			float f = RecipesFurnace.getInstance().b(itemstack);
			int j;

			if (f == 0.0F) {
				i = 0;
			} else if (f < 1.0F) {
				j = MathHelper.d(i * f);
				if (j < MathHelper.f(i * f) && (float) Math.random() < i * f - j) {
					++j;
				}

				i = j;
			}

			// CraftBukkit start - fire FurnaceExtractEvent
			Player player = (Player) a.getBukkitEntity();
			TileEntityFurnace furnace = (TileEntityFurnace) inventory;
			org.bukkit.block.Block block = a.world.getWorld().getBlockAt(furnace.x, furnace.y, furnace.z);

			FurnaceExtractEvent event = new FurnaceExtractEvent(player, block, org.bukkit.craftbukkit.util.CraftMagicNumbers.getMaterial(itemstack.getItem()), itemstack.count, i);
			a.world.getServer().getPluginManager().callEvent(event);

			i = event.getExpToDrop();
			// CraftBukkit end

			while (i > 0) {
				j = EntityExperienceOrb.getOrbValue(i);
				i -= j;
				a.world.addEntity(new EntityExperienceOrb(a.world, a.locX, a.locY + 0.5D, a.locZ + 0.5D, j));
			}
		}

		b = 0;
		if (itemstack.getItem() == Items.IRON_INGOT) {
			a.a(AchievementList.k, 1);
		}

		if (itemstack.getItem() == Items.COOKED_FISH) {
			a.a(AchievementList.p, 1);
		}
	}
}
