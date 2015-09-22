package net.minecraft.server;

public class ItemArmor extends Item
{
  public static final int[] k = { 11, 16, 15, 13 };

  public static final String[] a = { "minecraft:items/empty_armor_slot_helmet", "minecraft:items/empty_armor_slot_chestplate", "minecraft:items/empty_armor_slot_leggings", "minecraft:items/empty_armor_slot_boots" };

  private static final IDispenseBehavior l = new DispenseBehaviorArmor();
  public final int b;
  public final int c;
  public final int d;
  private final EnumArmorMaterial m;

  public ItemArmor(EnumArmorMaterial paramEnumArmorMaterial, int paramInt1, int paramInt2)
  {
    this.m = paramEnumArmorMaterial;
    this.b = paramInt2;
    this.d = paramInt1;
    this.c = paramEnumArmorMaterial.b(paramInt2);
    setMaxDurability(paramEnumArmorMaterial.a(paramInt2));
    this.maxStackSize = 1;
    a(CreativeModeTab.j);
    BlockDispenser.M.a(this, l);
  }

  public int b()
  {
    return this.m.a();
  }

  public EnumArmorMaterial w_() {
    return this.m;
  }

  public boolean d_(ItemStack paramItemStack) {
    if (this.m != EnumArmorMaterial.LEATHER) {
      return false;
    }
    if (!paramItemStack.hasTag()) {
      return false;
    }
    if (!paramItemStack.getTag().hasKeyOfType("display", 10)) {
      return false;
    }
    if (!paramItemStack.getTag().getCompound("display").hasKeyOfType("color", 3)) {
      return false;
    }

    return true;
  }

  public int b(ItemStack paramItemStack) {
    if (this.m != EnumArmorMaterial.LEATHER) {
      return -1;
    }

    NBTTagCompound localNBTTagCompound1 = paramItemStack.getTag();
    if (localNBTTagCompound1 != null) {
      NBTTagCompound localNBTTagCompound2 = localNBTTagCompound1.getCompound("display");
      if ((localNBTTagCompound2 != null) && 
        (localNBTTagCompound2.hasKeyOfType("color", 3))) {
        return localNBTTagCompound2.getInt("color");
      }
    }

    return 10511680;
  }

  public void c(ItemStack paramItemStack) {
    if (this.m != EnumArmorMaterial.LEATHER) {
      return;
    }
    NBTTagCompound localNBTTagCompound1 = paramItemStack.getTag();
    if (localNBTTagCompound1 == null) {
      return;
    }
    NBTTagCompound localNBTTagCompound2 = localNBTTagCompound1.getCompound("display");
    if (localNBTTagCompound2.hasKey("color"))
      localNBTTagCompound2.remove("color");
  }

  public void b(ItemStack paramItemStack, int paramInt)
  {
    if (this.m != EnumArmorMaterial.LEATHER) {
      throw new UnsupportedOperationException("Can't dye non-leather!");
    }

    NBTTagCompound localNBTTagCompound1 = paramItemStack.getTag();

    if (localNBTTagCompound1 == null) {
      localNBTTagCompound1 = new NBTTagCompound();
      paramItemStack.setTag(localNBTTagCompound1);
    }

    NBTTagCompound localNBTTagCompound2 = localNBTTagCompound1.getCompound("display");
    if (!localNBTTagCompound1.hasKeyOfType("display", 10)) {
      localNBTTagCompound1.set("display", localNBTTagCompound2);
    }
    localNBTTagCompound2.setInt("color", paramInt);
  }

  public boolean a(ItemStack paramItemStack1, ItemStack paramItemStack2)
  {
    if (this.m.b() == paramItemStack2.getItem()) {
      return true;
    }
    return super.a(paramItemStack1, paramItemStack2);
  }

  public ItemStack a(ItemStack paramItemStack, World paramWorld, EntityHuman paramEntityHuman)
  {
    int i = EntityInsentient.c(paramItemStack) - 1;
    ItemStack localItemStack = paramEntityHuman.q(i);

    if (localItemStack == null) {
      paramEntityHuman.setEquipment(i, paramItemStack.cloneItemStack());
      paramItemStack.count = 0;
    }

    return paramItemStack;
  }
}