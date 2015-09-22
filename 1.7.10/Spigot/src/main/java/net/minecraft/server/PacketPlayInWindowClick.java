package net.minecraft.server;

public class PacketPlayInWindowClick extends Packet {

	private int a;
	public int slot; // Spigot
	private int button;
	private short d;
	private ItemStack item;
	private int shift;

	public PacketPlayInWindowClick() {
	}

	public void a(PacketPlayInListener packetplayinlistener) {
		packetplayinlistener.a(this);
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readByte();
		slot = packetdataserializer.readShort();
		button = packetdataserializer.readByte();
		d = packetdataserializer.readShort();
		shift = packetdataserializer.readByte();
		item = packetdataserializer.c();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.writeByte(a);
		packetdataserializer.writeShort(slot);
		packetdataserializer.writeByte(button);
		packetdataserializer.writeShort(d);
		packetdataserializer.writeByte(shift);
		packetdataserializer.a(item);
	}

	@Override
	public String b() {
		return item != null ? String.format("id=%d, slot=%d, button=%d, type=%d, itemid=%d, itemcount=%d, itemaux=%d", new Object[] { Integer.valueOf(a), Integer.valueOf(slot), Integer.valueOf(button), Integer.valueOf(shift), Integer.valueOf(Item.getId(item.getItem())), Integer.valueOf(item.count), Integer.valueOf(item.getData()) }) : String.format("id=%d, slot=%d, button=%d, type=%d, itemid=-1",
				new Object[] { Integer.valueOf(a), Integer.valueOf(slot), Integer.valueOf(button), Integer.valueOf(shift) });
	}

	public int c() {
		return a;
	}

	public int d() {
		return slot;
	}

	public int e() {
		return button;
	}

	public short f() {
		return d;
	}

	public ItemStack g() {
		return item;
	}

	public int h() {
		return shift;
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayInListener) packetlistener);
	}
}
