package net.minecraft.server;

public class EnchantmentProtection extends Enchantment
{
  private static final String[] E = { "all", "fire", "fall", "explosion", "projectile" };

  private static final int[] F = { 1, 10, 5, 5, 3 };

  private static final int[] G = { 11, 8, 6, 8, 6 };

  private static final int[] H = { 20, 12, 10, 12, 15 };
  public final int a;

  public EnchantmentProtection(int paramInt1, MinecraftKey paramMinecraftKey, int paramInt2, int paramInt3)
  {
    super(paramInt1, paramMinecraftKey, paramInt2, EnchantmentSlotType.ARMOR);
    this.a = paramInt3;

    if (paramInt3 == 2)
      this.slot = EnchantmentSlotType.ARMOR_FEET;
  }

  public int a(int paramInt)
  {
    return F[this.a] + (paramInt - 1) * G[this.a];
  }

  public int b(int paramInt)
  {
    return a(paramInt) + H[this.a];
  }

  public int getMaxLevel()
  {
    return 4;
  }

  public int a(int paramInt, DamageSource paramDamageSource)
  {
    if (paramDamageSource.ignoresInvulnerability()) {
      return 0;
    }

    float f = (6 + paramInt * paramInt) / 3.0F;

    if (this.a == 0) {
      return MathHelper.d(f * 0.95F); // Alkazia -P4
    }
    if ((this.a == 1) && (paramDamageSource.o())) {
      return MathHelper.d(f * 1.25F);
    }
    if ((this.a == 2) && (paramDamageSource == DamageSource.FALL)) {
      return MathHelper.d(f * 2.5F);
    }
    if ((this.a == 3) && (paramDamageSource.isExplosion())) {
      return MathHelper.d(f * 1.5F);
    }
    if ((this.a == 4) && (paramDamageSource.a())) {
      return MathHelper.d(f * 1.5F);
    }
    return 0;
  }

  public String a()
  {
    return "enchantment.protect." + E[this.a];
  }

  public boolean a(Enchantment paramEnchantment)
  {
    if ((paramEnchantment instanceof EnchantmentProtection)) {
      EnchantmentProtection localEnchantmentProtection = (EnchantmentProtection)paramEnchantment;

      if (localEnchantmentProtection.a == this.a) {
        return false;
      }
      if ((this.a == 2) || (localEnchantmentProtection.a == 2)) {
        return true;
      }
      return false;
    }
    return super.a(paramEnchantment);
  }

  public static int a(Entity paramEntity, int paramInt) {
    int i = EnchantmentManager.a(Enchantment.PROTECTION_FIRE.id, paramEntity.getEquipment());

    if (i > 0) {
      paramInt -= MathHelper.d(paramInt * (i * 0.15F));
    }

    return paramInt;
  }

  public static double a(Entity paramEntity, double paramDouble) {
    int i = EnchantmentManager.a(Enchantment.PROTECTION_EXPLOSIONS.id, paramEntity.getEquipment());

    if (i > 0) {
      paramDouble -= MathHelper.floor(paramDouble * (i * 0.15F));
    }

    return paramDouble;
  }
}