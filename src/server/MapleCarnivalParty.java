package server;

import client.MapleCharacter;
import handling.channel.ChannelServer;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import server.maps.MapleMap;
import tools.packet.CField;

public class MapleCarnivalParty {

    private List<Integer> members = new LinkedList<>();
    private WeakReference<MapleCharacter> leader;
    private byte team;
    private int world, channel;
    private short availableCP = 0, totalCP = 0;
    private boolean winner = false;

    public MapleCarnivalParty(final MapleCharacter owner, final List<MapleCharacter> members1, final byte team1) {
        leader = new WeakReference<>(owner);
        members1.stream().map((mem) -> {
            members.add(mem.getId());
            return mem;
        }).forEachOrdered((mem) -> {
            mem.setCarnivalParty(this);
        });
        team = team1;
        channel = owner.getClient().getChannel();
        world = owner.getClient().getWorld();
    }

    public final MapleCharacter getLeader() {
        return leader.get();
    }

    public void addCP(MapleCharacter player, int ammount) {
        totalCP += ammount;
        availableCP += ammount;
        player.addCP(ammount);
    }

    public int getTotalCP() {
        return totalCP;
    }

    public int getAvailableCP() {
        return availableCP;
    }

    public void useCP(MapleCharacter player, int ammount) {
        availableCP -= ammount;
        player.useCP(ammount);
    }

    public List<Integer> getMembers() {
        return members;
    }

    public int getTeam() {
        return team;
    }

    public void warp(final MapleMap map, final String portalname) {
        members.stream().map((chr) -> ChannelServer.getInstance(world, channel).getPlayerStorage().getCharacterById(chr)).filter((c) -> (c != null)).forEachOrdered((c) -> {
            c.changeMap(map, map.getPortal(portalname));
        });
    }

    public void warp(final MapleMap map, final int portalid) {
        members.stream().map((chr) -> ChannelServer.getInstance(world, channel).getPlayerStorage().getCharacterById(chr)).filter((c) -> (c != null)).forEachOrdered((c) -> {
            c.changeMap(map, map.getPortal(portalid));
        });
    }

    public boolean allInMap(MapleMap map) {
        return members.stream().noneMatch((chr) -> (map.getCharacterById(chr) == null));
    }

    public void removeMember(MapleCharacter chr) {
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i) == chr.getId()) {
                members.remove(i);
                chr.setCarnivalParty(null);
            }
        }

    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean status) {
        winner = status;
    }

    public void displayMatchResult() {
        final String effect = winner ? "quest/carnival/win" : "quest/carnival/lose";
        final String sound = winner ? "MobCarnival/Win" : "MobCarnival/Lose";
        boolean done = false;
        for (int chr : members) {
            final MapleCharacter c = ChannelServer.getInstance(world, channel).getPlayerStorage().getCharacterById(chr);
            if (c != null) {
                c.getClient().getSession().write(CField.showEffect(effect));
                c.getClient().getSession().write(CField.playSound(sound));
                if (!done) {
                    done = true;
                    c.getMap().killAllMonsters(true);
                    c.getMap().setSpawns(false); //resetFully will take care of this
                }
            }
        }

    }
}
