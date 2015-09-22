package net.minecraft.server;

import java.io.IOException; // CraftBukkit

public class PacketPlayInChat extends Packet {

    private String message;

    public PacketPlayInChat() {}

    public PacketPlayInChat(String s) {
        if (s.length() > 100) {
            s = s.substring(0, 100);
        }

        this.message = s;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException { // CraftBukkit - added throws
        this.message = org.apache.commons.lang.StringUtils.normalizeSpace( packetdataserializer.c( 100 ) ); // Spigot
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException { // CraftBukkit - added throws
        packetdataserializer.a(this.message);
    }

    public void a(PacketPlayInListener packetplayinlistener) {
        packetplayinlistener.a(this);
    }

    public String b() {
        return String.format("message=\'%s\'", new Object[] { this.message});
    }

    public String c() {
        return this.message;
    }

    // CraftBukkit start - make chat async
    @Override
    public boolean a() {
        return !this.message.startsWith("/");
    }
    // CraftBukkit end

    // Spigot Start
    private static final java.util.concurrent.ExecutorService executors = java.util.concurrent.Executors.newCachedThreadPool(
            new com.google.common.util.concurrent.ThreadFactoryBuilder().setDaemon( true ).setNameFormat( "Async Chat Thread - #%d" ).build() );
    public void handle(final PacketListener packetlistener)
    {
        if ( a() )
        {
            executors.submit( new Runnable()
            {

                @Override
                public void run()
                {
                    PacketPlayInChat.this.a( (PacketPlayInListener) packetlistener );
                }
            } );
            return;
        }
        // Spigot End
        this.a((PacketPlayInListener) packetlistener);
    }
}
