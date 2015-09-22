package net.minecraft.server;

import java.util.Random;

// CraftBukkit start
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

// CraftBukkit end

public class EntitySheep extends EntityAnimal {

	private final InventoryCrafting bq = new InventoryCrafting(new ContainerSheepBreed(this), 2, 1);
	public static final float[][] bp = new float[][] { { 1.0F, 1.0F, 1.0F }, { 0.85F, 0.5F, 0.2F }, { 0.7F, 0.3F, 0.85F }, { 0.4F, 0.6F, 0.85F }, { 0.9F, 0.9F, 0.2F }, { 0.5F, 0.8F, 0.1F }, { 0.95F, 0.5F, 0.65F }, { 0.3F, 0.3F, 0.3F }, { 0.6F, 0.6F, 0.6F }, { 0.3F, 0.5F, 0.6F }, { 0.5F, 0.25F, 0.7F }, { 0.2F, 0.3F, 0.7F }, { 0.4F, 0.3F, 0.2F }, { 0.4F, 0.5F, 0.2F }, { 0.6F, 0.2F, 0.2F },
			{ 0.1F, 0.1F, 0.1F } };
	private int br;
	private PathfinderGoalEatTile bs = new PathfinderGoalEatTile(this);

	public EntitySheep(World world) {
		super(world);
		this.a(0.9F, 1.3F);
		getNavigation().a(true);
		goalSelector.a(0, new PathfinderGoalFloat(this));
		goalSelector.a(1, new PathfinderGoalPanic(this, 1.25D));
		goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
		goalSelector.a(3, new PathfinderGoalTempt(this, 1.1D, Items.WHEAT, false));
		goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.1D));
		goalSelector.a(5, bs);
		goalSelector.a(6, new PathfinderGoalRandomStroll(this, 1.0D));
		goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
		goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
		bq.setItem(0, new ItemStack(Items.INK_SACK, 1, 0));
		bq.setItem(1, new ItemStack(Items.INK_SACK, 1, 0));
		bq.resultInventory = new InventoryCraftResult(); // CraftBukkit - add result slot for event
	}

	@Override
	protected boolean bk() {
		return true;
	}

	@Override
	protected void bn() {
		br = bs.f();
		super.bn();
	}

	@Override
	public void e() {
		if (world.isStatic) {
			br = Math.max(0, br - 1);
		}

		super.e();
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeInstance(GenericAttributes.maxHealth).setValue(8.0D);
		getAttributeInstance(GenericAttributes.d).setValue(0.23000000417232513D);
	}

	@Override
	protected void c() {
		super.c();
		datawatcher.a(16, new Byte((byte) 0));
	}

	@Override
	protected void dropDeathLoot(boolean flag, int i) {
		if (!isSheared()) {
			this.a(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, getColor()), 0.0F);
		}
	}

	@Override
	 protected Item getLoot() {
        return this.isBurning() ? Items.cooked_sheep : Items.sheep;
    }

	@Override
	public boolean a(EntityHuman entityhuman) {
		ItemStack itemstack = entityhuman.inventory.getItemInHand();

		if (itemstack != null && itemstack.getItem() == Items.SHEARS && !isSheared() && !isBaby()) {
			if (!world.isStatic) {
				// CraftBukkit start
				PlayerShearEntityEvent event = new PlayerShearEntityEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), getBukkitEntity());
				world.getServer().getPluginManager().callEvent(event);

				if (event.isCancelled())
					return false;

				setSheared(true);
				int i = 1 + random.nextInt(3);

				for (int j = 0; j < i; ++j) {
					EntityItem entityitem = this.a(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, getColor()), 1.0F);

					entityitem.motY += random.nextFloat() * 0.05F;
					entityitem.motX += (random.nextFloat() - random.nextFloat()) * 0.1F;
					entityitem.motZ += (random.nextFloat() - random.nextFloat()) * 0.1F;
				}
			}

			itemstack.damage(1, entityhuman);
			makeSound("mob.sheep.shear", 1.0F, 1.0F);
		}

		return super.a(entityhuman);
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setBoolean("Sheared", isSheared());
		nbttagcompound.setByte("Color", (byte) getColor());
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		setSheared(nbttagcompound.getBoolean("Sheared"));
		setColor(nbttagcompound.getByte("Color"));
	}

	@Override
	protected String t() {
		return "mob.sheep.say";
	}

	@Override
	protected String aT() {
		return "mob.sheep.say";
	}

	@Override
	protected String aU() {
		return "mob.sheep.say";
	}

	@Override
	protected void a(int i, int j, int k, Block block) {
		makeSound("mob.sheep.step", 0.15F, 1.0F);
	}

	public int getColor() {
		return datawatcher.getByte(16) & 15;
	}

	public void setColor(int i) {
		byte b0 = datawatcher.getByte(16);

		datawatcher.watch(16, Byte.valueOf((byte) (b0 & 240 | i & 15)));
	}

	public boolean isSheared() {
		return (datawatcher.getByte(16) & 16) != 0;
	}

	public void setSheared(boolean flag) {
		byte b0 = datawatcher.getByte(16);

		if (flag) {
			datawatcher.watch(16, Byte.valueOf((byte) (b0 | 16)));
		} else {
			datawatcher.watch(16, Byte.valueOf((byte) (b0 & -17)));
		}
	}

	public static int a(Random random) {
		int i = random.nextInt(100);

		return i < 5 ? 15 : i < 10 ? 7 : i < 15 ? 8 : i < 18 ? 12 : random.nextInt(500) == 0 ? 6 : 0;
	}

	public EntitySheep b(EntityAgeable entityageable) {
		EntitySheep entitysheep = (EntitySheep) entityageable;
		EntitySheep entitysheep1 = new EntitySheep(world);
		int i = this.a(this, entitysheep);

		entitysheep1.setColor(15 - i);
		return entitysheep1;
	}

	@Override
	public void p() {
		// CraftBukkit start
		SheepRegrowWoolEvent event = new SheepRegrowWoolEvent((org.bukkit.entity.Sheep) getBukkitEntity());
		world.getServer().getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			setSheared(false);
		}
		// CraftBukkit end

		if (isBaby()) {
			this.a(60);
		}
	}

	@Override
	public GroupDataEntity prepare(GroupDataEntity groupdataentity) {
		groupdataentity = super.prepare(groupdataentity);
		setColor(a(world.random));
		return groupdataentity;
	}

	private int a(EntityAnimal entityanimal, EntityAnimal entityanimal1) {
		int i = this.b(entityanimal);
		int j = this.b(entityanimal1);

		bq.getItem(0).setData(i);
		bq.getItem(1).setData(j);
		ItemStack itemstack = CraftingManager.getInstance().craft(bq, ((EntitySheep) entityanimal).world);
		int k;

		if (itemstack != null && itemstack.getItem() == Items.INK_SACK) {
			k = itemstack.getData();
		} else {
			k = world.random.nextBoolean() ? i : j;
		}

		return k;
	}

	private int b(EntityAnimal entityanimal) {
		return 15 - ((EntitySheep) entityanimal).getColor();
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityageable) {
		return this.b(entityageable);
	}
}
