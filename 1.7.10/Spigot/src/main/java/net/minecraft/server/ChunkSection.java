package net.minecraft.server;

import java.util.Arrays; // CraftBukkit

public class ChunkSection {

	private int yPos;
	private int nonEmptyBlockCount;
	private int tickingBlockCount;
	private byte[] blockIds;
	private NibbleArray extBlockIds;
	private NibbleArray blockData;
	private NibbleArray emittedLight;
	private NibbleArray skyLight;
	// CraftBukkit start - Compact storage
	private int compactId;
	private byte compactExtId;
	private byte compactData;
	private byte compactEmitted;
	private byte compactSky;

	// Pre-generated (read-only!) NibbleArrays for every possible value, used for chunk saving
	private static NibbleArray[] compactPregen = new NibbleArray[16];
	static {
		for (int i = 0; i < 16; i++) {
			compactPregen[i] = expandCompactNibble((byte) i);
		}
	}

	private static NibbleArray expandCompactNibble(byte value) {
		byte[] data = new byte[2048];
		Arrays.fill(data, (byte) (value | value << 4));
		return new NibbleArray(data, 4);
	}

	private boolean canBeCompact(byte[] array) {
		byte value = array[0];
		for (int i = 1; i < array.length; i++) {
			if (value != array[i])
				return false;
		}

		return true;
	}

	// CraftBukkit end

	public ChunkSection(int i, boolean flag) {
		yPos = i;
		/* CraftBukkit - Start as null, using compact storage
		this.blockIds = new byte[4096];
		this.blockData = new NibbleArray(this.blockIds.length, 4);
		this.emittedLight = new NibbleArray(this.blockIds.length, 4);
		if (flag) {
		    this.skyLight = new NibbleArray(this.blockIds.length, 4);
		}
		*/
		if (!flag) {
			compactSky = -1;
		}
		// CraftBukkit end
	}

	// CraftBukkit start
	public ChunkSection(int y, boolean flag, byte[] blkIds, byte[] extBlkIds) {
		yPos = y;
		setIdArray(blkIds);
		if (extBlkIds != null) {
			setExtendedIdArray(new NibbleArray(extBlkIds, 4));
		}
		if (!flag) {
			compactSky = -1;
		}
		recalcBlockCounts();
	}

	// CraftBukkit end

	public Block getTypeId(int i, int j, int k) {
		// CraftBukkit start - Compact storage
		if (blockIds == null) {
			int id = compactId;
			if (extBlockIds == null) {
				id |= compactExtId << 8;
			} else {
				id |= extBlockIds.a(i, j, k) << 8;
			}

			return Block.getById(id);
		}
		// CraftBukkit end

		int l = blockIds[j << 8 | k << 4 | i] & 255;

		if (extBlockIds != null) {
			l |= extBlockIds.a(i, j, k) << 8;
		}

		return Block.getById(l);
	}

	public void setTypeId(int i, int j, int k, Block block) {
		// CraftBukkit start - Compact storage
		Block block1 = getTypeId(i, j, k);
		if (block == block1)
			return;

		if (block1 != Blocks.AIR) {
			--nonEmptyBlockCount;
			if (block1.isTicking()) {
				--tickingBlockCount;
			}
		}

		if (block != Blocks.AIR) {
			++nonEmptyBlockCount;
			if (block.isTicking()) {
				++tickingBlockCount;
			}
		}

		int i1 = Block.getId(block);

		// CraftBukkit start - Compact storage
		if (blockIds == null) {
			blockIds = new byte[4096];
			Arrays.fill(blockIds, (byte) (compactId & 255));
		}
		// CraftBukkit end

		blockIds[j << 8 | k << 4 | i] = (byte) (i1 & 255);
		if (i1 > 255) {
			if (extBlockIds == null) {
				extBlockIds = expandCompactNibble(compactExtId); // CraftBukkit - Compact storage
			}

			extBlockIds.a(i, j, k, (i1 & 3840) >> 8);
		} else if (extBlockIds != null) {
			extBlockIds.a(i, j, k, 0);
		}
	}

	public int getData(int i, int j, int k) {
		// CraftBukkit start - Compact storage
		if (blockData == null)
			return compactData;
		// CraftBukkit end
		return blockData.a(i, j, k);
	}

	public void setData(int i, int j, int k, int l) {
		// CraftBukkit start - Compact storage
		if (blockData == null) {
			if (compactData == l)
				return;
			blockData = expandCompactNibble(compactData);
		}
		// CraftBukkit end
		blockData.a(i, j, k, l);
	}

	public boolean isEmpty() {
		return nonEmptyBlockCount == 0;
	}

	public boolean shouldTick() {
		return tickingBlockCount > 0;
	}

	public int getYPosition() {
		return yPos;
	}

	public void setSkyLight(int i, int j, int k, int l) {
		// CraftBukkit start - Compact storage
		if (skyLight == null) {
			if (compactSky == l)
				return;
			skyLight = expandCompactNibble(compactSky);
		}
		// CraftBukkit end
		skyLight.a(i, j, k, l);
	}

	public int getSkyLight(int i, int j, int k) {
		// CraftBukkit start - Compact storage
		if (skyLight == null)
			return compactSky;
		// CraftBukkit end
		return skyLight.a(i, j, k);
	}

	public void setEmittedLight(int i, int j, int k, int l) {
		// CraftBukkit start - Compact storage
		if (emittedLight == null) {
			if (compactEmitted == l)
				return;
			emittedLight = expandCompactNibble(compactEmitted);
		}
		// CraftBukkit end
		emittedLight.a(i, j, k, l);
	}

	public int getEmittedLight(int i, int j, int k) {
		// CraftBukkit start - Compact storage
		if (emittedLight == null)
			return compactEmitted;
		// CraftBukkit end
		return emittedLight.a(i, j, k);
	}

	public void recalcBlockCounts() {
		// CraftBukkit start - Optimize for speed
		int cntNonEmpty = 0;
		int cntTicking = 0;

		if (blockIds == null) {
			int id = compactId;
			if (extBlockIds == null) {
				id |= compactExtId << 8;
				if (id > 0) {
					Block block = Block.getById(id);
					if (block == null) {
						compactId = 0;
						compactExtId = 0;
					} else {
						cntNonEmpty = 4096;
						if (block.isTicking()) {
							cntTicking = 4096;
						}
					}
				}
			} else {
				byte[] ext = extBlockIds.a;
				for (int off = 0, off2 = 0; off < 4096;) {
					byte extid = ext[off2];
					int l = id & 0xFF | (extid & 0xF) << 8; // Even data
					if (l > 0) {
						Block block = Block.getById(l);
						if (block == null) {
							compactId = 0;
							ext[off2] &= 0xF0;
						} else {
							++cntNonEmpty;
							if (block.isTicking()) {
								++cntTicking;
							}
						}
					}
					off++;
					l = id & 0xFF | (extid & 0xF0) << 4; // Odd data
					if (l > 0) {
						Block block = Block.getById(l);
						if (block == null) {
							compactId = 0;
							ext[off2] &= 0x0F;
						} else {
							++cntNonEmpty;
							if (block.isTicking()) {
								++cntTicking;
							}
						}
					}
					off++;
					off2++;
				}
			}
		} else {
			byte[] blkIds = blockIds;
			if (extBlockIds == null) { // No extended block IDs?  Don't waste time messing with them
				for (int off = 0; off < blkIds.length; off++) {
					int l = blkIds[off] & 0xFF;
					if (l > 0) {
						if (Block.getById(l) == null) {
							blkIds[off] = 0;
						} else {
							++cntNonEmpty;
							if (Block.getById(l).isTicking()) {
								++cntTicking;
							}
						}
					}
				}
			} else {
				byte[] ext = extBlockIds.a;
				for (int off = 0, off2 = 0; off < blkIds.length;) {
					byte extid = ext[off2];
					int l = blkIds[off] & 0xFF | (extid & 0xF) << 8; // Even data
					if (l > 0) {
						if (Block.getById(l) == null) {
							blkIds[off] = 0;
							ext[off2] &= 0xF0;
						} else {
							++cntNonEmpty;
							if (Block.getById(l).isTicking()) {
								++cntTicking;
							}
						}
					}
					off++;
					l = blkIds[off] & 0xFF | (extid & 0xF0) << 4; // Odd data
					if (l > 0) {
						if (Block.getById(l) == null) {
							blkIds[off] = 0;
							ext[off2] &= 0x0F;
						} else {
							++cntNonEmpty;
							if (Block.getById(l).isTicking()) {
								++cntTicking;
							}
						}
					}
					off++;
					off2++;
				}
			}
		}
		nonEmptyBlockCount = cntNonEmpty;
		tickingBlockCount = cntTicking;
	}

	public void old_recalcBlockCounts() {
		// CraftBukkit end
		nonEmptyBlockCount = 0;
		tickingBlockCount = 0;

		for (int i = 0; i < 16; ++i) {
			for (int j = 0; j < 16; ++j) {
				for (int k = 0; k < 16; ++k) {
					Block block = getTypeId(i, j, k);

					if (block != Blocks.AIR) {
						++nonEmptyBlockCount;
						if (block.isTicking()) {
							++tickingBlockCount;
						}
					}
				}
			}
		}
	}

	public byte[] getIdArray() {
		// CraftBukkit start - Compact storage
		if (blockIds == null) {
			byte[] ids = new byte[4096];
			Arrays.fill(ids, (byte) (compactId & 255));
			return ids;
		}
		// CraftBukkit end
		return blockIds;
	}

	public NibbleArray getExtendedIdArray() {
		// CraftBukkit start - Compact storage
		if (extBlockIds == null && compactExtId != 0)
			return compactPregen[compactExtId];
		// CraftBukkit end
		return extBlockIds;
	}

	public NibbleArray getDataArray() {
		// CraftBukkit start - Compact storage
		if (blockData == null)
			return compactPregen[compactData];
		// CraftBukkit end
		return blockData;
	}

	public NibbleArray getEmittedLightArray() {
		// CraftBukkit start - Compact storage
		if (emittedLight == null)
			return compactPregen[compactEmitted];
		// CraftBukkit end
		return emittedLight;
	}

	public NibbleArray getSkyLightArray() {
		// CraftBukkit start - Compact storage
		if (skyLight == null && compactSky != -1)
			return compactPregen[compactSky];
		// CraftBukkit end
		return skyLight;
	}

	public void setIdArray(byte[] abyte) {
		// CraftBukkit start - Compact storage
		if (abyte == null) {
			compactId = 0;
			blockIds = null;
			return;
		} else if (canBeCompact(abyte)) {
			compactId = abyte[0] & 255;
			return;
		}
		// CraftBukkit end
		blockIds = validateByteArray(abyte); // CraftBukkit - Validate data
	}

	public void setExtendedIdArray(NibbleArray nibblearray) {
		// CraftBukkit start - Compact storage
		if (nibblearray == null) {
			compactExtId = 0;
			extBlockIds = null;
			return;
		} else if (canBeCompact(nibblearray.a)) {
			compactExtId = (byte) (nibblearray.a(0, 0, 0) & 0xF);
			return;
		}
		// CraftBukkit end
		extBlockIds = validateNibbleArray(nibblearray); // CraftBukkit - Validate data
	}

	public void setDataArray(NibbleArray nibblearray) {
		// CraftBukkit start - Compact storage
		if (nibblearray == null) {
			compactData = 0;
			blockData = null;
			return;
		} else if (canBeCompact(nibblearray.a)) {
			compactData = (byte) (nibblearray.a(0, 0, 0) & 0xF);
			return;
		}
		// CraftBukkit end
		blockData = validateNibbleArray(nibblearray); // CraftBukkit - Validate data
	}

	public void setEmittedLightArray(NibbleArray nibblearray) {
		// CraftBukkit start - Compact storage
		if (nibblearray == null) {
			compactEmitted = 0;
			emittedLight = null;
			return;
		} else if (canBeCompact(nibblearray.a)) {
			compactEmitted = (byte) (nibblearray.a(0, 0, 0) & 0xF);
			return;
		}
		// CraftBukkit end
		emittedLight = validateNibbleArray(nibblearray); // CraftBukkit - Validate data
	}

	public void setSkyLightArray(NibbleArray nibblearray) {
		// CraftBukkit start - Compact storage
		if (nibblearray == null) {
			compactSky = -1;
			skyLight = null;
			return;
		} else if (canBeCompact(nibblearray.a)) {
			compactSky = (byte) (nibblearray.a(0, 0, 0) & 0xF);
			return;
		}
		// CraftBukkit end
		skyLight = validateNibbleArray(nibblearray); // CraftBukkit - Validate data
	}

	// CraftBukkit start - Validate array lengths
	private NibbleArray validateNibbleArray(NibbleArray nibbleArray) {
		if (nibbleArray != null && nibbleArray.a.length < 2048) {
			byte[] newArray = new byte[2048];
			System.arraycopy(nibbleArray.a, 0, newArray, 0, nibbleArray.a.length);
			nibbleArray = new NibbleArray(newArray, 4);
		}

		return nibbleArray;
	}

	private byte[] validateByteArray(byte[] byteArray) {
		if (byteArray != null && byteArray.length < 4096) {
			byte[] newArray = new byte[4096];
			System.arraycopy(byteArray, 0, newArray, 0, byteArray.length);
			byteArray = newArray;
		}

		return byteArray;
	}
	// CraftBukkit end
}
