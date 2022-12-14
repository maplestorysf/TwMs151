package client.messages.commands;

import client.MapleCharacter;
import constants.ServerConstants.PlayerGMRank;
import client.MapleClient;
import client.MapleStat;
import client.SkillFactory;
import client.anticheat.CheatingOffense;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.messages.CommandProcessorUtil;
import constants.GameConstants;
import constants.ServerConstants;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.World;
import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import scripting.EventManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.Timer.EventTimer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleNPC;
import server.life.OverrideMonsterStats;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.CPUSampler;
import tools.StringUtil;
import tools.packet.MobPacket;
import java.util.concurrent.ScheduledFuture;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.HexTool;
import tools.Pair;
import tools.data.MaplePacketLittleEndianWriter;
import tools.packet.CField;
import tools.packet.CField.NPCPacket;
import tools.packet.CWvsContext;

public class AdminCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.???????????????;
    }

    public static class GC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            System.gc();
            System.out.println("????????????????????? ---- " + FileoutputUtil.NowTime());
            //Timer.BuffTimer.getInstance().purge();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!GC - ?????????????????????").toString();
        }
    }

    public static class SavePlayerShops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            handling.channel.ChannelServer.getAllInstances().forEach((cserv) -> {
                cserv.closeAllMerchant();
            });
            c.getPlayer().dropMessage(6, "????????????????????????.");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!savePlayerShops - ??????????????????").toString();
        }
    }

    public static class Fame extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim;
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                return false;
            }
            victim = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);

            short fame;
            try {
                fame = Short.parseShort(splitted[2]);
            } catch (Exception nfe) {
                c.getPlayer().dropMessage(6, "??????????????????");
                return false;
            }
            if (victim != null && player.allowedToTarget(victim)) {
                victim.addFame(fame);
                victim.updateSingleStat(MapleStat.FAME, victim.getFame());
            } else {
                c.getPlayer().dropMessage(6, "[fame] ???????????????");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!fame <????????????> <??????> ...  - ??????").toString();
        }
    }

    public static class GodMode extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            if (player.isInvincible()) {
                player.setInvincible(false);
                player.dropMessage(6, "??????????????????");
            } else {
                player.setInvincible(true);
                player.clearAllCooldowns();
                player.dropMessage(6, "??????????????????.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!godmode  - ????????????").toString();
        }
    }

    public static class GainCash extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            MapleCharacter player;
            int amount = 0;
            String name = "";
            try {
                amount = Integer.parseInt(splitted[1]);
                name = splitted[2];
            } catch (Exception ex) {
                return false;
            }
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage("?????????????????????");
                return true;
            }
            player = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);
            if (player == null) {
                c.getPlayer().dropMessage("?????????????????????");
                return true;
            }
            player.modifyCSPoints(1, amount, true);
            player.dropMessage("????????????Gash??????" + amount + "???");
            FileoutputUtil.logToFile("logs/Data/????????????.txt", "\r\n " + FileoutputUtil.NowTime() + " IP: " + c.getSession().getRemoteAddress().toString().split(":")[0] + " GM " + c.getPlayer().getName() + " ?????? " + player.getName() + " Gash?????? " + amount + "???");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!gaingash <??????> <??????> - ??????Gash??????").toString();
        }
    }

    public static class ????????? extends GainMaplePoint {

        @Override
        public String getMessage() {
            return new StringBuilder().append("!????????? <??????> <??????> - ??????????????????").toString();
        }
    }

    public static class GainMaplePoint extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            MapleCharacter player;
            int amount = 0;
            String name = "";
            try {
                amount = Integer.parseInt(splitted[1]);
                name = splitted[2];
            } catch (Exception ex) {

            }
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage("?????????????????????");
                return true;
            }
            player = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);
            if (player == null) {
                c.getPlayer().dropMessage("?????????????????????");
                return true;
            }
            player.modifyCSPoints(2, amount, true);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!gainmaplepoint <??????> <??????> - ??????????????????").toString();
        }
    }

    public static class GainPoint extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            MapleCharacter player;
            int amount = 0;
            String name = "";
            try {
                amount = Integer.parseInt(splitted[1]);
                name = splitted[2];
            } catch (Exception ex) {

            }
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage("?????????????????????");
                return true;
            }
            player = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);
            if (player == null) {
                c.getPlayer().dropMessage("?????????????????????");
                return true;
            }
            player.setPoints(player.getPoints() + amount);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!gainpoint <??????> <??????> - ??????Point").toString();
        }
    }

    public static class GainVP extends GainPoint {
    }

    public static class item extends CommandExecute {// TODO

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            int itemId = 0;
            try {
                itemId = Integer.parseInt(splitted[1]);
            } catch (Exception ex) {

            }
            short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);

            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (GameConstants.isPet(itemId)) {
                MaplePet pet = MaplePet.createPet(itemId, MapleInventoryIdentifier.getInstance());
                if (pet != null) {
                    // MapleInventoryManipulator.addById(c, itemId, (short) 1, c.getPlayer().getName(), pet, ii.getPetLife(itemId));
                }
            } else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " - ???????????????");
            } else {
                Item item;
                byte flag = 0;
                flag |= ItemFlag.LOCK.getValue();

                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    item = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                    item.setFlag(flag);
                } else {
                    item = new client.inventory.Item(itemId, (byte) 0, quantity, (byte) 0);
                    if (GameConstants.getInventoryType(itemId) != MapleInventoryType.USE) {
                        item.setFlag(flag);
                    }
                }
                item.setOwner(c.getPlayer().getName());
                item.setGMLog(c.getPlayer().getName());

                MapleInventoryManipulator.addbyItem(c, item);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!item <??????ID> - ????????????").toString();
        }
    }

    public static class serverMsg extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                ChannelServer.getAllInstances().forEach((ch) -> {
                    ch.setServerMessage(sb.toString());
                });
                World.Broadcast.broadcastMessage(CWvsContext.serverMessage(sb.toString()));
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!servermsg ?????? - ????????????????????????").toString();
        }
    }

    public static class MobVac extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().getAllMonstersThreadsafe().stream().map((mmo) -> (MapleMonster) mmo).map((monster) -> {
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MobPacket.moveMonster(false, 0, 0, monster.getObjectId(), monster.getPosition(), c.getPlayer().getLastRes()), new Point(0, 0));
                return monster;
            }).forEachOrdered((monster) -> {
                monster.setPosition(c.getPlayer().getPosition());
            });
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!mobvac - ????????????").toString();
        }
    }

//    public static class ItemVac extends CommandExecute {
//
//        @Override
//        public boolean execute(MapleClient c, String splitted[]) {
//            boolean ItemVac = c.getPlayer().getItemVac();
//            if (ItemVac == false) {
//                c.getPlayer().stopItemVac();
//                c.getPlayer().startItemVac();
//            } else {
//                c.getPlayer().stopItemVac();
//            }
//            c.getPlayer().dropMessage(6, "????????????????????????:" + (ItemVac == false ? "??????" : "??????"));
//            return true;
//
//        }
//
//        @Override
//        public String getMessage() {
//            return new StringBuilder().append("!ItemVac - ??????????????????").toString();
//        }
//    }
    public static class ?????????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            final EventManager em = c.getChannelServer().getEventSM().getEventManager("AutomatedEvent");
            if (em != null) {
                em.scheduleRandomEvent();
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????????????????? - ??????????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        private static ScheduledFuture<?> ts = null;
        private int min = 1, sec = 0;

        @Override
        public boolean execute(final MapleClient c, String splitted[]) {
            if (c.getChannelServer().getEvent() == c.getPlayer().getMapId()) {
                MapleEvent.setEvent(c.getChannelServer(), false);
                if (c.getPlayer().getMapId() == 109020001) {
                    sec = 10;
                    c.getPlayer().dropMessage(5, "??????????????????????????????????????????????????????");
                    World.Broadcast.broadcastMessage(c.getWorld(), CWvsContext.serverMessage("??????:" + c.getChannel() + "???????????????????????????????????????????????????????????????"));
                    c.getPlayer().getMap().broadcastMessage(CField.getClock(sec));
                } else {
                    sec = 60;
                    c.getPlayer().dropMessage(5, "??????????????????????????????????????????????????????");
                    World.Broadcast.broadcastMessage(c.getWorld(), CWvsContext.serverMessage("??????:" + c.getChannel() + "???????????????????????????????????????????????????????????????"));
                    c.getPlayer().getMap().broadcastMessage(CField.getClock(sec));
                }
                ts = EventTimer.getInstance().register(new Runnable() {

                    @Override
                    public void run() {
                        if (min == 0) {
                            MapleEvent.onStartEvent(c.getPlayer());
                            ts.cancel(false);
                            return;
                        }
                        min--;
                    }
                }, sec * 1000);
                return true;
            } else {
                c.getPlayer().dropMessage(5, "?????????????????? !???????????? ????????????????????????????????????????????????????????????????????????");
                return true;
            }
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? - ????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            final MapleEventType type = MapleEventType.getByString(splitted[1]);
            if (type == null) {
                final StringBuilder sb = new StringBuilder("????????????????????????: ");
                for (MapleEventType t : MapleEventType.values()) {
                    sb.append(t.name()).append(",");
                }
                c.getPlayer().dropMessage(5, sb.toString().substring(0, sb.toString().length() - 1));
            }
            final String msg = MapleEvent.scheduleEvent(type, c.getChannelServer());
            if (msg.length() > 0) {
                c.getPlayer().dropMessage(5, msg);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? - ????????????").toString();
        }
    }

    public static class KillMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().getCharactersThreadsafe().stream().filter((map) -> (map != null && !map.isGM())).map((map) -> {
                map.getStat().setHp((short) 0, map);
                return map;
            }).map((map) -> {
                map.getStat().setMp((short) 0, map);
                return map;
            }).map((map) -> {
                map.updateSingleStat(MapleStat.HP, 0);
                return map;
            }).forEachOrdered((map) -> {
                map.updateSingleStat(MapleStat.MP, 0);
            });
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!killmap - ??????????????????").toString();
        }
    }

//    public static class Disease extends CommandExecute {
//
//        @Override
//        public boolean execute(MapleClient c, String splitted[]) {
//            if (splitted.length < 3) {
//                //   c.getPlayer().dropMessage(6, "");
//                return false;
//            }
//            int type;
//            MapleDisease dis;
//            if (splitted[1].equalsIgnoreCase("SEAL")) {
//                type = 120;
//            } else if (splitted[1].equalsIgnoreCase("DARKNESS")) {
//                type = 121;
//            } else if (splitted[1].equalsIgnoreCase("WEAKEN")) {
//                type = 122;
//            } else if (splitted[1].equalsIgnoreCase("STUN")) {
//                type = 123;
//            } else if (splitted[1].equalsIgnoreCase("CURSE")) {
//                type = 124;
//            } else if (splitted[1].equalsIgnoreCase("POISON")) {
//                type = 125;
//            } else if (splitted[1].equalsIgnoreCase("SLOW")) {
//                type = 126;
//            } else if (splitted[1].equalsIgnoreCase("SEDUCE")) {
//                type = 128;
//            } else if (splitted[1].equalsIgnoreCase("REVERSE")) {
//                type = 132;
//            } else if (splitted[1].equalsIgnoreCase("ZOMBIFY")) {
//                type = 133;
//            } else if (splitted[1].equalsIgnoreCase("POTION")) {
//                type = 134;
//            } else if (splitted[1].equalsIgnoreCase("SHADOW")) {
//                type = 135;
//            } else if (splitted[1].equalsIgnoreCase("BLIND")) {
//                type = 136;
//            } else if (splitted[1].equalsIgnoreCase("FREEZE")) {
//                type = 137;
//            } else {
//                return false;
//            }
//            dis = MapleDisease.getByMobSkill(type);
//            if (splitted.length == 4) {
//                MapleCharacter victim;
//                String name = splitted[2];
//                int ch = World.Find.findChannel(name);
//                if (ch <= 0) {
//                    c.getPlayer().dropMessage(6, "??????????????????");
//                    return true;
//                }
//                victim = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);
//
//                if (victim == null) {
//                    c.getPlayer().dropMessage(5, "??????????????????");
//                } else {
//                    victim.setChair(0);
//                    victim.getClient().sendPacket(MaplePacketCreator.cancelChair(-1));
//                    victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(c.getPlayer().getId(), 0), false);
//                    victim.getDiseaseBuff(dis, MobSkillFactory.getMobSkill(type, CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1)));
//                }
//            } else {
//                for (MapleCharacter victim : c.getPlayer().getMap().getCharactersThreadsafe()) {
//                    victim.setChair(0);
//                    victim.getClient().sendPacket(MaplePacketCreator.cancelChair(-1));
//                    victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(c.getPlayer().getId(), 0), false);
//                    victim.getDiseaseBuff(dis, MobSkillFactory.getMobSkill(type, CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1)));
//                }
//            }
//            return true;
//        }
//
//        @Override
//        public String getMessage() {
//            return new StringBuilder().append("!disease <SEAL/DARKNESS/WEAKEN/STUN/CURSE/POISON/SLOW/SEDUCE/REVERSE/ZOMBIFY/POTION/SHADOW/BLIND/FREEZE> [????????????] <????????????> - ????????????????????????").toString();
//        }
//
//    }
    public static class SendAllNote extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {

            if (splitted.length >= 1) {
                String text = StringUtil.joinStringFrom(splitted, 1);
                c.getChannelServer().getPlayerStorage().getAllCharactersThreadSafe().forEach((mch) -> {
                    c.getPlayer().sendNote(mch.getName(), text);
                });
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!sendallnote <??????> ??????Note???????????????????????????").toString();
        }
    }

    public static class giveMeso extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            MapleCharacter victim;
            String name = splitted[1];
            int gain = Integer.parseInt(splitted[2]);
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "??????????????????");
                return true;
            }
            victim = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                c.getPlayer().dropMessage(5, "????????? '" + name);
            } else {
                victim.gainMeso(gain, false);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!gainmeso <??????> <??????> - ???????????????").toString();
        }
    }

    public static class MesoEveryone extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            int gain = Integer.parseInt(splitted[1]);
            c.getWorldServer().getChannels().forEach((cserv) -> {
                cserv.getPlayerStorage().getAllCharactersThreadSafe().forEach((mch) -> {
                    mch.gainMeso(gain, true);
                });
            });
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!mesoeveryone <??????> - ?????????????????????").toString();
        }
    }

    public static class CloneMe extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().cloneLook();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!cloneme - ???????????????").toString();
        }
    }

    public static class DisposeClones extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage(6, c.getPlayer().getCloneSize() + "?????????????????????.");
            c.getPlayer().disposeClones();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!disposeclones - ???????????????").toString();
        }
    }

//    public static class Monitor extends CommandExecute {
//
//        @Override
//        public boolean execute(MapleClient c, String splitted[]) {
//            if (splitted.length < 2) {
//                return false;
//            }
//            MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
//            if (target != null) {
//                if (target.getClient().isMonitored()) {
//                    target.getClient().setMonitored(false);
//                    c.getPlayer().dropMessage(5, "Not monitoring " + target.getName() + " anymore.");
//                } else {
//                    target.getClient().setMonitored(true);
//                    c.getPlayer().dropMessage(5, "Monitoring " + target.getName() + ".");
//                }
//            } else {
//                c.getPlayer().dropMessage(5, "??????????????????");
//            }
//            return true;
//        }
//
//        @Override
//        public String getMessage() {
//            return new StringBuilder().append("!monitor <??????> - ??????????????????").toString();
//        }
//    }
    public static class PermWeather extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (c.getPlayer().getMap().getPermanentWeather() > 0) {
                c.getPlayer().getMap().setPermanentWeather(0);
                c.getPlayer().getMap().broadcastMessage(CField.removeMapEffect());
                c.getPlayer().dropMessage(5, "Map weather has been disabled.");
            } else {
                final int weather = CommandProcessorUtil.getOptionalIntArg(splitted, 1, 5120000);
                if (!MapleItemInformationProvider.getInstance().itemExists(weather) || weather / 10000 != 512) {
                    c.getPlayer().dropMessage(5, "Invalid ID.");
                } else {
                    c.getPlayer().getMap().setPermanentWeather(weather);
                    c.getPlayer().getMap().broadcastMessage(CField.startMapEffect("", weather, false));
                    c.getPlayer().dropMessage(5, "Map weather has been enabled.");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!permweather - ????????????").toString();

        }
    }

    public static class Threads extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            String filter = "";
            if (splitted.length > 1) {
                filter = splitted[1];
            }
            for (int i = 0; i < threads.length; i++) {
                String tstring = threads[i].toString();
                if (tstring.toLowerCase().contains(filter.toLowerCase())) {
                    c.getPlayer().dropMessage(6, i + ": " + tstring);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!threads - ??????Threads??????").toString();

        }
    }

    public static class ShowTrace extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            Thread t = threads[Integer.parseInt(splitted[1])];
            c.getPlayer().dropMessage(6, t.toString() + ":");
            for (StackTraceElement elem : t.getStackTrace()) {
                c.getPlayer().dropMessage(6, elem.toString());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!showtrace - show trace info").toString();

        }
    }

    public static class ToggleOffense extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }

            try {
                CheatingOffense co = CheatingOffense.valueOf(splitted[1]);
                co.setEnabled(!co.isEnabled());
            } catch (IllegalArgumentException iae) {
                c.getPlayer().dropMessage(6, "Offense " + splitted[1] + " not found");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!toggleoffense <Offense> - ???????????????CheatOffense").toString();

        }
    }

    public static class toggleDrop extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().toggleDrops();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!toggledrop - ?????????????????????").toString();

        }
    }

    public static class ToggleMegaphone extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            World.toggleMegaphoneMuteState(c.getWorld());
            c.getPlayer().dropMessage(6, "?????????????????? : " + (c.getChannelServer().getMegaphoneMuteState() ? "???" : "???"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!togglemegaphone - ?????????????????????").toString();

        }
    }

    public static class ExpRate extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2 && splitted[2].equalsIgnoreCase("all")) {
                    LoginServer.getWorlds().forEach((w) -> {
                        w.setExpRate(rate);
                    });
                } else {
                    c.getWorldServer().setExpRate(rate);

                }
                c.getPlayer().dropMessage(6, "???????????????????????? " + rate + "x");
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!exprate <??????> - ??????????????????").toString();

        }
    }

    public static class DropRate extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2 && splitted[2].equalsIgnoreCase("all")) {
                    LoginServer.getWorlds().forEach((w) -> {
                        w.setDropRate(rate);
                    });
                } else {
                    c.getWorldServer().setDropRate(rate);
                }
                c.getPlayer().dropMessage(6, "Drop Rate has been changed to " + rate + "x");
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!droprate <??????> - ??????????????????").toString();

        }
    }

    public static class MesoRate extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2 && splitted[2].equalsIgnoreCase("all")) {
                    LoginServer.getWorlds().forEach((w) -> {
                        w.setMesoRate(rate);
                    });
                } else {
                    c.getWorldServer().setMesoRate(rate);
                }
                c.getPlayer().dropMessage(6, "Meso Rate has been changed to " + rate + "x");
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!mesorate <??????> - ??????????????????").toString();

        }
    }

    public static class DCAll extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int range = -1;
            if (splitted.length < 2) {
                return false;
            }
            String input = null;
            try {
                input = splitted[1];
            } catch (Exception ex) {

            }
            switch (splitted[1]) {
                case "m":
                    range = 0;
                    break;
                case "c":
                    range = 1;
                    break;
                case "w":
                default:
                    range = 2;
                    break;
            }
            if (range == -1) {
                range = 1;
            }
            switch (range) {
                case 0:
                    c.getPlayer().getMap().disconnectAll();
                    break;
                case 1:
                    c.getChannelServer().getPlayerStorage().disconnectAll();
                    break;
                case 2:
                    ChannelServer.getAllInstances().forEach((cserv) -> {
                        cserv.getPlayerStorage().disconnectAll(true);
                    }); break;
                default:
                    break;
            }
            String show = "";
            switch (range) {
                case 0:
                    show = "??????";
                    break;
                case 1:
                    show = "??????";
                    break;
                case 2:
                    show = "??????";
                    break;
            }
            String msg = "[GM ??????] GM " + c.getPlayer().getName() + "  DC ??? " + show + "??????";
            World.Broadcast.broadcastGMMessage(CWvsContext.serverMessage(msg));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!dcall [m|c|w] - ??????????????????").toString();

        }
    }

    public static class KillAll extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            boolean withdrop = false;
            if (splitted.length > 1) {
                int mapid = Integer.parseInt(splitted[1]);
                int irange = 9999;
                if (splitted.length <= 2) {
                    range = irange * irange;
                } else {
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                    irange = Integer.parseInt(splitted[2]);
                    range = irange * irange;
                }
                if (splitted.length >= 3) {
                    withdrop = splitted[3].equalsIgnoreCase("true");
                }
            }

            MapleMonster mob;
            if (map == null) {
                c.getPlayer().dropMessage("??????[" + splitted[2] + "] ????????????");
                return true;
            }
            List<MapleMapObject> monsters = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER));
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                map.killMonster(mob, c.getPlayer(), withdrop, false, (byte) 1);
            }

            c.getPlayer().dropMessage("??????????????? " + monsters.size() + " ??????");

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!killall [range] [mapid] - ??????????????????").toString();

        }
    }

    public static class KillMonster extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                if (mob.getId() == Integer.parseInt(splitted[1])) {
                    mob.damage(c.getPlayer(), mob.getHp(), true);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!killmonster <mobid> - ???????????????????????????").toString();

        }
    }

    public static class KillMonsterByOID extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleMap map = c.getPlayer().getMap();
            int targetId = Integer.parseInt(splitted[1]);
            MapleMonster monster = map.getMonsterByOid(targetId);
            if (monster != null) {
                map.killMonster(monster, c.getPlayer(), false, false, (byte) 1);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!killmonsterbyoid <moboid> - ???????????????????????????").toString();

        }
    }

    public static class HitMonsterByOID extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleMap map = c.getPlayer().getMap();
            int targetId = Integer.parseInt(splitted[1]);
            int damage = Integer.parseInt(splitted[2]);
            MapleMonster monster = map.getMonsterByOid(targetId);
            if (monster != null) {
                map.broadcastMessage(MobPacket.damageMonster(targetId, damage));
                monster.damage(c.getPlayer(), damage, false);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!hitmonsterbyoid <moboid> <damage> - ???????????????????????????").toString();

        }
    }

    public static class NPC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int npcId = 0;
            try {
                npcId = Integer.parseInt(splitted[1]);
            } catch (Exception ex) {

            }
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(c.getPlayer().getPosition().y);
                npc.setRx0(c.getPlayer().getPosition().x + 50);
                npc.setRx1(c.getPlayer().getPosition().x - 50);
                npc.setFh(c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
                npc.setCustom(true);
                c.getPlayer().getMap().addMapObject(npc);
                c.getPlayer().getMap().broadcastMessage(NPCPacket.spawnNPC(npc, true));
            } else {
                c.getPlayer().dropMessage(6, "?????????????????????" + npcId + "???Npc");

            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!npc <npcid> - ?????????NPC").toString();
        }
    }

    public static class Spawn extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            int mid = 0;
            try {
                mid = Integer.parseInt(splitted[1]);
            } catch (Exception ex) {

            }
            int num = Math.min(CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1), 500);
            if (num > 1000) {
                num = 1000;
            }
            Long hp = CommandProcessorUtil.getNamedLongArg(splitted, 1, "hp");
            Integer mp = CommandProcessorUtil.getNamedIntArg(splitted, 1, "mp");
            Integer exp = CommandProcessorUtil.getNamedIntArg(splitted, 1, "exp");
            Double php = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "php");
            Double pmp = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "pmp");
            Double pexp = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "pexp");
            MapleMonster onemob;
            try {
                onemob = MapleLifeFactory.getMonster(mid);
            } catch (RuntimeException e) {
                c.getPlayer().dropMessage(5, "??????: " + e.getMessage());
                return true;
            }

            long newhp;
            int newexp, newmp;
            if (hp != null) {
                newhp = hp;
            } else if (php != null) {
                newhp = (long) (onemob.getMobMaxHp() * (php / 100));
            } else {
                newhp = onemob.getMobMaxHp();
            }
            if (mp != null) {
                newmp = mp;
            } else if (pmp != null) {
                newmp = (int) (onemob.getMobMaxMp() * (pmp / 100));
            } else {
                newmp = onemob.getMobMaxMp();
            }
            if (exp != null) {
                newexp = exp;
            } else if (pexp != null) {
                newexp = (int) (onemob.getMobExp() * (pexp / 100));
            } else {
                newexp = onemob.getMobExp();
            }
            if (newhp < 1) {
                newhp = 1;
            }

            final OverrideMonsterStats overrideStats = new OverrideMonsterStats(newhp, onemob.getMobMaxMp(), newexp, false);
            for (int i = 0; i < num; i++) {
                MapleMonster mob = MapleLifeFactory.getMonster(mid);
                mob.setHp(newhp);
                mob.setOverrideStats(overrideStats);
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!spawn <??????ID> <hp|exp|php||pexp = ?> - ????????????").toString();
        }
    }

    public static class WarpPlayersTo extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            try {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                final MapleMap from = c.getPlayer().getMap();
                from.getCharactersThreadsafe().forEach((chr) -> {
                    chr.changeMap(target, target.getPortal(0));
                });
            } catch (Exception e) {
                return false; //assume drunk GM
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!WarpPlayersTo <maipid> ????????????????????????????????????").toString();
        }
    }

    public static class WarpAllHere extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            ChannelServer.getAllInstances().forEach((CS) -> {
                CS.getPlayerStorage().getAllCharactersThreadSafe().stream().map((mch) -> {
                    if (mch.getMapId() != c.getPlayer().getMapId()) {
                        mch.changeMap(c.getPlayer().getMap(), c.getPlayer().getPosition());
                    }
                    return mch;
                }).filter((mch) -> (mch.getClient().getChannel() != c.getPlayer().getClient().getChannel())).forEachOrdered((mch) -> {
                    mch.changeChannel(c.getPlayer().getClient().getChannel());
                });
            });
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!WarpAllHere ??????????????????????????????").toString();
        }
    }

    public static class LOLCastle extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length != 2) {
                return false;
            }
            MapleMap target = c.getChannelServer().getEventSM().getEventManager("lolcastle").getInstance("lolcastle" + splitted[1]).getMapFactory().getMap(990000300, false, false);
            c.getPlayer().changeMap(target, target.getPortal(0));

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!lolcastle level (level = 1-5) - ???????????????").toString();
        }

    }

    public static class StartProfiling extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            CPUSampler sampler = CPUSampler.getInstance();
            sampler.addIncluded("client");
            sampler.addIncluded("constants"); //or should we do Packages.constants etc.?
            sampler.addIncluded("database");
            sampler.addIncluded("handling");
            sampler.addIncluded("provider");
            sampler.addIncluded("scripting");
            sampler.addIncluded("server");
            sampler.addIncluded("tools");
            sampler.start();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!startprofiling ????????????JVM??????").toString();
        }
    }

    public static class StopProfiling extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            CPUSampler sampler = CPUSampler.getInstance();
            try {
                String filename = "odinprofile.txt";
                if (splitted.length > 1) {
                    filename = splitted[1];
                }
                File file = new File(filename);
                if (file.exists()) {
                    c.getPlayer().dropMessage(6, "The entered filename already exists, choose a different one");
                    return true;
                }
                sampler.stop();
                try (FileWriter fw = new FileWriter(file)) {
                    sampler.save(fw, 1, 10);
                }
            } catch (IOException e) {
                System.err.println("Error saving profile" + e);
            }
            sampler.reset();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!stopprofiling <filename> - ????????????JVM????????????????????????").toString();
        }
    }

    public static class ReloadMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            final int mapId = Integer.parseInt(splitted[1]);
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                if (cserv.getMapFactory().isMapLoaded(mapId) && cserv.getMapFactory().getMap(mapId).getCharactersSize() > 0) {
                    c.getPlayer().dropMessage(5, "There exists characters on channel " + cserv.getChannel());
                    return true;
                }
            }
            ChannelServer.getAllInstances().stream().filter((cserv) -> (cserv.getMapFactory().isMapLoaded(mapId))).forEachOrdered((cserv) -> {
                cserv.getMapFactory().removeMap(mapId);
            });
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadmap <maipid> - ??????????????????").toString();
        }
    }

    public static class Respawn extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().respawn(true);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!respawn - ??????????????????").toString();
        }
    }

    public static class ResetMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().resetFully();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!respawn - ??????????????????").toString();
        }
    }

    public static class PNPC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {

            int npcId = Integer.parseInt(splitted[1]);
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                final int xpos = c.getPlayer().getPosition().x;
                final int ypos = c.getPlayer().getPosition().y;
                final int fh = c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId();
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(ypos);
                npc.setRx0(xpos);
                npc.setRx1(xpos);
                npc.setFh(fh);
                npc.setCustom(true);
                try {
                    com.mysql.jdbc.Connection con = (com.mysql.jdbc.Connection) DatabaseConnection.getConnection();
                    try (com.mysql.jdbc.PreparedStatement ps = (com.mysql.jdbc.PreparedStatement) con.prepareStatement("INSERT INTO wz_customlife (dataid, f, hide, fh, cy, rx0, rx1, type, x, y, mid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                        ps.setInt(1, npcId);
                        ps.setInt(2, 0); // 1 = right , 0 = left
                        ps.setInt(3, 0); // 1 = hide, 0 = show
                        ps.setInt(4, fh);
                        ps.setInt(5, ypos);
                        ps.setInt(6, xpos);
                        ps.setInt(7, xpos);
                        ps.setString(8, "n");
                        ps.setInt(9, xpos);
                        ps.setInt(10, ypos);
                        ps.setInt(11, c.getPlayer().getMapId());
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    c.getPlayer().dropMessage(6, "Failed to save NPC to the database");
                }
                c.getWorldServer().getChannels().stream().map((cserv) -> {
                    cserv.getMapFactory().getMap(c.getPlayer().getMapId()).addMapObject(npc);
                    return cserv;
                }).forEachOrdered((cserv) -> {
                    cserv.getMapFactory().getMap(c.getPlayer().getMapId()).broadcastMessage(CField.NPCPacket.spawnNPC(npc, true));
//                    c.getPlayer().getMap().addMapObject(npc);
//                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.spawnNPC(npc, true));
                });
                c.getPlayer().dropMessage(6, "Please do not reload this map or else the NPC will disappear till the next restart.");
            } else {
                c.getPlayer().dropMessage(6, "????????? Npc ");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!pnpc - ????????????NPC").toString();
        }
    }

    public static class autodc extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            ServerConstants.setAutodc(!ServerConstants.getAutodc());
            c.getPlayer().dropMessage("????????????: " + (ServerConstants.getAutodc() ? "??????" : "??????"));
            System.out.println("????????????: " + (ServerConstants.getAutodc() ? "??????" : "??????"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!Autodc - ??????????????????").toString();
        }
    }

    public static class autoban extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            ServerConstants.setAutoban(!ServerConstants.getAutoban());
            c.getPlayer().dropMessage("????????????: " + (ServerConstants.getAutoban() ? "??????" : "??????"));
            System.out.println("????????????: " + (ServerConstants.getAutoban() ? "??????" : "??????"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!Autoban - ??????????????????").toString();
        }
    }

    public static class search extends ???????????? {

    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            switch (splitted.length) {
                case 1:
                    c.getPlayer().dropMessage(6, splitted[0] + ": <NPC> <MOB> <ITEM> <MAP> <SKILL> <QUEST>");
                    break;
                case 2:
                    c.getPlayer().dropMessage(6, "Provide something to search.");
                    break;
                default:
                    String type = splitted[1];
                    String search = StringUtil.joinStringFrom(splitted, 2);
                    MapleData data = null;
                    MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/" + "String.wz"));
                    c.getPlayer().dropMessage(6, "<<Type: " + type + " | Search: " + search + ">>");
                    if (type.equalsIgnoreCase("NPC")) {
                        List<String> retNpcs = new ArrayList<String>();
                        data = dataProvider.getData("Npc.img");
                        List<Pair<Integer, String>> npcPairList = new LinkedList<Pair<Integer, String>>();
                        for (MapleData npcIdData : data.getChildren()) {
                            npcPairList.add(new Pair<Integer, String>(Integer.parseInt(npcIdData.getName()), MapleDataTool.getString(npcIdData.getChildByPath("name"), "NO-NAME")));
                        }
                        npcPairList.stream().filter((npcPair) -> (npcPair.getRight().toLowerCase().contains(search.toLowerCase()))).forEachOrdered((npcPair) -> {
                            retNpcs.add(npcPair.getLeft() + " - " + npcPair.getRight());
                        });
                        if (retNpcs != null && retNpcs.size() > 0) {
                            retNpcs.forEach((singleRetNpc) -> {
                                c.getPlayer().dropMessage(6, singleRetNpc);
                            });
                        } else {
                            c.getPlayer().dropMessage(6, "No NPC's Found");
                        }
                        
                    } else if (type.equalsIgnoreCase("MAP")) {
                        List<String> retMaps = new ArrayList<String>();
                        data = dataProvider.getData("Map.img");
                        List<Pair<Integer, String>> mapPairList = new LinkedList<Pair<Integer, String>>();
                        for (MapleData mapAreaData : data.getChildren()) {
                            mapAreaData.getChildren().forEach((mapIdData) -> {
                                mapPairList.add(new Pair<Integer, String>(Integer.parseInt(mapIdData.getName()), MapleDataTool.getString(mapIdData.getChildByPath("streetName"), "NO-NAME") + " - " + MapleDataTool.getString(mapIdData.getChildByPath("mapName"), "NO-NAME")));
                            });
                        }
                        mapPairList.stream().filter((mapPair) -> (mapPair.getRight().toLowerCase().contains(search.toLowerCase()))).forEachOrdered((mapPair) -> {
                            retMaps.add(mapPair.getLeft() + " - " + mapPair.getRight());
                        });
                        if (retMaps != null && retMaps.size() > 0) {
                            retMaps.forEach((singleRetMap) -> {
                                c.getPlayer().dropMessage(6, singleRetMap);
                            });
                        } else {
                            c.getPlayer().dropMessage(6, "No Maps Found");
                        }
                    } else if (type.equalsIgnoreCase("MOB")) {
                        List<String> retMobs = new ArrayList<String>();
                        data = dataProvider.getData("Mob.img");
                        List<Pair<Integer, String>> mobPairList = new LinkedList<Pair<Integer, String>>();
                        for (MapleData mobIdData : data.getChildren()) {
                            mobPairList.add(new Pair<Integer, String>(Integer.parseInt(mobIdData.getName()), MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME")));
                        }
                        mobPairList.stream().filter((mobPair) -> (mobPair.getRight().toLowerCase().contains(search.toLowerCase()))).forEachOrdered((mobPair) -> {
                            retMobs.add(mobPair.getLeft() + " - " + mobPair.getRight());
                        });
                        if (retMobs != null && retMobs.size() > 0) {
                            retMobs.forEach((singleRetMob) -> {
                                c.getPlayer().dropMessage(6, singleRetMob);
                            });
                        } else {
                            c.getPlayer().dropMessage(6, "No Mobs Found");
                        }
                        
                    } else if (type.equalsIgnoreCase("ITEM")) {
                        List<String> retItems = new ArrayList<String>();
                        MapleItemInformationProvider.getInstance().getAllItems().stream().filter((itemPair) -> (itemPair != null && itemPair.name != null && itemPair.name.toLowerCase().contains(search.toLowerCase()))).forEachOrdered((itemPair) -> {
                            retItems.add(itemPair.itemId + " - " + itemPair.name);
                        });
                        if (retItems != null && retItems.size() > 0) {
                            retItems.forEach((singleRetItem) -> {
                                c.getPlayer().dropMessage(6, singleRetItem);
                            });
                        } else {
                            c.getPlayer().dropMessage(6, "No Items Found");
                        }
                    } else if (type.equalsIgnoreCase("QUEST")) {
                        List<String> retItems = new ArrayList<String>();
                        MapleQuest.getAllInstances().stream().filter((itemPair) -> (itemPair.getName().length() > 0 && itemPair.getName().toLowerCase().contains(search.toLowerCase()))).forEachOrdered((itemPair) -> {
                            retItems.add(itemPair.getId() + " - " + itemPair.getName());
                        });
                        if (retItems != null && retItems.size() > 0) {
                            retItems.forEach((singleRetItem) -> {
                                c.getPlayer().dropMessage(6, singleRetItem);
                            });
                        } else {
                            c.getPlayer().dropMessage(6, "No Quests Found");
                        }
                    } else if (type.equalsIgnoreCase("SKILL")) {
                        List<String> retSkills = new ArrayList<String>();
                        SkillFactory.getAllSkills().stream().filter((skil) -> (skil.getName() != null && skil.getName().toLowerCase().contains(search.toLowerCase()))).forEachOrdered((skil) -> {
                            retSkills.add(skil.getId() + " - " + skil.getName());
                        });
                        if (retSkills != null && retSkills.size() > 0) {
                            retSkills.forEach((singleRetSkill) -> {
                                c.getPlayer().dropMessage(6, singleRetSkill);
                            });
                        } else {
                            c.getPlayer().dropMessage(6, "No Skills Found");
                        }
                    } else {
                        c.getPlayer().dropMessage(6, "Sorry, that search call is unavailable");
                    }   break;
            }
            //   return 0;
            // }
//            c.removeClickedNPC();
//            NPCScriptManager.getInstance().start(c, 9010000, "AdvancedSearch");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? - ????????????????????????").toString();
        }
    }

    public static class Packet extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            int packetheader = Integer.parseInt(splitted[1]);
            String packet_in = " 00 00 00 00 00 00 00 00 00 ";
            if (splitted.length > 2) {
                packet_in = StringUtil.joinStringFrom(splitted, 2);
            }

            mplew.writeShort(packetheader);
            mplew.write(HexTool.getByteArrayFromHexString(packet_in));
            mplew.writeZeroBytes(20);
            c.getSession().write(mplew.getPacket());
            c.getPlayer().dropMessage(packetheader + "???????????????[" + packetheader + "] : " + mplew.toString());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!Packet - <????????????>").toString();
        }
    }

    public static class UpdateMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                return false;
            }
            boolean custMap = splitted.length >= 2;
            int mapid = custMap ? Integer.parseInt(splitted[1]) : player.getMapId();
            MapleMap map = custMap ? player.getClient().getChannelServer().getMapFactory().getMap(mapid) : player.getMap();
            if (player.getClient().getChannelServer().getMapFactory().destroyMap(mapid)) {
                MapleMap newMap = player.getClient().getChannelServer().getMapFactory().getMap(mapid);
                MaplePortal newPor = newMap.getPortal(0);
                LinkedHashSet<MapleCharacter> mcs = new LinkedHashSet<>(map.getCharacters()); // do NOT remove, fixing ConcurrentModificationEx.
                outerLoop:
                for (MapleCharacter m : mcs) {
                    for (int x = 0; x < 5; x++) {
                        try {
                            m.changeMap(newMap, newPor);
                            continue outerLoop;
                        } catch (Throwable t) {
                        }
                    }
                    player.dropMessage("???????????? " + m.getName() + " ??????????????????. ????????????...");
                }
                player.dropMessage("??????????????????.");
                return true;
            }
            player.dropMessage("??????????????????!");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!UpdateMap <mapid> - ??????????????????").toString();
        }
    }

    public static class maxmeso extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().gainMeso(Integer.MAX_VALUE - c.getPlayer().getMeso(), true);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!maxmeso - ?????????").toString();
        }
    }

    public static class mesos extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            int meso = 0;
            try {
                meso = Integer.parseInt(splitted[1]);
            } catch (Exception ex) {
            }
            c.getPlayer().gainMeso(meso, true);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!mesos <???????????????> - ????????????").toString();
        }
    }

    public static class Drop extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            int itemId = 0;
            String name = null;
            try {
                itemId = Integer.parseInt(splitted[1]);
                name = splitted[3];
            } catch (Exception ex) {
            }

            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (GameConstants.isPet(itemId)) {
                c.getPlayer().dropMessage(5, "??????????????????????????????.");
            } else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " - ???????????????");
            } else {
                Item toDrop;
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    toDrop = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                } else {
                    toDrop = new client.inventory.Item(itemId, (byte) 0, (short) quantity, (byte) 0);
                }
                toDrop.setOwner(c.getPlayer().getName());
                toDrop.setGMLog(c.getPlayer().getName());
                if (name != null) {
                    int ch = World.Find.findChannel(name);
                    if (ch > 0) {
                        MapleCharacter victim = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);
                        if (victim != null) {
                            victim.getMap().spawnItemDrop(victim, victim, toDrop, victim.getPosition(), true, true);
                        }
                    } else {
                        c.getPlayer().dropMessage("??????: [" + name + "] ???????????????");
                    }
                } else {
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!Drop <??????ID> - ????????????").toString();
        }
    }

    public static class ProDrop extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                return false;
            }
            int itemId = 0;
            int quantity = 1;
            int Str = 0;
            int Dex = 0;
            int Int = 0;
            int Luk = 0;
            int HP = 0;
            int MP = 0;
            int Watk = 0;
            int Matk = 0;
            int Wdef = 0;
            int Mdef = 0;
            int Scroll = 0;
            int Upg = 0;
            int Acc = 0;
            int Avoid = 0;
            int jump = 0;
            int speed = 0;
            int day = 0;
            try {
                int splitted_count = 1;
                itemId = Integer.parseInt(splitted[splitted_count++]);
                Str = Integer.parseInt(splitted[splitted_count++]);
                Dex = Integer.parseInt(splitted[splitted_count++]);
                Int = Integer.parseInt(splitted[splitted_count++]);
                Luk = Integer.parseInt(splitted[splitted_count++]);
                HP = Integer.parseInt(splitted[splitted_count++]);
                MP = Integer.parseInt(splitted[splitted_count++]);
                Watk = Integer.parseInt(splitted[splitted_count++]);
                Matk = Integer.parseInt(splitted[splitted_count++]);
                Wdef = Integer.parseInt(splitted[splitted_count++]);
                Mdef = Integer.parseInt(splitted[splitted_count++]);
                Upg = Integer.parseInt(splitted[splitted_count++]);
                Acc = Integer.parseInt(splitted[splitted_count++]);
                Avoid = Integer.parseInt(splitted[splitted_count++]);
                speed = Integer.parseInt(splitted[splitted_count++]);
                jump = Integer.parseInt(splitted[splitted_count++]);
                Scroll = Integer.parseInt(splitted[splitted_count++]);
                day = Integer.parseInt(splitted[splitted_count++]);
            } catch (Exception ex) {
                //   ex.printStackTrace();
            }
            boolean Str_check = Str != 0;
            boolean Int_check = Int != 0;
            boolean Dex_check = Dex != 0;
            boolean Luk_check = Luk != 0;
            boolean HP_check = HP != 0;
            boolean MP_check = MP != 0;
            boolean WATK_check = Watk != 0;
            boolean MATK_check = Matk != 0;
            boolean WDEF_check = Wdef != 0;
            boolean MDEF_check = Mdef != 0;
            boolean SCROLL_check = true;
            boolean UPG_check = Upg != 0;
            boolean ACC_check = Acc != 0;
            boolean AVOID_check = Avoid != 0;
            boolean JUMP_check = jump != 0;
            boolean SPEED_check = speed != 0;
            boolean DAY_check = day != 0;
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (GameConstants.isPet(itemId)) {
                c.getPlayer().dropMessage(5, "????????????????????????.");
                return true;
            } else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " ?????????");
                return true;
            }
            Item toDrop;
            Equip equip;
            if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {// ?????????????????????
                equip = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                equip.setGMLog(c.getPlayer().getName() + " ?????? !Prodrop");
                if (Str_check) {
                    equip.setStr((short) Str);
                }
                if (Luk_check) {
                    equip.setLuk((short) Luk);
                }
                if (Dex_check) {
                    equip.setDex((short) Dex);
                }
                if (Int_check) {
                    equip.setInt((short) Int);
                }
                if (HP_check) {
                    equip.setHp((short) HP);
                }
                if (MP_check) {
                    equip.setMp((short) MP);
                }
                if (WATK_check) {
                    equip.setWatk((short) Watk);
                }
                if (MATK_check) {
                    equip.setMatk((short) Matk);
                }
                if (WDEF_check) {
                    equip.setWdef((short) Wdef);
                }
                if (MDEF_check) {
                    equip.setMdef((short) Mdef);
                }
                if (ACC_check) {
                    equip.setAcc((short) Acc);
                }
                if (AVOID_check) {
                    equip.setAvoid((short) Avoid);
                }
                if (SCROLL_check) {
                    equip.setUpgradeSlots((byte) Scroll);
                }
                if (UPG_check) {
                    equip.setLevel((byte) Upg);
                }
                if (JUMP_check) {
                    equip.setJump((short) jump);
                }
                if (SPEED_check) {
                    equip.setSpeed((short) speed);
                }
                if (DAY_check) {
                    equip.setExpiration(System.currentTimeMillis() + (day * 24 * 60 * 60 * 1000));
                }
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), equip, c.getPlayer().getPosition(), true, true);
            } else {
                toDrop = new client.inventory.Item(itemId, (byte) 0, (short) quantity, (byte) 0);
                toDrop.setGMLog(c.getPlayer().getName() + " ?????? !Prodrop");
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!ProDrop <????????????> (<??????> <??????> <??????> <??????> <HP> <MP> <??????> <??????> <??????> <??????> <??????+x> <??????> <??????> <??????> <??????> <?????????> <??????>)").toString();
        }
    }

    public static class ????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 4) {
                return false;
            }
            String error = null;
            String input = splitted[1];
            String name = splitted[2];
            int nx = 0;
            int gain = 0;
            try {
                switch (input) {
                    case "??????":
                        nx = 1;
                        break;
                    case "??????":
                        nx = 2;
                        break;
                    default:
                        error = "?????????????????????[??????]???[??????] ??????[" + input + "]";
                        break;
                }
                gain = Integer.parseInt(splitted[3]);
            } catch (Exception ex) {
                error = "???????????????????????????????????????2147483647??? " + input + " ?????????: " + ex.toString();
            }
            if (error != null) {
                c.getPlayer().dropMessage(error);
                return true;
            }

            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage("??????????????????");
                return true;
            }
            MapleCharacter victim = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                c.getPlayer().dropMessage("??????????????????");
            } else {
                c.getPlayer().dropMessage("??????????????????[" + name + "] " + input + " " + gain);
                FileoutputUtil.logToFile("logs/Data/?????????.txt", "\r\n " + FileoutputUtil.NowTime() + " GM " + c.getPlayer().getName() + " ?????? " + victim.getName() + " " + input + " " + gain + "???");
                victim.modifyCSPoints(nx, gain, true);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!????????? ??????/?????? ???????????? ??????").toString();
        }
    }

    public static class ResetMobs extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().killAllMonsters(false);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!resetmobs - ???????????????????????????").toString();
        }
    }

    public static class ??????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MaplePortal portal = c.getPlayer().getMap().findClosestPortal(c.getPlayer().getTruePosition());
            c.getPlayer().dropMessage(-11, portal.getName() + " id: " + portal.getId() + " script: " + portal.getScriptName());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??????????????? - ????????????????????????").toString();
        }
    }

    public static class setRate extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            MapleCharacter mc = player;
            if (splitted.length > 3) {
                String input = splitted[1];
                int arg = Integer.parseInt(splitted[2]);
                int mins = Integer.parseInt(splitted[3]);
                boolean bOk = true;
                if (input.equals("??????")) {
                    c.getWorldServer().setExExpRate(arg);
                    c.getWorldServer().getChannels().forEach((cservs) -> {
                        cservs.broadcastPacket(CWvsContext.serverMessage("????????????????????????????????? " + arg + "???????????????????????????.?????????????????????????????????????????????"));
                    });
                } else if (input.equals("??????")) {
                    c.getWorldServer().setExDropRate(arg);
                    c.getWorldServer().getChannels().forEach((cservs) -> {
                        cservs.broadcastPacket(CWvsContext.serverMessage("????????????????????????????????? " + arg + "???????????????????????????.?????????????????????????????????????????????"));
                    });
                } else if (input.equals("??????")) {
                    c.getWorldServer().setExMesoRate(arg);
                    c.getWorldServer().getChannels().forEach((cservs) -> {
                        cservs.broadcastPacket(CWvsContext.serverMessage("??????????????????????????? " + arg + "???????????????????????????.?????????????????????????????????????????????"));
                    });
                } else {
                    bOk = false;
                }
                if (bOk) {
                    c.getWorldServer().scheduleRateDelay(input, mins);
                } else {
                    return false;
                }
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!setRate <??????/??????/??????> <??????> <?????????> - ??????????????????(????????????)").toString();
        }
    }

    public static class setExRate extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            String input = splitted[1];
            int arg = Integer.parseInt(splitted[2]);
            if (input.equals("??????")) {
                c.getWorldServer().setExExpRate(arg);
                c.getWorldServer().getChannels().forEach((cservs) -> {
                    cservs.broadcastPacket(CWvsContext.serverMessage("????????????????????????????????? " + arg + "???????????????????????????.?????????????????????????????????????????????"));
                });
            } else if (input.equals("??????")) {
                c.getWorldServer().setExDropRate(arg);
                c.getWorldServer().getChannels().forEach((cservs) -> {
                    cservs.broadcastPacket(CWvsContext.serverMessage("????????????????????????????????? " + arg + "???????????????????????????.?????????????????????????????????????????????"));
                });
            } else if (input.equals("??????")) {
                c.getWorldServer().setExMesoRate(arg);
                c.getWorldServer().getChannels().forEach((cservs) -> {
                    cservs.broadcastPacket(CWvsContext.serverMessage("??????????????????????????? " + arg + "???????????????????????????.?????????????????????????????????????????????"));
                });
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!setExRate <??????/??????/??????> <??????> - ????????????????????????").toString();
        }
    }

}
