package net.minecraft.server;

import java.util.Iterator;

public class WorldMapHumanTracker {

    public final EntityHuman trackee;
    public int[] b;
    public int[] c;
    private int f;
    private int g;
    private byte[] h;
    public int d;
    private boolean i;
    final WorldMap worldMap;

    public WorldMapHumanTracker(WorldMap worldmap, EntityHuman entityhuman) {
        this.worldMap = worldmap;
        this.b = new int[128];
        this.c = new int[128];
        this.trackee = entityhuman;

        for (int i = 0; i < this.b.length; ++i) {
            this.b[i] = 0;
            this.c[i] = 127;
        }
    }

    public byte[] a(ItemStack itemstack) {
        byte[] abyte;

        if (!this.i) {
            abyte = new byte[] { (byte) 2, this.worldMap.scale};
            this.i = true;
            return abyte;
        } else {
            int i;
            int j;

            // Spigot start
            boolean custom = this.worldMap.mapView.renderers.size() > 1 || !(this.worldMap.mapView.renderers.get(0) instanceof org.bukkit.craftbukkit.map.CraftMapRenderer);
            org.bukkit.craftbukkit.map.RenderData render = (custom) ? this.worldMap.mapView.render((org.bukkit.craftbukkit.entity.CraftPlayer) trackee.getBukkitEntity()) : null; // CraftBukkit

            if (--this.g < 0) {
                this.g = 4;
                abyte = new byte[((custom) ? render.cursors.size() : this.worldMap.g.size()) * 3 + 1]; // CraftBukkit
                abyte[0] = 1;
                i = 0;

                // CraftBukkit start

                // Spigot start
                for (Iterator iterator = ((custom) ? render.cursors.iterator() : this.worldMap.g.values().iterator()); iterator.hasNext(); ++i) {
                    org.bukkit.map.MapCursor cursor = (custom) ? (org.bukkit.map.MapCursor) iterator.next() : null;
                    if (cursor != null && !cursor.isVisible()) continue;
                    WorldMapDecoration deco = (custom) ? null : (WorldMapDecoration) iterator.next();

                    abyte[i * 3 + 1] = (byte) (((custom) ? cursor.getRawType() : deco.type) << 4 | ((custom) ? cursor.getDirection() : deco.rotation) & 15);
                    abyte[i * 3 + 2] = (byte) ((custom) ? cursor.getX() : deco.locX);
                    abyte[i * 3 + 3] = (byte) ((custom) ? cursor.getY() : deco.locY);
                }
                // Spigot end
                // CraftBukkit end

                boolean flag = !itemstack.A();

                if (this.h != null && this.h.length == abyte.length) {
                    for (j = 0; j < abyte.length; ++j) {
                        if (abyte[j] != this.h[j]) {
                            flag = false;
                            break;
                        }
                    }
                } else {
                    flag = false;
                }

                if (!flag) {
                    this.h = abyte;
                    return abyte;
                }
            }

            for (int k = 0; k < 1; ++k) {
                i = this.f++ * 11 % 128;
                if (this.b[i] >= 0) {
                    int l = this.c[i] - this.b[i] + 1;

                    j = this.b[i];
                    byte[] abyte1 = new byte[l + 3];

                    abyte1[0] = 0;
                    abyte1[1] = (byte) i;
                    abyte1[2] = (byte) j;

                    for (int i1 = 0; i1 < abyte1.length - 3; ++i1) {
                        abyte1[i1 + 3] = ((custom) ? render.buffer : this.worldMap.colors)[(i1 + j) * 128 + i];
                    }

                    this.c[i] = -1;
                    this.b[i] = -1;
                    return abyte1;
                }
            }

            return null;
        }
    }
}
