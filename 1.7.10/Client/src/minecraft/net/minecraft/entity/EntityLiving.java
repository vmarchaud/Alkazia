package net.minecraft.entity;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityLiving extends EntityLivingBase
{
    /** Number of ticks since this EntityLiving last produced its sound */
    public int livingSoundTime;

    /** The experience points the Entity gives. */
    protected int experienceValue;
    private EntityLookHelper lookHelper;
    private EntityMoveHelper moveHelper;

    /** Entity jumping helper */
    private EntityJumpHelper jumpHelper;
    private EntityBodyHelper bodyHelper;
    private PathNavigate navigator;
    protected final EntityAITasks tasks;
    protected final EntityAITasks targetTasks;

    /** The active target the Task system uses for tracking */
    private EntityLivingBase attackTarget;
    private EntitySenses senses;

    /** Equipment (armor and held item) for this entity. */
    private ItemStack[] equipment = new ItemStack[5];

    /** Chances for each equipment piece from dropping when this entity dies. */
    protected float[] equipmentDropChances = new float[5];

    /** Whether this entity can pick up items from the ground. */
    private boolean canPickUpLoot;

    /** Whether this entity should NOT despawn. */
    private boolean persistenceRequired;
    protected float defaultPitch;

    /** This entity's current target. */
    private Entity currentTarget;

    /** How long to keep a specific target entity */
    protected int numTicksToChaseTarget;
    private boolean isLeashed;
    private Entity leashedToEntity;
    private NBTTagCompound field_110170_bx;
    private static final String __OBFID = "CL_00001550";

    public EntityLiving(World p_i1595_1_)
    {
        super(p_i1595_1_);
        this.tasks = new EntityAITasks(p_i1595_1_ != null && p_i1595_1_.theProfiler != null ? p_i1595_1_.theProfiler : null);
        this.targetTasks = new EntityAITasks(p_i1595_1_ != null && p_i1595_1_.theProfiler != null ? p_i1595_1_.theProfiler : null);
        this.lookHelper = new EntityLookHelper(this);
        this.moveHelper = new EntityMoveHelper(this);
        this.jumpHelper = new EntityJumpHelper(this);
        this.bodyHelper = new EntityBodyHelper(this);
        this.navigator = new PathNavigate(this, p_i1595_1_);
        this.senses = new EntitySenses(this);

        for (int var2 = 0; var2 < this.equipmentDropChances.length; ++var2)
        {
            this.equipmentDropChances[var2] = 0.085F;
        }
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
    }

    public EntityLookHelper getLookHelper()
    {
        return this.lookHelper;
    }

    public EntityMoveHelper getMoveHelper()
    {
        return this.moveHelper;
    }

    public EntityJumpHelper getJumpHelper()
    {
        return this.jumpHelper;
    }

    public PathNavigate getNavigator()
    {
        return this.navigator;
    }

    /**
     * returns the EntitySenses Object for the EntityLiving
     */
    public EntitySenses getEntitySenses()
    {
        return this.senses;
    }

    /**
     * Gets the active target the Task system uses for tracking
     */
    public EntityLivingBase getAttackTarget()
    {
        return this.attackTarget;
    }

    /**
     * Sets the active target the Task system uses for tracking
     */
    public void setAttackTarget(EntityLivingBase p_70624_1_)
    {
        this.attackTarget = p_70624_1_;
    }

    /**
     * Returns true if this entity can attack entities of the specified class.
     */
    public boolean canAttackClass(Class p_70686_1_)
    {
        return EntityCreeper.class != p_70686_1_ && EntityGhast.class != p_70686_1_;
    }

    /**
     * This function applies the benefits of growing back wool and faster growing up to the acting entity. (This
     * function is used in the AIEatGrass)
     */
    public void eatGrassBonus() {}

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(11, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(10, "");
    }

    /**
     * Get number of ticks, at least during which the living entity will be silent.
     */
    public int getTalkInterval()
    {
        return 80;
    }

    /**
     * Plays living's sound at its position
     */
    public void playLivingSound()
    {
        String var1 = this.getLivingSound();

        if (var1 != null)
        {
            this.playSound(var1, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    /**
     * Gets called every tick from main Entity class
     */
    public void onEntityUpdate()
    {
        super.onEntityUpdate();
        this.worldObj.theProfiler.startSection("mobBaseTick");

        if (this.isEntityAlive() && this.rand.nextInt(1000) < this.livingSoundTime++)
        {
            this.livingSoundTime = -this.getTalkInterval();
            this.playLivingSound();
        }

        this.worldObj.theProfiler.endSection();
    }

    /**
     * Get the experience points the entity currently has.
     */
    protected int getExperiencePoints(EntityPlayer p_70693_1_)
    {
        if (this.experienceValue > 0)
        {
            int var2 = this.experienceValue;
            ItemStack[] var3 = this.getLastActiveItems();

            for (int var4 = 0; var4 < var3.length; ++var4)
            {
                if (var3[var4] != null && this.equipmentDropChances[var4] <= 1.0F)
                {
                    var2 += 1 + this.rand.nextInt(3);
                }
            }

            return var2;
        }
        else
        {
            return this.experienceValue;
        }
    }

    /**
     * Spawns an explosion particle around the Entity's location
     */
    public void spawnExplosionParticle()
    {
        for (int var1 = 0; var1 < 20; ++var1)
        {
            double var2 = this.rand.nextGaussian() * 0.02D;
            double var4 = this.rand.nextGaussian() * 0.02D;
            double var6 = this.rand.nextGaussian() * 0.02D;
            double var8 = 10.0D;
            this.worldObj.spawnParticle("explode", this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width - var2 * var8, this.posY + (double)(this.rand.nextFloat() * this.height) - var4 * var8, this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width - var6 * var8, var2, var4, var6);
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (!this.worldObj.isClient)
        {
            this.updateLeashedState();
        }
    }

    protected float func_110146_f(float p_110146_1_, float p_110146_2_)
    {
        if (this.isAIEnabled())
        {
            this.bodyHelper.func_75664_a();
            return p_110146_2_;
        }
        else
        {
            return super.func_110146_f(p_110146_1_, p_110146_2_);
        }
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return null;
    }

    protected Item func_146068_u()
    {
        return Item.getItemById(0);
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
    {
        Item var3 = this.func_146068_u();

        if (var3 != null)
        {
            int var4 = this.rand.nextInt(3);

            if (p_70628_2_ > 0)
            {
                var4 += this.rand.nextInt(p_70628_2_ + 1);
            }

            for (int var5 = 0; var5 < var4; ++var5)
            {
                this.func_145779_a(var3, 1);
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound p_70014_1_)
    {
        super.writeEntityToNBT(p_70014_1_);
        p_70014_1_.setBoolean("CanPickUpLoot", this.canPickUpLoot());
        p_70014_1_.setBoolean("PersistenceRequired", this.persistenceRequired);
        NBTTagList var2 = new NBTTagList();
        NBTTagCompound var4;

        for (int var3 = 0; var3 < this.equipment.length; ++var3)
        {
            var4 = new NBTTagCompound();

            if (this.equipment[var3] != null)
            {
                this.equipment[var3].writeToNBT(var4);
            }

            var2.appendTag(var4);
        }

        p_70014_1_.setTag("Equipment", var2);
        NBTTagList var6 = new NBTTagList();

        for (int var7 = 0; var7 < this.equipmentDropChances.length; ++var7)
        {
            var6.appendTag(new NBTTagFloat(this.equipmentDropChances[var7]));
        }

        p_70014_1_.setTag("DropChances", var6);
        p_70014_1_.setString("CustomName", this.getCustomNameTag());
        p_70014_1_.setBoolean("CustomNameVisible", this.getAlwaysRenderNameTag());
        p_70014_1_.setBoolean("Leashed", this.isLeashed);

        if (this.leashedToEntity != null)
        {
            var4 = new NBTTagCompound();

            if (this.leashedToEntity instanceof EntityLivingBase)
            {
                var4.setLong("UUIDMost", this.leashedToEntity.getUniqueID().getMostSignificantBits());
                var4.setLong("UUIDLeast", this.leashedToEntity.getUniqueID().getLeastSignificantBits());
            }
            else if (this.leashedToEntity instanceof EntityHanging)
            {
                EntityHanging var5 = (EntityHanging)this.leashedToEntity;
                var4.setInteger("X", var5.field_146063_b);
                var4.setInteger("Y", var5.field_146064_c);
                var4.setInteger("Z", var5.field_146062_d);
            }

            p_70014_1_.setTag("Leash", var4);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound p_70037_1_)
    {
        super.readEntityFromNBT(p_70037_1_);
        this.setCanPickUpLoot(p_70037_1_.getBoolean("CanPickUpLoot"));
        this.persistenceRequired = p_70037_1_.getBoolean("PersistenceRequired");

        if (p_70037_1_.func_150297_b("CustomName", 8) && p_70037_1_.getString("CustomName").length() > 0)
        {
            this.setCustomNameTag(p_70037_1_.getString("CustomName"));
        }

        this.setAlwaysRenderNameTag(p_70037_1_.getBoolean("CustomNameVisible"));
        NBTTagList var2;
        int var3;

        if (p_70037_1_.func_150297_b("Equipment", 9))
        {
            var2 = p_70037_1_.getTagList("Equipment", 10);

            for (var3 = 0; var3 < this.equipment.length; ++var3)
            {
                this.equipment[var3] = ItemStack.loadItemStackFromNBT(var2.getCompoundTagAt(var3));
            }
        }

        if (p_70037_1_.func_150297_b("DropChances", 9))
        {
            var2 = p_70037_1_.getTagList("DropChances", 5);

            for (var3 = 0; var3 < var2.tagCount(); ++var3)
            {
                this.equipmentDropChances[var3] = var2.func_150308_e(var3);
            }
        }

        this.isLeashed = p_70037_1_.getBoolean("Leashed");

        if (this.isLeashed && p_70037_1_.func_150297_b("Leash", 10))
        {
            this.field_110170_bx = p_70037_1_.getCompoundTag("Leash");
        }
    }

    public void setMoveForward(float p_70657_1_)
    {
        this.moveForward = p_70657_1_;
    }

    /**
     * set the movespeed used for the new AI system
     */
    public void setAIMoveSpeed(float p_70659_1_)
    {
        super.setAIMoveSpeed(p_70659_1_);
        this.setMoveForward(p_70659_1_);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        this.worldObj.theProfiler.startSection("looting");

        if (!this.worldObj.isClient && this.canPickUpLoot() && !this.dead && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
        {
            List var1 = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(1.0D, 0.0D, 1.0D));
            Iterator var2 = var1.iterator();

            while (var2.hasNext())
            {
                EntityItem var3 = (EntityItem)var2.next();

                if (!var3.isDead && var3.getEntityItem() != null)
                {
                    ItemStack var4 = var3.getEntityItem();
                    int var5 = getArmorPosition(var4);

                    if (var5 > -1)
                    {
                        boolean var6 = true;
                        ItemStack var7 = this.getEquipmentInSlot(var5);

                        if (var7 != null)
                        {
                            if (var5 == 0)
                            {
                                if (var4.getItem() instanceof ItemSword && !(var7.getItem() instanceof ItemSword))
                                {
                                    var6 = true;
                                }
                                else if (var4.getItem() instanceof ItemSword && var7.getItem() instanceof ItemSword)
                                {
                                    ItemSword var8 = (ItemSword)var4.getItem();
                                    ItemSword var9 = (ItemSword)var7.getItem();

                                    if (var8.func_150931_i() == var9.func_150931_i())
                                    {
                                        var6 = var4.getItemDamage() > var7.getItemDamage() || var4.hasTagCompound() && !var7.hasTagCompound();
                                    }
                                    else
                                    {
                                        var6 = var8.func_150931_i() > var9.func_150931_i();
                                    }
                                }
                                else
                                {
                                    var6 = false;
                                }
                            }
                            else if (var4.getItem() instanceof ItemArmor && !(var7.getItem() instanceof ItemArmor))
                            {
                                var6 = true;
                            }
                            else if (var4.getItem() instanceof ItemArmor && var7.getItem() instanceof ItemArmor)
                            {
                                ItemArmor var10 = (ItemArmor)var4.getItem();
                                ItemArmor var12 = (ItemArmor)var7.getItem();

                                if (var10.damageReduceAmount == var12.damageReduceAmount)
                                {
                                    var6 = var4.getItemDamage() > var7.getItemDamage() || var4.hasTagCompound() && !var7.hasTagCompound();
                                }
                                else
                                {
                                    var6 = var10.damageReduceAmount > var12.damageReduceAmount;
                                }
                            }
                            else
                            {
                                var6 = false;
                            }
                        }

                        if (var6)
                        {
                            if (var7 != null && this.rand.nextFloat() - 0.1F < this.equipmentDropChances[var5])
                            {
                                this.entityDropItem(var7, 0.0F);
                            }

                            if (var4.getItem() == Items.diamond && var3.func_145800_j() != null)
                            {
                                EntityPlayer var11 = this.worldObj.getPlayerEntityByName(var3.func_145800_j());

                                if (var11 != null)
                                {
                                    var11.triggerAchievement(AchievementList.field_150966_x);
                                }
                            }

                            this.setCurrentItemOrArmor(var5, var4);
                            this.equipmentDropChances[var5] = 2.0F;
                            this.persistenceRequired = true;
                            this.onItemPickup(var3, 1);
                            var3.setDead();
                        }
                    }
                }
            }
        }

        this.worldObj.theProfiler.endSection();
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    protected boolean isAIEnabled()
    {
        return false;
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    protected boolean canDespawn()
    {
        return true;
    }

    /**
     * Makes the entity despawn if requirements are reached
     */
    protected void despawnEntity()
    {
        if (this.persistenceRequired)
        {
            this.entityAge = 0;
        }
        else
        {
            EntityPlayer var1 = this.worldObj.getClosestPlayerToEntity(this, -1.0D);

            if (var1 != null)
            {
                double var2 = var1.posX - this.posX;
                double var4 = var1.posY - this.posY;
                double var6 = var1.posZ - this.posZ;
                double var8 = var2 * var2 + var4 * var4 + var6 * var6;

                if (this.canDespawn() && var8 > 16384.0D)
                {
                    this.setDead();
                }

                if (this.entityAge > 600 && this.rand.nextInt(800) == 0 && var8 > 1024.0D && this.canDespawn())
                {
                    this.setDead();
                }
                else if (var8 < 1024.0D)
                {
                    this.entityAge = 0;
                }
            }
        }
    }

    protected void updateAITasks()
    {
        ++this.entityAge;
        this.worldObj.theProfiler.startSection("checkDespawn");
        this.despawnEntity();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("sensing");
        this.senses.clearSensingCache();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("targetSelector");
        this.targetTasks.onUpdateTasks();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("goalSelector");
        this.tasks.onUpdateTasks();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("navigation");
        this.navigator.onUpdateNavigation();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("mob tick");
        this.updateAITick();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("controls");
        this.worldObj.theProfiler.startSection("move");
        this.moveHelper.onUpdateMoveHelper();
        this.worldObj.theProfiler.endStartSection("look");
        this.lookHelper.onUpdateLook();
        this.worldObj.theProfiler.endStartSection("jump");
        this.jumpHelper.doJump();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.endSection();
    }

    protected void updateEntityActionState()
    {
        super.updateEntityActionState();
        this.moveStrafing = 0.0F;
        this.moveForward = 0.0F;
        this.despawnEntity();
        float var1 = 8.0F;

        if (this.rand.nextFloat() < 0.02F)
        {
            EntityPlayer var2 = this.worldObj.getClosestPlayerToEntity(this, (double)var1);

            if (var2 != null)
            {
                this.currentTarget = var2;
                this.numTicksToChaseTarget = 10 + this.rand.nextInt(20);
            }
            else
            {
                this.randomYawVelocity = (this.rand.nextFloat() - 0.5F) * 20.0F;
            }
        }

        if (this.currentTarget != null)
        {
            this.faceEntity(this.currentTarget, 10.0F, (float)this.getVerticalFaceSpeed());

            if (this.numTicksToChaseTarget-- <= 0 || this.currentTarget.isDead || this.currentTarget.getDistanceSqToEntity(this) > (double)(var1 * var1))
            {
                this.currentTarget = null;
            }
        }
        else
        {
            if (this.rand.nextFloat() < 0.05F)
            {
                this.randomYawVelocity = (this.rand.nextFloat() - 0.5F) * 20.0F;
            }

            this.rotationYaw += this.randomYawVelocity;
            this.rotationPitch = this.defaultPitch;
        }

        boolean var4 = this.isInWater();
        boolean var3 = this.handleLavaMovement();

        if (var4 || var3)
        {
            this.isJumping = this.rand.nextFloat() < 0.8F;
        }
    }

    /**
     * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
     * use in wolves.
     */
    public int getVerticalFaceSpeed()
    {
        return 40;
    }

    /**
     * Changes pitch and yaw so that the entity calling the function is facing the entity provided as an argument.
     */
    public void faceEntity(Entity p_70625_1_, float p_70625_2_, float p_70625_3_)
    {
        double var4 = p_70625_1_.posX - this.posX;
        double var8 = p_70625_1_.posZ - this.posZ;
        double var6;

        if (p_70625_1_ instanceof EntityLivingBase)
        {
            EntityLivingBase var10 = (EntityLivingBase)p_70625_1_;
            var6 = var10.posY + (double)var10.getEyeHeight() - (this.posY + (double)this.getEyeHeight());
        }
        else
        {
            var6 = (p_70625_1_.boundingBox.minY + p_70625_1_.boundingBox.maxY) / 2.0D - (this.posY + (double)this.getEyeHeight());
        }

        double var14 = (double)MathHelper.sqrt_double(var4 * var4 + var8 * var8);
        float var12 = (float)(Math.atan2(var8, var4) * 180.0D / Math.PI) - 90.0F;
        float var13 = (float)(-(Math.atan2(var6, var14) * 180.0D / Math.PI));
        this.rotationPitch = this.updateRotation(this.rotationPitch, var13, p_70625_3_);
        this.rotationYaw = this.updateRotation(this.rotationYaw, var12, p_70625_2_);
    }

    /**
     * Arguments: current rotation, intended rotation, max increment.
     */
    private float updateRotation(float p_70663_1_, float p_70663_2_, float p_70663_3_)
    {
        float var4 = MathHelper.wrapAngleTo180_float(p_70663_2_ - p_70663_1_);

        if (var4 > p_70663_3_)
        {
            var4 = p_70663_3_;
        }

        if (var4 < -p_70663_3_)
        {
            var4 = -p_70663_3_;
        }

        return p_70663_1_ + var4;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        return this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox);
    }

    /**
     * Returns render size modifier
     */
    public float getRenderSizeModifier()
    {
        return 1.0F;
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    public int getMaxSpawnedInChunk()
    {
        return 4;
    }

    /**
     * The number of iterations PathFinder.getSafePoint will execute before giving up.
     */
    public int getMaxSafePointTries()
    {
        if (this.getAttackTarget() == null)
        {
            return 3;
        }
        else
        {
            int var1 = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
            var1 -= (3 - this.worldObj.difficultySetting.getDifficultyId()) * 4;

            if (var1 < 0)
            {
                var1 = 0;
            }

            return var1 + 3;
        }
    }

    /**
     * Returns the item that this EntityLiving is holding, if any.
     */
    public ItemStack getHeldItem()
    {
        return this.equipment[0];
    }

    /**
     * 0: Tool in Hand; 1-4: Armor
     */
    public ItemStack getEquipmentInSlot(int p_71124_1_)
    {
        return this.equipment[p_71124_1_];
    }

    public ItemStack func_130225_q(int p_130225_1_)
    {
        return this.equipment[p_130225_1_ + 1];
    }

    /**
     * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is armor. Params: Item, slot
     */
    public void setCurrentItemOrArmor(int p_70062_1_, ItemStack p_70062_2_)
    {
        this.equipment[p_70062_1_] = p_70062_2_;
    }

    public ItemStack[] getLastActiveItems()
    {
        return this.equipment;
    }

    /**
     * Drop the equipment for this entity.
     */
    protected void dropEquipment(boolean p_82160_1_, int p_82160_2_)
    {
        for (int var3 = 0; var3 < this.getLastActiveItems().length; ++var3)
        {
            ItemStack var4 = this.getEquipmentInSlot(var3);
            boolean var5 = this.equipmentDropChances[var3] > 1.0F;

            if (var4 != null && (p_82160_1_ || var5) && this.rand.nextFloat() - (float)p_82160_2_ * 0.01F < this.equipmentDropChances[var3])
            {
                if (!var5 && var4.isItemStackDamageable())
                {
                    int var6 = Math.max(var4.getMaxDamage() - 25, 1);
                    int var7 = var4.getMaxDamage() - this.rand.nextInt(this.rand.nextInt(var6) + 1);

                    if (var7 > var6)
                    {
                        var7 = var6;
                    }

                    if (var7 < 1)
                    {
                        var7 = 1;
                    }

                    var4.setItemDamage(var7);
                }

                this.entityDropItem(var4, 0.0F);
            }
        }
    }

    /**
     * Makes entity wear random armor based on difficulty
     */
    protected void addRandomArmor()
    {
        if (this.rand.nextFloat() < 0.15F * this.worldObj.func_147462_b(this.posX, this.posY, this.posZ))
        {
            int var1 = this.rand.nextInt(2);
            float var2 = this.worldObj.difficultySetting == EnumDifficulty.HARD ? 0.1F : 0.25F;

            if (this.rand.nextFloat() < 0.095F)
            {
                ++var1;
            }

            if (this.rand.nextFloat() < 0.095F)
            {
                ++var1;
            }

            if (this.rand.nextFloat() < 0.095F)
            {
                ++var1;
            }

            for (int var3 = 3; var3 >= 0; --var3)
            {
                ItemStack var4 = this.func_130225_q(var3);

                if (var3 < 3 && this.rand.nextFloat() < var2)
                {
                    break;
                }

                if (var4 == null)
                {
                    Item var5 = getArmorItemForSlot(var3 + 1, var1);

                    if (var5 != null)
                    {
                        this.setCurrentItemOrArmor(var3 + 1, new ItemStack(var5));
                    }
                }
            }
        }
    }

    public static int getArmorPosition(ItemStack p_82159_0_)
    {
        if (p_82159_0_.getItem() != Item.getItemFromBlock(Blocks.pumpkin) && p_82159_0_.getItem() != Items.skull)
        {
            if (p_82159_0_.getItem() instanceof ItemArmor)
            {
                switch (((ItemArmor)p_82159_0_.getItem()).armorType)
                {
                    case 0:
                        return 4;

                    case 1:
                        return 3;

                    case 2:
                        return 2;

                    case 3:
                        return 1;
                }
            }

            return 0;
        }
        else
        {
            return 4;
        }
    }

    /**
     * Params: Armor slot, Item tier
     */
    public static Item getArmorItemForSlot(int p_82161_0_, int p_82161_1_)
    {
        switch (p_82161_0_)
        {
            case 4:
                if (p_82161_1_ == 0)
                {
                    return Items.leather_helmet;
                }
                else if (p_82161_1_ == 1)
                {
                    return Items.golden_helmet;
                }
                else if (p_82161_1_ == 2)
                {
                    return Items.chainmail_helmet;
                }
                else if (p_82161_1_ == 3)
                {
                    return Items.iron_helmet;
                }
                else if (p_82161_1_ == 4)
                {
                    return Items.diamond_helmet;
                }

            case 3:
                if (p_82161_1_ == 0)
                {
                    return Items.leather_chestplate;
                }
                else if (p_82161_1_ == 1)
                {
                    return Items.golden_chestplate;
                }
                else if (p_82161_1_ == 2)
                {
                    return Items.chainmail_chestplate;
                }
                else if (p_82161_1_ == 3)
                {
                    return Items.iron_chestplate;
                }
                else if (p_82161_1_ == 4)
                {
                    return Items.diamond_chestplate;
                }

            case 2:
                if (p_82161_1_ == 0)
                {
                    return Items.leather_leggings;
                }
                else if (p_82161_1_ == 1)
                {
                    return Items.golden_leggings;
                }
                else if (p_82161_1_ == 2)
                {
                    return Items.chainmail_leggings;
                }
                else if (p_82161_1_ == 3)
                {
                    return Items.iron_leggings;
                }
                else if (p_82161_1_ == 4)
                {
                    return Items.diamond_leggings;
                }

            case 1:
                if (p_82161_1_ == 0)
                {
                    return Items.leather_boots;
                }
                else if (p_82161_1_ == 1)
                {
                    return Items.golden_boots;
                }
                else if (p_82161_1_ == 2)
                {
                    return Items.chainmail_boots;
                }
                else if (p_82161_1_ == 3)
                {
                    return Items.iron_boots;
                }
                else if (p_82161_1_ == 4)
                {
                    return Items.diamond_boots;
                }

            default:
                return null;
        }
    }

    /**
     * Enchants the entity's armor and held item based on difficulty
     */
    protected void enchantEquipment()
    {
        float var1 = this.worldObj.func_147462_b(this.posX, this.posY, this.posZ);

        if (this.getHeldItem() != null && this.rand.nextFloat() < 0.25F * var1)
        {
            EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItem(), (int)(5.0F + var1 * (float)this.rand.nextInt(18)));
        }

        for (int var2 = 0; var2 < 4; ++var2)
        {
            ItemStack var3 = this.func_130225_q(var2);

            if (var3 != null && this.rand.nextFloat() < 0.5F * var1)
            {
                EnchantmentHelper.addRandomEnchantment(this.rand, var3, (int)(5.0F + var1 * (float)this.rand.nextInt(18)));
            }
        }
    }

    public IEntityLivingData onSpawnWithEgg(IEntityLivingData p_110161_1_)
    {
        this.getEntityAttribute(SharedMonsterAttributes.followRange).applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05D, 1));
        return p_110161_1_;
    }

    /**
     * returns true if all the conditions for steering the entity are met. For pigs, this is true if it is being ridden
     * by a player and the player is holding a carrot-on-a-stick
     */
    public boolean canBeSteered()
    {
        return false;
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getCommandSenderName()
    {
        return this.hasCustomNameTag() ? this.getCustomNameTag() : super.getCommandSenderName();
    }

    public void func_110163_bv()
    {
        this.persistenceRequired = true;
    }

    public void setCustomNameTag(String p_94058_1_)
    {
        this.dataWatcher.updateObject(10, p_94058_1_);
    }

    public String getCustomNameTag()
    {
        return this.dataWatcher.getWatchableObjectString(10);
    }

    public boolean hasCustomNameTag()
    {
        return this.dataWatcher.getWatchableObjectString(10).length() > 0;
    }

    public void setAlwaysRenderNameTag(boolean p_94061_1_)
    {
        this.dataWatcher.updateObject(11, Byte.valueOf((byte)(p_94061_1_ ? 1 : 0)));
    }

    public boolean getAlwaysRenderNameTag()
    {
        return this.dataWatcher.getWatchableObjectByte(11) == 1;
    }

    public boolean getAlwaysRenderNameTagForRender()
    {
        return this.getAlwaysRenderNameTag();
    }

    public void setEquipmentDropChance(int p_96120_1_, float p_96120_2_)
    {
        this.equipmentDropChances[p_96120_1_] = p_96120_2_;
    }

    public boolean canPickUpLoot()
    {
        return this.canPickUpLoot;
    }

    public void setCanPickUpLoot(boolean p_98053_1_)
    {
        this.canPickUpLoot = p_98053_1_;
    }

    public boolean isNoDespawnRequired()
    {
        return this.persistenceRequired;
    }

    /**
     * First layer of player interaction
     */
    public final boolean interactFirst(EntityPlayer p_130002_1_)
    {
        if (this.getLeashed() && this.getLeashedToEntity() == p_130002_1_)
        {
            this.clearLeashed(true, !p_130002_1_.capabilities.isCreativeMode);
            return true;
        }
        else
        {
            ItemStack var2 = p_130002_1_.inventory.getCurrentItem();

            if (var2 != null && var2.getItem() == Items.lead && this.allowLeashing())
            {
                if (!(this instanceof EntityTameable) || !((EntityTameable)this).isTamed())
                {
                    this.setLeashedToEntity(p_130002_1_, true);
                    --var2.stackSize;
                    return true;
                }

                if (((EntityTameable)this).func_152114_e(p_130002_1_))
                {
                    this.setLeashedToEntity(p_130002_1_, true);
                    --var2.stackSize;
                    return true;
                }
            }

            return this.interact(p_130002_1_) ? true : super.interactFirst(p_130002_1_);
        }
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    protected boolean interact(EntityPlayer p_70085_1_)
    {
        return false;
    }

    /**
     * Applies logic related to leashes, for example dragging the entity or breaking the leash.
     */
    protected void updateLeashedState()
    {
        if (this.field_110170_bx != null)
        {
            this.recreateLeash();
        }

        if (this.isLeashed)
        {
            if (this.leashedToEntity == null || this.leashedToEntity.isDead)
            {
                this.clearLeashed(true, true);
            }
        }
    }

    /**
     * Removes the leash from this entity. Second parameter tells whether to send a packet to surrounding players.
     */
    public void clearLeashed(boolean p_110160_1_, boolean p_110160_2_)
    {
        if (this.isLeashed)
        {
            this.isLeashed = false;
            this.leashedToEntity = null;

            if (!this.worldObj.isClient && p_110160_2_)
            {
                this.func_145779_a(Items.lead, 1);
            }

            if (!this.worldObj.isClient && p_110160_1_ && this.worldObj instanceof WorldServer)
            {
                ((WorldServer)this.worldObj).getEntityTracker().func_151247_a(this, new S1BPacketEntityAttach(1, this, (Entity)null));
            }
        }
    }

    public boolean allowLeashing()
    {
        return !this.getLeashed() && !(this instanceof IMob);
    }

    public boolean getLeashed()
    {
        return this.isLeashed;
    }

    public Entity getLeashedToEntity()
    {
        return this.leashedToEntity;
    }

    /**
     * Sets the entity to be leashed to.\nArgs:\n@param par1Entity: The entity to be tethered to.\n@param par2: Whether
     * to send an attaching notification packet to surrounding players.
     */
    public void setLeashedToEntity(Entity p_110162_1_, boolean p_110162_2_)
    {
        this.isLeashed = true;
        this.leashedToEntity = p_110162_1_;

        if (!this.worldObj.isClient && p_110162_2_ && this.worldObj instanceof WorldServer)
        {
            ((WorldServer)this.worldObj).getEntityTracker().func_151247_a(this, new S1BPacketEntityAttach(1, this, this.leashedToEntity));
        }
    }

    private void recreateLeash()
    {
        if (this.isLeashed && this.field_110170_bx != null)
        {
            if (this.field_110170_bx.func_150297_b("UUIDMost", 4) && this.field_110170_bx.func_150297_b("UUIDLeast", 4))
            {
                UUID var5 = new UUID(this.field_110170_bx.getLong("UUIDMost"), this.field_110170_bx.getLong("UUIDLeast"));
                List var6 = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(10.0D, 10.0D, 10.0D));
                Iterator var7 = var6.iterator();

                while (var7.hasNext())
                {
                    EntityLivingBase var8 = (EntityLivingBase)var7.next();

                    if (var8.getUniqueID().equals(var5))
                    {
                        this.leashedToEntity = var8;
                        break;
                    }
                }
            }
            else if (this.field_110170_bx.func_150297_b("X", 99) && this.field_110170_bx.func_150297_b("Y", 99) && this.field_110170_bx.func_150297_b("Z", 99))
            {
                int var1 = this.field_110170_bx.getInteger("X");
                int var2 = this.field_110170_bx.getInteger("Y");
                int var3 = this.field_110170_bx.getInteger("Z");
                EntityLeashKnot var4 = EntityLeashKnot.getKnotForBlock(this.worldObj, var1, var2, var3);

                if (var4 == null)
                {
                    var4 = EntityLeashKnot.func_110129_a(this.worldObj, var1, var2, var3);
                }

                this.leashedToEntity = var4;
            }
            else
            {
                this.clearLeashed(false, true);
            }
        }

        this.field_110170_bx = null;
    }
}
