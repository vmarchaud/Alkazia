package org.spigotmc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.server.TileEntity;
import net.minecraft.server.TileEntityBeacon;
import net.minecraft.server.TileEntityChest;
import net.minecraft.server.TileEntityCommand;
import net.minecraft.server.TileEntityComparator;
import net.minecraft.server.TileEntityDispenser;
import net.minecraft.server.TileEntityDropper;
import net.minecraft.server.TileEntityEnchantTable;
import net.minecraft.server.TileEntityEnderChest;
import net.minecraft.server.TileEntityEnderPortal;
import net.minecraft.server.TileEntityFlowerPot;
import net.minecraft.server.TileEntityLightDetector;
import net.minecraft.server.TileEntityNote;
import net.minecraft.server.TileEntityRecordPlayer;
import net.minecraft.server.TileEntitySign;
import net.minecraft.server.TileEntitySkull;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class WorldTileEntityList extends HashSet<TileEntity> {
	private static final TObjectIntHashMap<Class<? extends TileEntity>> tileEntityTickIntervals = new TObjectIntHashMap<Class<? extends TileEntity>>() {
		{
			// Use -1 for no ticking
			// These TE's have empty tick methods, doing nothing. Never bother ticking them.
			for (Class<? extends TileEntity> ignored : new Class[] { TileEntityChest.class, // PaperSpigot - Don't tick chests either
					TileEntityEnderChest.class, // PaperSpigot - Don't tick chests either
					TileEntityRecordPlayer.class, TileEntityDispenser.class, TileEntityDropper.class, TileEntitySign.class, TileEntityNote.class, TileEntityEnderPortal.class, TileEntityCommand.class, TileEntitySkull.class, TileEntityComparator.class, TileEntityFlowerPot.class }) {
				put(ignored, -1);
			}

			// does findPlayer lookup, so this helps performance to slow down
			put(TileEntityEnchantTable.class, 20);

			// Slow things down that players won't notice due to craftbukkit "wall time" patches.
			// These need to be investigated further before they can be safely used here
			//put(TileEntityFurnace.class, 20);
			//put(TileEntityBrewingStand.class, 10);

			// Vanilla controlled values - These are checks already done in vanilla, so don't tick on ticks we know
			// won't do anything anyways
			put(TileEntityBeacon.class, 80);
			put(TileEntityLightDetector.class, 20);
		}
	};

	private static int getInterval(Class<? extends TileEntity> cls) {
		int tickInterval = tileEntityTickIntervals.get(cls);
		return tickInterval != 0 ? tickInterval : 1;
	}

	private static int getBucketId(TileEntity entity, Integer interval) {
		return entity.tileId % interval;
	}

	private final Map<Integer, Multimap<Integer, TileEntity>> tickList = Maps.newHashMap();
	private final WorldServer world;

	public WorldTileEntityList(World world) {
		this.world = (WorldServer) world;
	}

	private Multimap<Integer, TileEntity> getBucket(int interval) {
		Multimap<Integer, TileEntity> intervalBucket = tickList.get(interval);
		if (intervalBucket == null) {
			intervalBucket = ArrayListMultimap.create();
			tickList.put(interval, intervalBucket);
		}
		return intervalBucket;
	}

	/**
	 * Adds the TileEntity to the tick list only if it is expected to tick
	 */
	@Override
	public boolean add(TileEntity entity) {
		if (entity.isAdded)
					return false;

		int interval = getInterval(entity.getClass());
		if (interval > 0) {
			entity.isAdded = true;
			int bucket = getBucketId(entity, interval);
			Multimap<Integer, TileEntity> typeBucket = getBucket(interval);
			return typeBucket.put(bucket, entity);
		}
		return false;
	}

	@Override
	public boolean remove(Object o) {
		if (!(o instanceof TileEntity))
					return false;
		TileEntity entity = (TileEntity) o;
		if (!entity.isAdded)
					return false;
		entity.isAdded = false;
		int interval = getInterval(entity.getClass());
		int bucket = getBucketId(entity, interval);
		Multimap<Integer, TileEntity> typeBucket = getBucket(interval);
		return typeBucket.remove(bucket, entity);
	}

	@Override
	public Iterator iterator() {
		return new WorldTileEntityIterator();
	}

	@Override
	public boolean contains(Object o) {
		return o instanceof TileEntity && ((TileEntity) o).isAdded;
	}

	private class WorldTileEntityIterator implements Iterator<TileEntity> {
		private final Iterator<Map.Entry<Integer, Multimap<Integer, TileEntity>>> intervalIterator;
		private Map.Entry<Integer, Multimap<Integer, TileEntity>> intervalMap = null;
		private Iterator<TileEntity> listIterator = null;

		protected WorldTileEntityIterator() {
			intervalIterator = tickList.entrySet().iterator();
			nextInterval();
		}

		private boolean nextInterval() {
			listIterator = null;
			if (intervalIterator.hasNext()) {
				intervalMap = intervalIterator.next();

				final Integer interval = intervalMap.getKey();
				final Multimap<Integer, TileEntity> buckets = intervalMap.getValue();

				int bucket = (int) (world.getTime() % interval);

				if (!buckets.isEmpty() && buckets.containsKey(bucket)) {
					final Collection<TileEntity> tileList = buckets.get(bucket);

					if (tileList != null && !tileList.isEmpty()) {
						listIterator = tileList.iterator();
						return true;
					}
				}
			}

			return false;

		}

		@Override
		public boolean hasNext() {
			do {
				if (listIterator != null && listIterator.hasNext())
							return true;
			} while (nextInterval());
			return false;
		}

		@Override
		public TileEntity next() {
			return listIterator.next();
		}

		@Override
		public void remove() {
			listIterator.remove();
		}
	}
}
