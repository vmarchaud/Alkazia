package net.minecraft.server;

import java.util.ArrayList;

public class EntityPainting extends EntityHanging {

	public EnumArt art;

	public EntityPainting(World world) {
		super(world);
		art = EnumArt.values()[random.nextInt(EnumArt.values().length)]; // CraftBukkit - generate a non-null painting
	}

	public EntityPainting(World world, int i, int j, int k, int l) {
		super(world, i, j, k, l);
		ArrayList arraylist = new ArrayList();
		EnumArt[] aenumart = EnumArt.values();
		int i1 = aenumart.length;

		for (int j1 = 0; j1 < i1; ++j1) {
			EnumArt enumart = aenumart[j1];

			art = enumart;
			setDirection(l);
			if (survives()) {
				arraylist.add(enumart);
			}
		}

		if (!arraylist.isEmpty()) {
			art = (EnumArt) arraylist.get(random.nextInt(arraylist.size()));
		}

		setDirection(l);
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		nbttagcompound.setString("Motive", art.B);
		super.b(nbttagcompound);
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		String s = nbttagcompound.getString("Motive");
		EnumArt[] aenumart = EnumArt.values();
		int i = aenumart.length;

		for (int j = 0; j < i; ++j) {
			EnumArt enumart = aenumart[j];

			if (enumart.B.equals(s)) {
				art = enumart;
			}
		}

		if (art == null) {
			art = EnumArt.KEBAB;
		}

		super.a(nbttagcompound);
	}

	@Override
	public int f() {
		return art.C;
	}

	@Override
	public int i() {
		return art.D;
	}

	@Override
	public void b(Entity entity) {
		if (entity instanceof EntityHuman) {
			EntityHuman entityhuman = (EntityHuman) entity;

			if (entityhuman.abilities.canInstantlyBuild)
				return;
		}

		this.a(new ItemStack(Items.PAINTING), 0.0F);
	}
}
