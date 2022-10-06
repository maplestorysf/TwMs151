package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import client.SkillFactory;
import java.util.List;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MobSkillFactory;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;
import tools.packet.MonsterCarnivalPacket;

public class MonsterCarnivalHandler {

    public static final void MonsterCarnival(LittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer().getCarnivalParty() == null) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        int tab = slea.readByte();

        switch (tab) {
            case 0:
                // 100 CP
                List mobs = c.getPlayer().getMap().getMobsToSpawn();
                int num = MapleCharacter.rand(1, 4); // size should be (int)5
                if ((num >= mobs.size()) || (c.getPlayer().getAvailableCP() < 100)) {
                    c.getPlayer().dropMessage(5, "You do not have the CP.");
                    c.getSession().write(CWvsContext.enableActions());
                    return;
                }   MapleMonster mons = MapleLifeFactory.getMonster(((Integer) ((Pair) mobs.get(num)).left));
                if ((mons != null) && (c.getPlayer().getMap().makeCarnivalSpawn(c.getPlayer().getCarnivalParty().getTeam(), mons, num))) {
                    c.getPlayer().getCarnivalParty().useCP(c.getPlayer(), 100);
                    c.getPlayer().CPUpdate(false, c.getPlayer().getAvailableCP(), c.getPlayer().getTotalCP(), 0);
                    c.getPlayer().getMap().getCharactersThreadsafe().forEach((chr) -> {
                        chr.CPUpdate(true, c.getPlayer().getCarnivalParty().getAvailableCP(), c.getPlayer().getCarnivalParty().getTotalCP(), c.getPlayer().getCarnivalParty().getTeam());
                    });
                    c.getPlayer().getMap().broadcastMessage(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, num));
                    c.getSession().write(CWvsContext.enableActions());
                } else {
                    c.getPlayer().dropMessage(5, "You may no longer summon the monster.");
                    c.getSession().write(CWvsContext.enableActions());
                }   break;
            case 1:
                {
                    // 200 CP
                    if (c.getPlayer().getAvailableCP() < 200) {
                        c.getPlayer().dropMessage(5, "You do not have the CP.");
                        c.getSession().write(CWvsContext.enableActions());
                        return;
                    }       int rand = MapleCharacter.rand(1, 20);
                    if (rand < 10) {
                        SkillFactory.getSkill(80001079).getEffect(SkillFactory.getSkill(80001079).getMaxLevel()).applyTo(c.getPlayer());
                        c.getSession().write(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, 0));
                        c.getPlayer().getParty().getMembers().stream().filter((mpc) -> (mpc.getId() != c.getPlayer().getId() && mpc.getChannel() == c.getChannel() && mpc.getMapid() == c.getPlayer().getMapId() && mpc.isOnline())).map((mpc) -> c.getPlayer().getMap().getCharacterById(mpc.getId())).filter((mc) -> (mc != null)).map((mc) -> {
                            SkillFactory.getSkill(80001079).getEffect(SkillFactory.getSkill(80001079).getMaxLevel()).applyTo(mc);
                            return mc;
                        }).forEachOrdered((mc) -> {
                            mc.getClient().getSession().write(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, 0));
                        });
                    } else {
                        c.getPlayer().getMap().getCharactersThreadsafe().stream().filter((chr) -> (chr.getParty() != c.getPlayer().getParty())).map((chr) -> {
                            chr.giveDebuff(MapleDisease.BLIND, MobSkillFactory.getMobSkill(136, 1));
                            return chr;
                        }).forEachOrdered((_item) -> {
                            c.getPlayer().getMap().broadcastMessage(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, 1));
                        }); // should check for null partys but whatever
                    }       c.getPlayer().getCarnivalParty().useCP(c.getPlayer(), 200);
                    c.getPlayer().CPUpdate(false, c.getPlayer().getAvailableCP(), c.getPlayer().getTotalCP(), 0);
                    c.getPlayer().getParty().getMembers().stream().map((mpc) -> c.getPlayer().getMap().getCharacterById(mpc.getId())).forEachOrdered((mc) -> {
                        mc.CPUpdate(true, c.getPlayer().getCarnivalParty().getAvailableCP(), c.getPlayer().getCarnivalParty().getTotalCP(), c.getPlayer().getCarnivalParty().getTeam());
                    });     break;
                }
            case 2:
                {
                    // 300 CP
                    if (c.getPlayer().getAvailableCP() < 300) {
                        c.getPlayer().dropMessage(5, "You do not have the CP.");
                        c.getSession().write(CWvsContext.enableActions());
                        return;
                    }       int rand = MapleCharacter.rand(1, 20);
                    if (rand < 10) {
                        SkillFactory.getSkill(80001080).getEffect(SkillFactory.getSkill(80001080).getMaxLevel()).applyTo(c.getPlayer());
                        c.getSession().write(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, 2));
                        c.getPlayer().getParty().getMembers().stream().filter((mpc) -> (mpc.getId() != c.getPlayer().getId() && mpc.getChannel() == c.getChannel() && mpc.getMapid() == c.getPlayer().getMapId() && mpc.isOnline())).map((mpc) -> c.getPlayer().getMap().getCharacterById(mpc.getId())).filter((mc) -> (mc != null)).map((mc) -> {
                            SkillFactory.getSkill(80001080).getEffect(SkillFactory.getSkill(80001080).getMaxLevel()).applyTo(mc);
                            return mc;
                        }).forEachOrdered((mc) -> {
                            mc.getClient().getSession().write(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, 2));
                        });
                    } else {
                        c.getPlayer().getMap().getCharactersThreadsafe().stream().filter((chr) -> (chr.getParty() != c.getPlayer().getParty())).map((chr) -> {
                            chr.giveDebuff(MapleDisease.SLOW, MobSkillFactory.getMobSkill(126, 10));
                            return chr;
                        }).forEachOrdered((_item) -> {
                            c.getPlayer().getMap().broadcastMessage(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, 3));
                        });
                    }       c.getPlayer().getCarnivalParty().useCP(c.getPlayer(), 300);
                    c.getPlayer().CPUpdate(false, c.getPlayer().getAvailableCP(), c.getPlayer().getTotalCP(), 0);
                    c.getPlayer().getParty().getMembers().stream().map((mpc) -> c.getPlayer().getMap().getCharacterById(mpc.getId())).forEachOrdered((mc) -> {
                        mc.CPUpdate(true, c.getPlayer().getCarnivalParty().getAvailableCP(), c.getPlayer().getCarnivalParty().getTotalCP(), c.getPlayer().getCarnivalParty().getTeam());
                    });     break;
                }
            case 3:
                {
                    // 400 CP
                    if (c.getPlayer().getAvailableCP() < 400) {
                        c.getPlayer().dropMessage(5, "You do not have the CP.");
                        c.getSession().write(CWvsContext.enableActions());
                        return;
                    }       int rand = MapleCharacter.rand(1, 20);
                    if (rand < 10) {
                        SkillFactory.getSkill(80001081).getEffect(SkillFactory.getSkill(80001081).getMaxLevel()).applyTo(c.getPlayer());
                        c.getSession().write(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, 4));
                        c.getPlayer().getParty().getMembers().stream().filter((mpc) -> (mpc.getId() != c.getPlayer().getId() && mpc.getChannel() == c.getChannel() && mpc.getMapid() == c.getPlayer().getMapId() && mpc.isOnline())).map((mpc) -> c.getPlayer().getMap().getCharacterById(mpc.getId())).filter((mc) -> (mc != null)).map((mc) -> {
                            SkillFactory.getSkill(80001081).getEffect(SkillFactory.getSkill(80001081).getMaxLevel()).applyTo(mc);
                            return mc;
                        }).forEachOrdered((mc) -> {
                            mc.getClient().getSession().write(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, 4));
                        });
                    } else {
                        c.getPlayer().getMap().getCharactersThreadsafe().stream().filter((chr) -> (chr.getParty() != c.getPlayer().getParty())).map((chr) -> {
                            chr.giveDebuff(MapleDisease.SEAL, MobSkillFactory.getMobSkill(120, 10));
                            return chr;
                        }).forEachOrdered((_item) -> {
                            c.getPlayer().getMap().broadcastMessage(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, 5));
                        });
                    }       c.getPlayer().getCarnivalParty().useCP(c.getPlayer(), 400);
                    c.getPlayer().CPUpdate(false, c.getPlayer().getAvailableCP(), c.getPlayer().getTotalCP(), 0);
                    c.getPlayer().getParty().getMembers().stream().map((mpc) -> c.getPlayer().getMap().getCharacterById(mpc.getId())).forEachOrdered((mc) -> {
                        mc.CPUpdate(true, c.getPlayer().getCarnivalParty().getAvailableCP(), c.getPlayer().getCarnivalParty().getTotalCP(), c.getPlayer().getCarnivalParty().getTeam());
                    });     break;
                }
            default:
                break;
        }
        c.getSession().write(CWvsContext.enableActions());
    }
}
