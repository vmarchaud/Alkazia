package net.minecraft.server;

import com.google.gson.JsonParseException;

public class TileEntitySign extends TileEntity {

    public final IChatBaseComponent[] lines = new IChatBaseComponent[] { new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText("")};
    public int f = -1;
    public boolean isEditable = true;
    private EntityHuman h;
    private final CommandObjectiveExecutor i = new CommandObjectiveExecutor();

    public TileEntitySign() {}

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);

        for (int i = 0; i < 4; ++i) {
            String s = ChatSerializer.a(this.lines[i]);

            nbttagcompound.setString("Text" + (i + 1), s);
        }
        
        // CraftBukkit start
        if (Boolean.getBoolean("convertLegacySigns")) {
            nbttagcompound.setBoolean("Bukkit.isConverted", true);
        }
        // CraftBukkit end

        this.i.b(nbttagcompound);
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.isEditable = false;
        super.a(nbttagcompound);
        TileEntitySignCommandListener tileentitysigncommandlistener = new TileEntitySignCommandListener(this);

        // CraftBukkit start - Add an option to convert signs correctly
        // This is done with a flag instead of all the time because
        // we have no way to tell whether a sign is from 1.7.10 or 1.8
        
        boolean oldSign = Boolean.getBoolean("convertLegacySigns") && !nbttagcompound.getBoolean("Bukkit.isConverted");
        
        for (int i = 0; i < 4; ++i) {
            String s = nbttagcompound.getString("Text" + (i + 1));
            
            if (oldSign) {
                lines[i] = org.bukkit.craftbukkit.util.CraftChatMessage.fromString(s)[0];
                continue;
            }
            // CraftBukkit end

            try {
                IChatBaseComponent ichatbasecomponent = ChatSerializer.a(s);

                try {
                    this.lines[i] = ChatComponentUtils.filterForDisplay(tileentitysigncommandlistener, ichatbasecomponent, (Entity) null);
                    if (false) throw new CommandException(null, null); // CraftBukkit - fix decompile error
                } catch (CommandException commandexception) {
                    this.lines[i] = ichatbasecomponent;
                }
            } catch (JsonParseException jsonparseexception) {
                this.lines[i] = new ChatComponentText(s);
            }
        }

        this.i.a(nbttagcompound);
    }

    public Packet getUpdatePacket() {
        IChatBaseComponent[] aichatbasecomponent = new IChatBaseComponent[4];

        System.arraycopy(this.lines, 0, aichatbasecomponent, 0, 4);
        return new PacketPlayOutUpdateSign(this.world, this.position, aichatbasecomponent);
    }

    public boolean b() {
        return this.isEditable;
    }

    public void a(EntityHuman entityhuman) {
        this.h = entityhuman;
    }

    public EntityHuman c() {
        return this.h;
    }

    public boolean b(EntityHuman entityhuman) {
        TileEntitySignPlayerWrapper tileentitysignplayerwrapper = new TileEntitySignPlayerWrapper(this, entityhuman);

        for (int i = 0; i < this.lines.length; ++i) {
            ChatModifier chatmodifier = this.lines[i] == null ? null : this.lines[i].getChatModifier();

            if (chatmodifier != null && chatmodifier.h() != null) {
                ChatClickable chatclickable = chatmodifier.h();

                if (chatclickable.a() == EnumClickAction.RUN_COMMAND) {
                    // CraftBukkit start
                    // MinecraftServer.getServer().getCommandHandler().a(tileentitysignplayerwrapper, chatclickable.b());
                    CommandBlockListenerAbstract.executeCommand(entityhuman, (org.bukkit.entity.Player) entityhuman.getBukkitEntity(), chatclickable.b());
                    // CraftBukkit ebd
                }
            }
        }

        return true;
    }

    public CommandObjectiveExecutor d() {
        return this.i;
    }

    static CommandObjectiveExecutor a(TileEntitySign tileentitysign) {
        return tileentitysign.i;
    }
}
