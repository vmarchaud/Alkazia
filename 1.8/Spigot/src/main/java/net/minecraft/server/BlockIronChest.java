package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

public class BlockIronChest extends BlockChest
{
    public static final BlockStateDirection FACING = BlockStateDirection.of("facing", EnumDirectionLimit.HORIZONTAL);
    private final Random rand = new Random();
    public final int b = 0;
    // Alkazia - iron chest

    public BlockIronChest()
    {
        super(0);
        this.a(CreativeModeTab.c);
        this.a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    }

    public boolean c()
    {
        return false;
    }

    public boolean d()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int b()
    {
        return 2;
    }

    public void updateShape(IBlockAccess access, BlockPosition pos)
    {
        if (access.getType(pos.north()).getBlock() == this)
        {
            this.a(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
        }
        else if (access.getType(pos.south()).getBlock() == this)
        {
            this.a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
        }
        else if (access.getType(pos.west()).getBlock() == this)
        {
            this.a(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        }
        else if (access.getType(pos.east()).getBlock() == this)
        {
            this.a(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
        }
        else
        {
            this.a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        }
    }


    public IBlockData getPlacedState(World worldIn, BlockPosition pos, EnumDirection facing, float hitX, float hitY, float hitZ, int meta, EntityLiving placer)
    {
    	return getBlockData().set(FACING, placer.getDirection());
    }

    public void postPlace(World worldIn, BlockPosition pos, IBlockData state, EntityLiving placer, ItemStack stack)
    {
        EnumDirection var6 = EnumDirection.fromType2(MathHelper.floor((double)(placer.yaw * 4.0F / 360.0F) + 0.5D) & 3).opposite();
        state = state.set(FACING, var6);
        
        BlockPosition var7 = pos.north();
        BlockPosition var8 = pos.south();
        BlockPosition var9 = pos.west();
        BlockPosition var10 = pos.east();
        boolean var11 = this == worldIn.getType(var7).getBlock();
        boolean var12 = this == worldIn.getType(var8).getBlock();
        boolean var13 = this == worldIn.getType(var9).getBlock();
        boolean var14 = this == worldIn.getType(var10).getBlock();

        if (!var11 && !var12 && !var13 && !var14)
        {
            worldIn.setTypeAndData(pos, state, 3);
        }
        else if (var6.k() == EnumAxis.X && (var11 || var12))
        {
            if (var11)
            {
                worldIn.setTypeAndData(var7, state, 3);
            }
            else
            {
                worldIn.setTypeAndData(var8, state, 3);
            }

            worldIn.setTypeAndData(pos, state, 3);
        }
        else if (var6.k() == EnumAxis.Z && (var13 || var14))
        {
            if (var13)
            {
                worldIn.setTypeAndData(var9, state, 3);
            }
            else
            {
                worldIn.setTypeAndData(var10, state, 3);
            }

            worldIn.setTypeAndData(pos, state, 3);
        }

        if (stack.hasName())
        {
            TileEntity var15 = worldIn.getTileEntity(pos);

            if (var15 instanceof TileEntityIronChest)
            {
                ((TileEntityIronChest)var15).a(stack.getName());
            }
        }
    }


    public void doPhysics(World worldIn, BlockPosition pos, IBlockData state, Block neighborBlock)
    {
        super.doPhysics(worldIn, pos, state, neighborBlock);
        TileEntity var5 = worldIn.getTileEntity(pos);

        if (var5 instanceof TileEntityIronChest)
        {
            var5.E();
        }
    }
    public IBlockData f(World worldIn, BlockPosition p_176458_2_, IBlockData p_176458_3_)
    {
        EnumDirection var4 = null;
        Iterator var5 = EnumDirectionLimit.HORIZONTAL.iterator();

        while (var5.hasNext())
        {
            EnumDirection var6 = (EnumDirection)var5.next();
            IBlockData var7 = worldIn.getType(p_176458_2_.shift(var6));

            if (var7.getBlock() == this)
            {
                return p_176458_3_;
            }

            if (var7.getBlock().m())
            {
                if (var4 != null)
                {
                    var4 = null;
                    break;
                }

                var4 = var6;
            }
        }

        if (var4 != null)
        {
            return p_176458_3_.set(FACING, var4.opposite());
        }
        else
        {
            EnumDirection var8 = (EnumDirection)p_176458_3_.get(FACING);

            if (worldIn.getType(p_176458_2_.shift(var8)).getBlock().m())
            {
                var8 = var8.opposite();
            }

            if (worldIn.getType(p_176458_2_.shift(var8)).getBlock().m())
            {
                var8 = var8.e();
            }

            if (worldIn.getType(p_176458_2_.shift(var8)).getBlock().m())
            {
                var8 = var8.opposite();
            }

            return p_176458_3_.set(FACING, var8);
        }
    }
    
    public void remove(World worldIn, BlockPosition pos, IBlockData state)
    {
        TileEntity var4 = worldIn.getTileEntity(pos);

        if (var4 instanceof IInventory)
        {
        	InventoryUtils.dropInventory(worldIn, pos, (IInventory)var4);
            worldIn.updateAdjacentComparators(pos, (Block)this);
        }

        super.remove(worldIn, pos, state);
    }

    public boolean onBlockActivated(World worldIn, BlockPosition pos, IBlockData state, EntityPlayer playerIn, EnumDirection side, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isStatic)
        {
            return true;
        }
        else
        {
        	ITileInventory var9 = this.d(worldIn, pos);

            if (var9 != null)
            {
                playerIn.openContainer(var9);
            }

            return true;
        }
    }


    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity a(World worldIn, int meta)
    {
        return new TileEntityIronChest();
    }


    public int a(IBlockAccess worldIn, BlockPosition pos, IBlockData state, EnumDirection side)
    {
        if (!this.isPowerSource())
        {
            return 0;
        }
        else
        {
            int var5 = 0;
            TileEntity var6 = worldIn.getTileEntity(pos);

            if (var6 instanceof TileEntityIronChest)
            {
                var5 = ((TileEntityIronChest)var6).l;
            }

            return MathHelper.clamp(var5, 0, 15);
        }
    }

    public int b(IBlockAccess worldIn, BlockPosition pos, IBlockData state, EnumDirection side)
    {
        return side == EnumDirection.UP ? this.a(worldIn, pos, state, side) : 0;
    }

    private boolean m(World worldIn, BlockPosition p_176457_2_)
    {
        return this.n(worldIn, p_176457_2_) || this.o(worldIn, p_176457_2_);
    }

    private boolean n(World worldIn, BlockPosition p_176456_2_)
    {
        return worldIn.getType(p_176456_2_.up()).getBlock().isOccluding();
    }

    private boolean o(World worldIn, BlockPosition p_176453_2_)
    {
        Iterator var3 = worldIn.a(EntityOcelot.class, new AxisAlignedBB((double)p_176453_2_.getX(), (double)(p_176453_2_.getY() + 1), (double)p_176453_2_.getZ(), (double)(p_176453_2_.getX() + 1), (double)(p_176453_2_.getY() + 2), (double)(p_176453_2_.getZ() + 1))).iterator();
        EntityOcelot var5;

        do
        {
            if (!var3.hasNext())
            {
                return false;
            }

            Entity var4 = (Entity)var3.next();
            var5 = (EntityOcelot)var4;
        }
        while (!var5.isSitting());

        return true;
    }

    public boolean isComplexRedstone()
    {
        return true;
    }

    public int l(World worldIn, BlockPosition pos)
    {
        return Container.b(this.d(worldIn, pos));
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockData fromLegacyData(int meta)
    {
        EnumDirection var2 = EnumDirection.fromType1(meta);

        if (var2.k() == EnumAxis.Y)
        {
            var2 = EnumDirection.NORTH;
        }

        return this.getBlockData().set(FACING, var2);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int toLegacyData(IBlockData state)
    {
        return ((EnumDirection)state.get(FACING)).a();
    }

    public ITileInventory d(World paramWorld, BlockPosition paramBlockPosition)
    {
      TileEntity localTileEntity1 = paramWorld.getTileEntity(paramBlockPosition);
      if (!(localTileEntity1 instanceof TileEntityIronChest)) {
        return null;
      }

      Object localObject = (TileEntityIronChest)localTileEntity1;

      if (m(paramWorld, paramBlockPosition)) {
        return null;
      }

      return (ITileInventory) localObject;
    }

}
