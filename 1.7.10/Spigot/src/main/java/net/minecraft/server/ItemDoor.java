package net.minecraft.server;

public class ItemDoor extends Item {
	private Material a;
	private final DoorType doortype;
	
	public enum DoorType {
		DEFAULT_OAK,
		BIRCH,
		SPRUCE,
		JUNGLE,
		ACACIA,
		DARK_OAK,
		IRON;
	}
	
	public ItemDoor(Material material, DoorType doortype) {
		a = material;
		maxStackSize = 1; // TODO consider 64
		a(CreativeModeTab.d);
		this.doortype = doortype;
	}
	
    @Override
	public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l, float f, float f1, float f2) {
		if (l != 1) {
			return false;
		}
		
		j++;
		
		Block block;
		if (a == Material.WOOD) {
			switch(doortype) {
			case DEFAULT_OAK:
				block = Blocks.WOODEN_DOOR;
				break;
			default:
				System.out.println("Unknown door type at x:" + i + " y:" + j + " z:" + k);
				block = Blocks.WOODEN_DOOR;
				break;
			
			}
		} else
			block = Blocks.IRON_DOOR_BLOCK;

		if ((entityhuman.a(i, j, k, l, itemstack)) && (entityhuman.a(i, j + 1, k, l, itemstack))) {
			if (!block.canPlace(world, i, j, k)) {
				return false;
			}
			int i1 = MathHelper.floor((entityhuman.yaw + 180.0F) * 4.0F / 360.0F - 0.5D) & 0x3;

			place(world, i, j, k, i1, block);
			itemstack.count -= 1;
			return true;
		}

		return false;
	}

	public static void place(World world, int i, int j, int k, int l, Block block) {
		byte b0 = 0;
		byte b1 = 0;

		if (l == 0) {
			b1 = 1;
		}

		if (l == 1) {
			b0 = -1;
		}

		if (l == 2) {
			b1 = -1;
		}

		if (l == 3) {
			b0 = 1;
		}

		int i1 = (world.getType(i - b0, j, k - b1).r() ? 1 : 0) + (world.getType(i - b0, j + 1, k - b1).r() ? 1 : 0);
		int j1 = (world.getType(i + b0, j, k + b1).r() ? 1 : 0) + (world.getType(i + b0, j + 1, k + b1).r() ? 1 : 0);
		boolean flag = (world.getType(i - b0, j, k - b1) == block) || (world.getType(i - b0, j + 1, k - b1) == block);
		boolean flag1 = (world.getType(i + b0, j, k + b1) == block) || (world.getType(i + b0, j + 1, k + b1) == block);
		boolean flag2 = false;

		if ((flag) && (!flag1))
			flag2 = true;
		else if (j1 > i1) {
			flag2 = true;
		}

		world.setTypeAndData(i, j, k, block, l, 2);
		world.setTypeAndData(i, j + 1, k, block, 0x8 | (flag2 ? 1 : 0), 2);
		world.applyPhysics(i, j, k, block);
		world.applyPhysics(i, j + 1, k, block);
	}
}