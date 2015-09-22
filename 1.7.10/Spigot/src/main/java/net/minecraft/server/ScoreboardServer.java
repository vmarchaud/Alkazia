package net.minecraft.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ScoreboardServer extends Scoreboard {

	private final MinecraftServer a;
	private final Set b = new HashSet();
	private PersistentScoreboard c;

	public ScoreboardServer(MinecraftServer minecraftserver) {
		a = minecraftserver;
	}

	@Override
	public void handleScoreChanged(ScoreboardScore scoreboardscore) {
		super.handleScoreChanged(scoreboardscore);
		if (b.contains(scoreboardscore.getObjective())) {
			sendAll(new PacketPlayOutScoreboardScore(scoreboardscore, 0)); // CraftBukkit - Internal packet method
		}

		b();
	}

	@Override
	public void handlePlayerRemoved(String s) {
		super.handlePlayerRemoved(s);
		sendAll(new PacketPlayOutScoreboardScore(s)); // CraftBukkit - Internal packet method
		b();
	}

	@Override
	public void setDisplaySlot(int i, ScoreboardObjective scoreboardobjective) {
		ScoreboardObjective scoreboardobjective1 = getObjectiveForSlot(i);

		super.setDisplaySlot(i, scoreboardobjective);
		if (scoreboardobjective1 != scoreboardobjective && scoreboardobjective1 != null) {
			if (h(scoreboardobjective1) > 0) {
				sendAll(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective)); // CraftBukkit - Internal packet method
			} else {
				g(scoreboardobjective1);
			}
		}

		if (scoreboardobjective != null) {
			if (b.contains(scoreboardobjective)) {
				sendAll(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective)); // CraftBukkit - Internal packet method
			} else {
				e(scoreboardobjective);
			}
		}

		b();
	}

	@Override
	public boolean addPlayerToTeam(String s, String s1) {
		if (super.addPlayerToTeam(s, s1)) {
			ScoreboardTeam scoreboardteam = getTeam(s1);

			sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, Arrays.asList(new String[] { s }), 3)); // CraftBukkit - Internal packet method
			b();
			return true;
		} else
			return false;
	}

	@Override
	public void removePlayerFromTeam(String s, ScoreboardTeam scoreboardteam) {
		super.removePlayerFromTeam(s, scoreboardteam);
		sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, Arrays.asList(new String[] { s }), 4)); // CraftBukkit - Internal packet method
		b();
	}

	@Override
	public void handleObjectiveAdded(ScoreboardObjective scoreboardobjective) {
		super.handleObjectiveAdded(scoreboardobjective);
		b();
	}

	@Override
	public void handleObjectiveChanged(ScoreboardObjective scoreboardobjective) {
		super.handleObjectiveChanged(scoreboardobjective);
		if (b.contains(scoreboardobjective)) {
			sendAll(new PacketPlayOutScoreboardObjective(scoreboardobjective, 2)); // CraftBukkit - Internal packet method
		}

		b();
	}

	@Override
	public void handleObjectiveRemoved(ScoreboardObjective scoreboardobjective) {
		super.handleObjectiveRemoved(scoreboardobjective);
		if (b.contains(scoreboardobjective)) {
			g(scoreboardobjective);
		}

		b();
	}

	@Override
	public void handleTeamAdded(ScoreboardTeam scoreboardteam) {
		super.handleTeamAdded(scoreboardteam);
		sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, 0)); // CraftBukkit - Internal packet method
		b();
	}

	@Override
	public void handleTeamChanged(ScoreboardTeam scoreboardteam) {
		super.handleTeamChanged(scoreboardteam);
		sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, 2)); // CraftBukkit - Internal packet method
		b();
	}

	@Override
	public void handleTeamRemoved(ScoreboardTeam scoreboardteam) {
		super.handleTeamRemoved(scoreboardteam);
		sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, 1)); // CraftBukkit - Internal packet method
		b();
	}

	public void a(PersistentScoreboard persistentscoreboard) {
		c = persistentscoreboard;
	}

	protected void b() {
		if (c != null) {
			c.c();
		}
	}

	public List getScoreboardScorePacketsForObjective(ScoreboardObjective scoreboardobjective) {
		ArrayList arraylist = new ArrayList();

		arraylist.add(new PacketPlayOutScoreboardObjective(scoreboardobjective, 0));

		for (int i = 0; i < 3; ++i) {
			if (getObjectiveForSlot(i) == scoreboardobjective) {
				arraylist.add(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
			}
		}

		Iterator iterator = getScoresForObjective(scoreboardobjective).iterator();

		while (iterator.hasNext()) {
			ScoreboardScore scoreboardscore = (ScoreboardScore) iterator.next();

			arraylist.add(new PacketPlayOutScoreboardScore(scoreboardscore, 0));
		}

		return arraylist;
	}

	public void e(ScoreboardObjective scoreboardobjective) {
		List list = getScoreboardScorePacketsForObjective(scoreboardobjective);
		Iterator iterator = a.getPlayerList().players.iterator();

		while (iterator.hasNext()) {
			EntityPlayer entityplayer = (EntityPlayer) iterator.next();
			if (entityplayer.getBukkitEntity().getScoreboard().getHandle() != this) {
				continue; // CraftBukkit - Only players on this board
			}
			Iterator iterator1 = list.iterator();

			while (iterator1.hasNext()) {
				Packet packet = (Packet) iterator1.next();

				entityplayer.playerConnection.sendPacket(packet);
			}
		}

		b.add(scoreboardobjective);
	}

	public List f(ScoreboardObjective scoreboardobjective) {
		ArrayList arraylist = new ArrayList();

		arraylist.add(new PacketPlayOutScoreboardObjective(scoreboardobjective, 1));

		for (int i = 0; i < 3; ++i) {
			if (getObjectiveForSlot(i) == scoreboardobjective) {
				arraylist.add(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
			}
		}

		return arraylist;
	}

	public void g(ScoreboardObjective scoreboardobjective) {
		List list = f(scoreboardobjective);
		Iterator iterator = a.getPlayerList().players.iterator();

		while (iterator.hasNext()) {
			EntityPlayer entityplayer = (EntityPlayer) iterator.next();
			if (entityplayer.getBukkitEntity().getScoreboard().getHandle() != this) {
				continue; // CraftBukkit - Only players on this board
			}
			Iterator iterator1 = list.iterator();

			while (iterator1.hasNext()) {
				Packet packet = (Packet) iterator1.next();

				entityplayer.playerConnection.sendPacket(packet);
			}
		}

		b.remove(scoreboardobjective);
	}

	public int h(ScoreboardObjective scoreboardobjective) {
		int i = 0;

		for (int j = 0; j < 3; ++j) {
			if (getObjectiveForSlot(j) == scoreboardobjective) {
				++i;
			}
		}

		return i;
	}

	// CraftBukkit start - Send to players
	private void sendAll(Packet packet) {
		for (EntityPlayer entityplayer : (List<EntityPlayer>) a.getPlayerList().players) {
			if (entityplayer.getBukkitEntity().getScoreboard().getHandle() == this) {
				entityplayer.playerConnection.sendPacket(packet);
			}
		}
	}
	// CraftBukkit end
}
