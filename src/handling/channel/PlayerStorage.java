package handling.channel;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import handling.world.CharacterTransfer;
import handling.world.CheaterData;
import handling.world.World;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.Timer.PingTimer;

public class PlayerStorage {

    private final ReentrantReadWriteLock locks = new ReentrantReadWriteLock();
    private final Lock wL = locks.writeLock(); // Pending Players (CS/MTS)
    private final Lock rlock = locks.readLock();
    private final Lock wlock = locks.writeLock();
    private final Map<Integer, MapleCharacter> storage = new LinkedHashMap<>();
    private final Map<String, MapleCharacter> nameToChar = new HashMap<>();
    private final Map<Integer, CharacterTransfer> pendingChars = new HashMap<>();

    public PlayerStorage() {
        PingTimer.getInstance().register(new PersistingTask(), 60000);
    }

    public void addPlayer(MapleCharacter chr) {
        wlock.lock();
        try {
            World.Find.register(chr.getId(), chr.getName(), chr.getWorld(), chr.getClient().getChannel());
            nameToChar.put(chr.getName(), chr);
            storage.put(chr.getId(), chr);
        } finally {
            wlock.unlock();
        }
    }

    public MapleCharacter removePlayer(int chr) {
        wlock.lock();
        try {
            String naam = getCharacterById(chr).getName();
            World.Find.forceDeregister(chr, naam);
            nameToChar.remove(naam);
            return storage.remove(chr);
        } finally {
            wlock.unlock();
        }
    }

    public final void deregisterPlayer(final MapleCharacter chr) {
        wL.lock();
        try {
            nameToChar.remove(chr.getName().toLowerCase());
            storage.remove(chr.getId());
        } finally {
            wL.unlock();
        }
        World.Find.forceDeregister(chr.getId(), chr.getName());
    }

    public MapleCharacter getCharacterByName(String name) {
        rlock.lock();
        try {
            for (MapleCharacter chr : storage.values()) {
                if (chr.getName().toLowerCase().equals(name.toLowerCase())) {
                    return chr;
                }
            }
            return null;
        } finally {
            rlock.unlock();
        }
    }

    public MapleCharacter getCharacterById(int id) {
        rlock.lock();
        try {
            return storage.get(id);
        } finally {
            rlock.unlock();
        }
    }

    public Collection<MapleCharacter> getAllCharacters() {
        rlock.lock();
        try {
            return storage.values();
        } finally {
            rlock.unlock();
        }
    }

    public final void registerPendingPlayer(final CharacterTransfer chr, final int playerid) {
        wL.lock();
        try {
            pendingChars.put(playerid, chr);
        } finally {
            wL.unlock();
        }
    }

    public final int pendingCharacterSize() {
        return pendingChars.size();
    }

    public final void deregisterPendingPlayer(final int charid) {
        wL.lock();
        try {
            pendingChars.remove(charid);
        } finally {
            wL.unlock();
        }
    }

    public final CharacterTransfer getPendingCharacter(final int charid) {
        wL.lock();
        try {
            return pendingChars.remove(charid);
        } finally {
            wL.unlock();
        }
    }

    public final void deregisterPendingPlayerByAccountId(final int accountId) {
        wL.lock();
        try {
            pendingChars.values().stream().filter((transfer) -> (transfer.accountid == accountId)).forEachOrdered((transfer) -> {
                pendingChars.remove(transfer.characterid);
            });
        } finally {
            wL.unlock();
        }
    }

    public final int getConnectedClients() {
        return storage.size();
    }

    public final void disconnectAll() {
        disconnectAll(false);
    }

    public final void disconnectAll(final boolean checkGM) {
        wL.lock();
        try {
            final Iterator<MapleCharacter> itr = nameToChar.values().iterator();
            MapleCharacter chr;
            while (itr.hasNext()) {
                chr = itr.next();
                if (!chr.isGM() || !checkGM) {
                    chr.getClient().disconnect(false, false, true);
                    chr.getClient().getSession().close(true);
                    World.Find.forceDeregister(chr.getId(), chr.getName());
                    itr.remove();
                }
            }
        } finally {
            wL.unlock();
        }
    }

    public final String getOnlinePlayers(final boolean byGM) {
        final StringBuilder sb = new StringBuilder();
        if (byGM) {
            rlock.lock();
            try {
                final Iterator<MapleCharacter> itr = nameToChar.values().iterator();
                while (itr.hasNext()) {
                    sb.append(MapleCharacterUtil.makeMapleReadable(itr.next().getName()));
                    sb.append(", ");
                }
            } finally {
                rlock.unlock();
            }
        } else {
            rlock.lock();
            try {
                final Iterator<MapleCharacter> itr = nameToChar.values().iterator();
                MapleCharacter chr;
                while (itr.hasNext()) {
                    chr = itr.next();
                    if (!chr.isGM()) {
                        sb.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                        sb.append(", ");
                    }
                }
            } finally {
                rlock.unlock();
            }
        }
        return sb.toString();
    }

    public final List<MapleCharacter> getAllCharactersThreadSafe() {
        List<MapleCharacter> ret = new ArrayList<>();
        try {
            ret.addAll(getAllCharacters());
        } catch (ConcurrentModificationException ex) {

        }
        return ret;
    }

    public final List<CheaterData> getCheaters() {
        final List<CheaterData> cheaters = new ArrayList<>();

        try {
            final Iterator<MapleCharacter> itr = this.storage.values().iterator();
            MapleCharacter chr;
            while (itr.hasNext()) {
                chr = itr.next();

                if (chr.getCheatTracker().getPoints() > 0) {
                    cheaters.add(new CheaterData(chr.getCheatTracker().getPoints(), MapleCharacterUtil.makeMapleReadable(chr.getName()) + "(編號:" + chr.getId() + ") 檢測次數(" + chr.getCheatTracker().getPoints() + ") " + chr.getCheatTracker().getSummary() + " 地圖:" + chr.getMap().getMapName()));
                }
            }
        } finally {

        }
        return cheaters;
    }

    public class PersistingTask implements Runnable {

        @Override
        public void run() {
            wlock.lock();
            try {
                final long currenttime = System.currentTimeMillis();
                final Iterator<Map.Entry<Integer, CharacterTransfer>> itr = pendingChars.entrySet().iterator();

                while (itr.hasNext()) {
                    if (currenttime - itr.next().getValue().TranferTime > 40000) { // 40 sec
                        itr.remove();
                    }
                }
            } finally {
                wlock.unlock();
            }
        }
    }
}
