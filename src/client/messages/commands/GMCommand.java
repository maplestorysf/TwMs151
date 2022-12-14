package client.messages.commands;

import client.MapleCharacter;
import constants.ServerConstants.PlayerGMRank;
import client.MapleClient;
import client.MapleStat;
import client.Skill;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import client.inventory.OnlyID;
import client.messages.CommandProcessorUtil;
import constants.GameConstants;
import constants.ServerConstants;
import database.DatabaseConnection;
import handling.RecvPacketOpcode;
import handling.SendPacketOpcode;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.World;
import handling.world.family.MapleFamily;
import handling.world.guild.MapleGuild;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import scripting.NPCScriptManager;
import scripting.PortalScriptManager;
import scripting.ReactorScriptManager;
import server.CashItemFactory;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleShopFactory;
import server.ShutdownServer;
import server.Timer.EventTimer;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterInformationProvider;
import server.life.MapleNPC;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleReactor;
import server.maps.MapleReactorFactory;
import server.maps.MapleReactorStats;
import server.quest.MapleQuest;
import tools.ArrayMap;
import tools.FileoutputUtil;
import tools.StringUtil;
import tools.Triple;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.GuildPacket;

public class GMCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.?????????;
    }

//    public static class ?????????????????? extends CommandExecute {
//
//        @Override
//        public boolean execute(MapleClient c, String splitted[]) {
//            if (splitted.length < 3) {
//                return false;
//            }
//            int input = 0, Change = 0, sn = 0;
//            try {
//                input = Integer.parseInt(splitted[1]);
//                Change = (input - 1);
//                sn = Integer.parseInt(splitted[2]);
//            } catch (Exception ex) {
//                return false;
//            }
//            if (input < 1 || input > 5) {
//                c.getPlayer().dropMessage("??????????????????1~5?????????");
//                return true;
//            }
//            ServerConstants.hot_sell[Change] = sn;
//            c.getPlayer().dropMessage("?????????????????????" + input + "??????????????????SN??? " + sn + " ?????????");
//            return true;
//        }
//
//        @Override
//        public String getMessage() {
//            return new StringBuilder().append("!?????????????????? <???X???????????????> <????????????SN> - ??????????????????????????????").toString();
//        }
//    }
    public static class SaveAll extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int p = 0;
            for (ChannelServer cserv : c.getWorldServer().getChannels()) {
                List<MapleCharacter> chrs = cserv.getPlayerStorage().getAllCharactersThreadSafe();
                for (MapleCharacter chr : chrs) {
                    p++;
                    chr.saveToDB(false, false);
                }
            }
            c.getPlayer().dropMessage("[??????] " + p + "?????????????????????????????????.");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!saveall - ????????????????????????").toString();
        }
    }

    public static class LowHP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getStat().setHp((short) 1, c.getPlayer());
            c.getPlayer().getStat().setMp((short) 1, c.getPlayer());
            c.getPlayer().updateSingleStat(MapleStat.HP, 1);
            c.getPlayer().updateSingleStat(MapleStat.MP, 1);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!lowhp - ????????????").toString();
        }
    }

    public static class MyPos extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            Point pos = c.getPlayer().getPosition();
            c.getPlayer().dropMessage(6, "X: " + pos.x + " | Y: " + pos.y + " | RX0: " + (pos.x + 50) + " | RX1: " + (pos.x - 50) + " | CY:" + pos.y);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!mypos - ????????????").toString();
        }
    }

    public static class Notice extends CommandExecute {

        private static int getNoticeType(String typestring) {
            switch (typestring) {
                case "n":
                    return 0;
                case "p":
                    return 1;
                case "l":
                    return 2;
                case "nv":
                    return 5;
                case "v":
                    return 5;
                case "b":
                    return 6;
            }
            return -1;
        }

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int joinmod = 1;
            int range = -1;
            if (splitted.length < 2) {
                return false;
            }
            switch (splitted[1]) {
                case "m":
                    range = 0;
                    break;
                case "c":
                    range = 1;
                    break;
                case "w":
                    range = 2;
                    break;
            }

            int tfrom = 2;
            if (range == -1) {
                range = 2;
                tfrom = 1;
            }
            if (splitted.length < tfrom + 1) {
                return false;
            }
            int type = getNoticeType(splitted[tfrom]);
            if (type == -1) {
                type = 0;
                joinmod = 0;
            }
            StringBuilder sb = new StringBuilder();
            if (splitted[tfrom].equals("nv")) {
                sb.append("[??????]");
            } else {
                sb.append("");
            }
            joinmod += tfrom;
            if (splitted.length < joinmod + 1) {
                return false;
            }
            sb.append(StringUtil.joinStringFrom(splitted, joinmod));

            byte[] packet = CWvsContext.serverNotice(type, sb.toString());
            switch (range) {
                case 0:
                    c.getPlayer().getMap().broadcastMessage(packet);
                    break;
                case 1:
                    ChannelServer.getInstance(c.getWorld(), c.getChannel()).broadcastPacket(packet);
                    break;
                case 2:
                    World.Broadcast.broadcastMessage(c.getWorld(), packet);
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!notice <n|p|l|nv|v|b> <m|c|w> <message> - ??????").toString();
        }
    }

    public static class Yellow extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            int range = -1;
            switch (splitted[1]) {
                case "m":
                    range = 0;
                    break;
                case "c":
                    range = 1;
                    break;
                case "w":
                    range = 2;
                    break;
            }
            if (range == -1) {
                range = 2;
            }
            byte[] packet = CWvsContext.yellowChat((splitted[0].equals("!y") ? ("[" + c.getPlayer().getName() + "] ") : "") + StringUtil.joinStringFrom(splitted, 2));
            switch (range) {
                case 0:
                    c.getPlayer().getMap().broadcastMessage(packet);
                    break;
                case 1:
                    ChannelServer.getInstance(c.getWorld(), c.getChannel()).broadcastPacket(packet);
                    break;
                case 2:
                    World.Broadcast.broadcastMessage(c.getWorld(), packet);
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!yellow <m|c|w> <message> - ????????????").toString();
        }
    }

    public static class Y extends Yellow {
    }

    public static class NpcNotice extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length <= 2) {
                return false;
            }
            int npcid = Integer.parseInt(splitted[1]);
            String msg = splitted[2];
            MapleNPC npc = MapleLifeFactory.getNPC(npcid);
            if (npc == null || !npc.getName().equals("MISSINGNO")) {
                c.getPlayer().dropMessage(6, "????????? Npc ");
                return true;
            }
            World.Broadcast.broadcastMessage(CField.NPCPacket.getNPCTalk(npcid, (byte) 0, msg, "00 00", (byte) 0));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!NpcNotice <npcid> <message> - ???NPC?????????").toString();
        }
    }

    public static class opennpc extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            int npcid = 0;
            try {
                npcid = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException asd) {
            }
            MapleNPC npc = MapleLifeFactory.getNPC(npcid);
            if (npc != null && !npc.getName().equalsIgnoreCase("MISSINGNO")) {
                NPCScriptManager.getInstance().start(c, npcid);
            } else {
                c.getPlayer().dropMessage(6, "??????NPC");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!openNpc <NPC??????> - ??????NPC").toString();
        }
    }

    public static class ????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            String after = splitted[1];
            if (after.length() <= 12) {
                c.getPlayer().setName(splitted[1]);
                c.getPlayer().fakeRelog();
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!????????? 	????????? - ???????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length != 2) {
                return false;
            }
            com.mysql.jdbc.Connection dcon = (com.mysql.jdbc.Connection) DatabaseConnection.getConnection();
            try {
                com.mysql.jdbc.PreparedStatement ps = (com.mysql.jdbc.PreparedStatement) dcon.prepareStatement("SELECT guildid FROM guilds WHERE name = ?");
                ps.setString(1, splitted[1]);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    if (c.getPlayer().getGuildId() > 0) {
                        try {
                            World.Guild.leaveGuild(c.getPlayer().getMGC());
                        } catch (Exception e) {
                            c.sendPacket(CWvsContext.getErrorNotice("??????????????????????????????????????????????????????"));
                            return false;
                        }
                        c.sendPacket(GuildPacket.showGuildInfo(null));

                        c.getPlayer().setGuildId(0);
                        c.getPlayer().saveGuildStatus();
                    }
                    c.getPlayer().setGuildId(rs.getInt("guildid"));
                    c.getPlayer().setGuildRank((byte) 2); // ?????????
                    try {
                        World.Guild.addGuildMember(c.getPlayer().getMGC());
                    } catch (Exception e) {
                    }
                    c.sendPacket(GuildPacket.showGuildInfo(c.getPlayer()));
                    c.getPlayer().getMap().broadcastMessage(c.getPlayer(), CField.removePlayerFromMap(c.getPlayer().getId()), false);
                    c.getPlayer().getMap().broadcastMessage(c.getPlayer(), CField.spawnPlayerMapobject(c.getPlayer()), false);
                    c.getPlayer().saveGuildStatus();
                } else {
                    c.getPlayer().dropMessage(6, "????????????????????????");
                }
                rs.close();
                ps.close();
            } catch (SQLException e) {
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? 	???????????? - ??????????????????").toString();
        }
    }

    public static class ?????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim;
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "??????????????????");
                return true;
            }
            victim = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                c.getPlayer().dropMessage(6, "??????????????????");
                return true;
            }
            victim.setMarriageId(0);
            victim.reloadC();
            victim.dropMessage(5, "???????????????");
            victim.saveToDB(false, false);
            c.getPlayer().dropMessage(6, victim.getName() + "???????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!?????? <????????????> - ??????").toString();
        }
    }

    public static class CancelBuffs extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().cancelAllBuffs();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!CancelBuffs - ????????????BUFF").toString();
        }
    }

    public static class RemoveNPCs extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().resetNPCs();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!removenpcs - ????????????NPC").toString();
        }
    }

    public static class LookNPCs extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().getAllNPCsThreadsafe().stream().map((reactor1l) -> (MapleNPC) reactor1l).forEachOrdered((reactor2l) -> {
                c.getPlayer().dropMessage(5, "NPC: oID: " + reactor2l.getObjectId() + " npcID: " + reactor2l.getId() + " Position: " + reactor2l.getPosition().toString() + " Name: " + reactor2l.getName());
            });
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!looknpcs - ????????????NPC").toString();
        }
    }

    public static class LookReactors extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().getAllReactorsThreadsafe().stream().map((reactor1l) -> (MapleReactor) reactor1l).forEachOrdered((reactor2l) -> {
                c.getPlayer().dropMessage(5, "Reactor: oID: " + reactor2l.getObjectId() + " reactorID: " + reactor2l.getReactorId() + " Position: " + reactor2l.getPosition().toString() + " State: " + reactor2l.getState() + " Name: " + reactor2l.getName());
            });
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!lookreactors - ?????????????????????").toString();
        }
    }

    public static class LookPortals extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().getPortals().forEach((portal) -> {
                c.getPlayer().dropMessage(5, "Portal: ID: " + portal.getId() + " script: " + portal.getScriptName() + " name: " + portal.getName() + " pos: " + portal.getPosition().x + "," + portal.getPosition().y + " target: " + portal.getTargetMapId() + " / " + portal.getTarget());
            });
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!lookportals - ?????????????????????").toString();
        }
    }

    public static class GoTo extends CommandExecute {

        private static final HashMap<String, Integer> gotomaps = new HashMap<>();

        static {
            gotomaps.put("gmmap", 180000000);
            gotomaps.put("southperry", 2000000);
            gotomaps.put("amherst", 1010000);
            gotomaps.put("henesys", 100000000);
            gotomaps.put("ellinia", 101000000);
            gotomaps.put("perion", 102000000);
            gotomaps.put("kerning", 103000000);
            gotomaps.put("lithharbour", 104000000);
            gotomaps.put("sleepywood", 105040300);
            gotomaps.put("florina", 110000000);
            gotomaps.put("orbis", 200000000);
            gotomaps.put("happyville", 209000000);
            gotomaps.put("elnath", 211000000);
            gotomaps.put("ludibrium", 220000000);
            gotomaps.put("aquaroad", 230000000);
            gotomaps.put("leafre", 240000000);
            gotomaps.put("mulung", 250000000);
            gotomaps.put("herbtown", 251000000);
            gotomaps.put("omegasector", 221000000);
            gotomaps.put("koreanfolktown", 222000000);
            gotomaps.put("newleafcity", 600000000);
            gotomaps.put("sharenian", 990000000);
            gotomaps.put("pianus", 230040420);
            gotomaps.put("horntail", 240060200);
            gotomaps.put("chorntail", 240060201);
            gotomaps.put("mushmom", 100000005);
            gotomaps.put("griffey", 240020101);
            gotomaps.put("manon", 240020401);
            gotomaps.put("zakum", 280030000);
            gotomaps.put("czakum", 280030001);
            gotomaps.put("papulatus", 220080001);
            gotomaps.put("showatown", 801000000);
            gotomaps.put("zipangu", 800000000);
            gotomaps.put("ariant", 260000100);
            gotomaps.put("nautilus", 120000000);
            gotomaps.put("boatquay", 541000000);
            gotomaps.put("malaysia", 550000000);
            gotomaps.put("taiwan", 740000000);
            gotomaps.put("thailand", 500000000);
            gotomaps.put("erev", 130000000);
            gotomaps.put("ellinforest", 300000000);
            gotomaps.put("kampung", 551000000);
            gotomaps.put("singapore", 540000000);
            gotomaps.put("amoria", 680000000);
            gotomaps.put("timetemple", 270000000);
            gotomaps.put("pinkbean", 270050100);
            gotomaps.put("peachblossom", 700000000);
            gotomaps.put("fm", 910000000);
            gotomaps.put("freemarket", 910000000);
            gotomaps.put("oxquiz", 109020001);
            gotomaps.put("ola", 109030101);
            gotomaps.put("fitness", 109040000);
            gotomaps.put("snowball", 109060000);
            gotomaps.put("cashmap", 741010200);
            gotomaps.put("golden", 950100000);
            gotomaps.put("phantom", 610010000);
            gotomaps.put("cwk", 610030000);
            gotomaps.put("rien", 140000000);
        }

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "????????????: !goto <????????????>");
            } else if (gotomaps.containsKey(splitted[1])) {
                MapleMap target = c.getChannelServer().getMapFactory().getMap(gotomaps.get(splitted[1]));
                MaplePortal targetPortal = target.getPortal(0);
                c.getPlayer().changeMap(target, targetPortal);
            } else if (splitted[1].equals("?????????")) {
                c.getPlayer().dropMessage(6, "?????? !goto <?????????>. ?????????????????????:");
                StringBuilder sb = new StringBuilder();
                gotomaps.keySet().forEach((s) -> {
                    sb.append(s).append(", ");
                });
                c.getPlayer().dropMessage(6, sb.substring(0, sb.length() - 2));
            } else {
                c.getPlayer().dropMessage(6, "????????????????????? - ?????? !goto <?????????>. ???????????????????????????, ???????????? !goto ?????????????????????.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!goto <??????> - ???????????????").toString();

        }
    }

    public static class cleardrops extends RemoveDrops {

    }

    public static class RemoveDrops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage(5, "????????? " + c.getPlayer().getMap().getNumItems() + " ????????????");
            c.getPlayer().getMap().removeDrops();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!removedrops - ?????????????????????").toString();

        }
    }

    public static class NearestPortal extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MaplePortal portal = c.getPlayer().getMap().findClosestSpawnpoint(c.getPlayer().getPosition());
            c.getPlayer().dropMessage(6, portal.getName() + " id: " + portal.getId() + " script: " + portal.getScriptName());

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!nearestportal - ????????????").toString();

        }
    }

    public static class SpawnDebug extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage(6, c.getPlayer().getMap().spawnDebug());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!spawndebug - debug????????????").toString();

        }
    }

    public static class Speak extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim;
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "??????????????????");
                return true;
            }
            victim = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);

            if (victim == null) {
                c.getPlayer().dropMessage(5, "????????? '" + splitted[1]);
                return false;
            } else {
                victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 2), victim.isGM(), 0));
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!speak <????????????> <??????> - ????????????????????????").toString();
        }
    }

    public static class SpeakMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().getCharactersThreadsafe().stream().filter((victim) -> (victim.getId() != c.getPlayer().getId())).forEachOrdered((victim) -> {
                victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
            });
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!speakmap <??????> - ?????????????????????????????????").toString();
        }

    }

    public static class SpeakChannel extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getChannelServer().getPlayerStorage().getAllCharactersThreadSafe().stream().filter((victim) -> (victim.getId() != c.getPlayer().getId())).forEachOrdered((victim) -> {
                victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
            });
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!speakchannel <??????> - ?????????????????????????????????").toString();
        }

    }

    public static class SpeakWorld extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            ChannelServer.getAllInstances().forEach((cserv) -> {
                cserv.getPlayerStorage().getAllCharactersThreadSafe().stream().filter((victim) -> (victim.getId() != c.getPlayer().getId())).forEachOrdered((victim) -> {
                    victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), 0));
                });
            });
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!speakchannel <??????> - ????????????????????????????????????").toString();
        }
    }

//    public static class SpeakMega extends CommandExecute {
//
//        @Override
//        public boolean execute(MapleClient c, String splitted[]) {
//            MapleCharacter victim = null;
//            if (splitted.length >= 2) {
//                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
//            }
//            try {
//                
//                World.Broadcast.broadcastSmega(MaplePacketCreator.getSuperMegaphone(victim == null ? splitted[1] : victim.getName() + " : " + StringUtil.joinStringFrom(splitted, 2), true, victim == null ? c.getChannel() : victim.getClient().getChannel()));
//            } catch (Exception e) {
//                return false;
//            }
//            return true;
//        }
//
//        @Override
//        public String getMessage() {
//            return new StringBuilder().append("!speakmega [????????????] <??????> - ????????????????????????????????????").toString();
//        }
//    }
    public static class Say extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                sb.append(c.getPlayer().getName());
                sb.append("] ");
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                World.Broadcast.broadcastMessage(CWvsContext.getItemNotice(sb.toString()));
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!say ?????? - ???????????????").toString();
        }
    }

    public static class Shutdown extends CommandExecute {

        private static Thread t = null;

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage(6, "??????????????????...");
            if (t == null || !t.isAlive()) {
                t = new Thread(server.ShutdownServer.getInstance());
                t.start();
            } else {
                c.getPlayer().dropMessage(6, "???????????????...");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!shutdown - ???????????????").toString();
        }
    }

    public static class ShutdownTime extends CommandExecute {

        private static ScheduledFuture<?> ts = null;
        private int minutesLeft = 0;
        private static Thread t = null;

        @Override
        public boolean execute(MapleClient c, String splitted[]) {

            if (splitted.length < 2) {
                return false;
            }
            minutesLeft = Integer.parseInt(splitted[1]);
            c.getPlayer().dropMessage(6, "??????????????? " + minutesLeft + "????????????????????????????????????????????????????????????.");
            c.getPlayer().dropMessage(6, "??????????????????????????????");
            if (ts == null && (t == null || !t.isAlive())) {
                t = new Thread(ShutdownServer.getInstance());
                ts = EventTimer.getInstance().register(new Runnable() {

                    @Override
                    public void run() {
                        if (minutesLeft == 0) {
                            ShutdownServer.getInstance().run();
                            t.start();
                            ts.cancel(false);
                            return;
                        }
                        StringBuilder message = new StringBuilder();
                        message.append("[???????????????] ??????????????? ");
                        message.append(minutesLeft);
                        message.append(" ???????????????????????????????????????????????????????????????");
                        World.Broadcast.broadcastMessage(CWvsContext.getItemNotice(message.toString()));
                        World.Broadcast.broadcastMessage(CWvsContext.serverMessage(message.toString()));
                        ChannelServer.getAllInstances().forEach((cs) -> {
                            cs.setServerMessage("??????????????? " + minutesLeft + " ???????????????");
                        });
                        System.out.println("??????????????? " + minutesLeft + " ???????????????");
                        minutesLeft--;
                    }
                }, 60000);
            } else {
                c.getPlayer().dropMessage(6, new StringBuilder().append("?????????????????????????????? ").append(minutesLeft).append("????????????????????????????????????").toString());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!shutdowntime <?????????> - ???????????????").toString();
        }
    }

    public static class UnbanIP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            byte ret_ = MapleClient.unbanIP(splitted[1]);
            if (ret_ == -2) {
                c.getPlayer().dropMessage(6, "[unbanip] SQL ??????.");
            } else if (ret_ == -1) {
                c.getPlayer().dropMessage(6, "[unbanip] ???????????????.");
            } else if (ret_ == 0) {
                c.getPlayer().dropMessage(6, "[unbanip] No IP or Mac with that character exists!");
            } else if (ret_ == 1) {
                c.getPlayer().dropMessage(6, "[unbanip] IP???Mac?????????????????????.");
            } else if (ret_ == 2) {
                c.getPlayer().dropMessage(6, "[unbanip] IP??????Mac???????????????.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!unbanip <????????????> - ????????????").toString();
        }
    }

    public static class TempBan extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter victim;
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                return false;
            }
            victim = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);
            final int reason = Integer.parseInt(splitted[2]);
            final int numDay = Integer.parseInt(splitted[3]);

            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, numDay);
            final DateFormat df = DateFormat.getInstance();

            if (victim == null) {
                c.getPlayer().dropMessage(6, "[tempban] ?????????????????????");

            } else {
                victim.tempban("???" + c.getPlayer().getName() + "???????????????", cal, reason, true);
                c.getPlayer().dropMessage(6, "[tempban] " + splitted[1] + " ??????????????????????????? " + df.format(cal.getTime()));
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!tempban <????????????> - ??????????????????").toString();
        }
    }

    public static class copyAll extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            MapleCharacter victim;
            if (splitted.length < 2) {
                return false;
            }
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "??????????????????");
                return true;
            }
            victim = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                player.dropMessage("??????????????????");
                return true;
            }
            MapleInventory equipped = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED);
            MapleInventory equip = c.getPlayer().getInventory(MapleInventoryType.EQUIP);
            List<Short> ids = new LinkedList<>();
            equipped.list().forEach((item) -> {
                ids.add(item.getPosition());
            });
            ids.forEach((id) -> {
                MapleInventoryManipulator.unequip(c, id, equip.getNextFreeSlot());
            });
            c.getPlayer().clearSkills();
            c.getPlayer().setStr(victim.getStr());
            c.getPlayer().setDex(victim.getDex());
            c.getPlayer().setInt(victim.getInt());
            c.getPlayer().setLuk(victim.getLuk());

            c.getPlayer().setMeso(victim.getMeso());
            c.getPlayer().setLevel((short) (victim.getLevel()));
            c.getPlayer().changeJob(victim.getJob());

            c.getPlayer().setHp(victim.getHp());
            c.getPlayer().setMp(victim.getMp());
            c.getPlayer().setMaxHp(victim.getMaxHp());
            c.getPlayer().setMaxMp(victim.getMaxMp());

            String normal = victim.getName();
            String after = (normal + "x2");
            if (after.length() <= 12) {
                c.getPlayer().setName(victim.getName() + "x2");
            }
            c.getPlayer().setRemainingAp(victim.getRemainingAp());
            c.getPlayer().setRemainingSp(victim.getRemainingSp());
            c.getPlayer().LearnSameSkill(victim);

            c.getPlayer().setFame(victim.getFame());
            c.getPlayer().setHair(victim.getHair());
            c.getPlayer().setFace(victim.getFace());

            c.getPlayer().setSkinColor(victim.getSkinColor() == 0 ? c.getPlayer().getSkinColor() : victim.getSkinColor());

            c.getPlayer().setGender(victim.getGender());

            victim.getInventory(MapleInventoryType.EQUIPPED).list().stream().map((ii) -> ii.copy()).map((eq) -> {
                eq.setPosition(eq.getPosition());
                return eq;
            }).map((eq) -> {
                eq.setQuantity((short) 1);
                return eq;
            }).map((eq) -> {
                eq.setEquipOnlyId(-1);
                return eq;
            }).forEachOrdered((eq) -> {
                c.getPlayer().forceReAddItem_NoUpdate(eq, MapleInventoryType.EQUIPPED);
            });
            c.getPlayer().fakeRelog();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!copyall ???????????? - ????????????").toString();
        }
    }

    public static class copyInv extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            MapleCharacter victim;
            int type = 1;
            if (splitted.length < 2) {
                return false;
            }

            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "??????????????????");
                return false;
            }
            victim = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                player.dropMessage("??????????????????");
                return true;
            }
            try {
                type = Integer.parseInt(splitted[2]);
            } catch (Exception ex) {
            }
            if (type == 0) {
                victim.getInventory(MapleInventoryType.EQUIPPED).list().stream().map((ii) -> ii.copy()).forEachOrdered((n) -> {
                    player.getInventory(MapleInventoryType.EQUIP).addItem(n);
                });
                player.fakeRelog();
            } else {
                MapleInventoryType types;
                switch (type) {
                    case 1:
                        types = MapleInventoryType.EQUIP;
                        break;
                    case 2:
                        types = MapleInventoryType.USE;
                        break;
                    case 3:
                        types = MapleInventoryType.ETC;
                        break;
                    case 4:
                        types = MapleInventoryType.SETUP;
                        break;
                    case 5:
                        types = MapleInventoryType.CASH;
                        break;
                    default:
                        types = null;
                        break;
                }
                if (types == null) {
                    c.getPlayer().dropMessage("????????????");
                    return true;
                }
                int[] equip = new int[97];
                for (int i = 1; i < 97; i++) {
                    if (victim.getInventory(types).getItem((short) i) != null) {
                        equip[i] = i;
                    }
                }
                for (int i = 0; i < equip.length; i++) {
                    if (equip[i] != 0) {
                        Item n = victim.getInventory(types).getItem((short) equip[i]).copy();
                        n.setEquipOnlyId(-1);
                        player.getInventory(types).addItem(n);
                        c.getSession().write(CWvsContext.InventoryPacket.addInventorySlot(types, n));
                    }
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!copyinv ???????????? ????????????(0 = ????????? 1=????????? 2=????????? 3=????????? 4=????????? 5=?????????)(???????????????) - ??????????????????").toString();
        }
    }

    public static class Clock extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().getMap().broadcastMessage(CField.getClock(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 60)));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!clock <time> ??????").toString();
        }
    }

    public static class Song extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().getMap().broadcastMessage(CField.musicChange(splitted[1]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!song - ????????????").toString();
        }
    }

    public static class Kill extends CommandExecute {

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
                c.getPlayer().dropMessage(6, "??????????????????");
                return true;
            }
            victim = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);
            if (victim == null) {
                c.getPlayer().dropMessage(6, "[kill] ?????? " + name + " ?????????.");
            } else if (player.allowedToTarget(victim)) {
                victim.getStat().setHp((short) 0, victim);
                victim.getStat().setMp((short) 0, victim);
                victim.updateSingleStat(MapleStat.HP, 0);
                victim.updateSingleStat(MapleStat.MP, 0);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!kill <????????????> - ????????????").toString();
        }
    }

    public static class ReloadOps extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            SendPacketOpcode.reloadValues();
            RecvPacketOpcode.reloadValues();
            c.getPlayer().dropMessage(6, "??????????????????????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadops - ????????????OpCode").toString();
        }
    }

    public static class ReloadDrops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleMonsterInformationProvider.getInstance().clearDrops();
            ReactorScriptManager.getInstance().clearDrops();
            c.getPlayer().dropMessage(6, "?????????????????????????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloaddrops - ??????????????????").toString();
        }
    }

    public static class ReloadPortals extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            PortalScriptManager.getInstance().clearScripts();
            c.getPlayer().dropMessage(6, "???????????????????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadportals - ?????????????????????").toString();
        }
    }

    public static class ReloadShops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleShopFactory.getInstance().clear();
            c.getPlayer().dropMessage(6, "NPC?????????????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadshops - ??????????????????").toString();
        }
    }

    public static class ReloadEvents extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            ChannelServer.getAllInstances().forEach((instance) -> {
                instance.reloadEvents();
            });
            c.getPlayer().dropMessage(6, "?????????????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!reloadevents - ????????????????????????").toString();
        }
    }

    public static class Reloadall extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            ChannelServer.getAllInstances().forEach((instance) -> {
                instance.reloadEvents();
            });
            MapleShopFactory.getInstance().clear();
            PortalScriptManager.getInstance().clearScripts();
            // MapleItemInformationProvider.getInstance().load();

            CashItemFactory.getInstance().initialize();
            MapleMonsterInformationProvider.getInstance().clearDrops();

            MapleGuild.loadAll(); //(this); 
            MapleFamily.loadAll(); //(this); 
            MapleLifeFactory.loadQuestCounts();
            MapleQuest.initQuests();
            MapleOxQuizFactory.getInstance();
            ReactorScriptManager.getInstance().clearDrops();
            SendPacketOpcode.reloadValues();
            RecvPacketOpcode.reloadValues();
            c.getPlayer().dropMessage(6, "???????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!Reloadall - ??????????????????").toString();
        }
    }

    public static class skill extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            Skill skill = SkillFactory.getSkill(Integer.parseInt(splitted[1]));
            byte level = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            byte masterlevel = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);
            if (level > skill.getMaxLevel()) {
                level = (byte) skill.getMaxLevel();
            }
            c.getPlayer().changeSkillLevel(skill, level, masterlevel);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!skill <??????ID> [????????????] [??????????????????] ...  - ????????????").toString();
        }
    }

    public static class GiveSkill extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            MapleCharacter victim;
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                return false;
            }
            victim = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);

            Skill skill = SkillFactory.getSkill(Integer.parseInt(splitted[2]));
            byte level = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);
            byte masterlevel = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 4, 1);

            if (level > skill.getMaxLevel()) {
                level = (byte) skill.getMaxLevel();
            }
            victim.changeSkillLevel(skill, level, masterlevel);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!giveskill <????????????> <??????ID> [????????????] [??????????????????] - ????????????").toString();
        }
    }

    public static class MaxSkillsByJob extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().maxSkillsByJob();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!MaxSkillsByJob - ??????????????????").toString();
        }
    }

    public static class MaxSkills extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().maxSkillsByJob();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!MaxSkills - ????????????").toString();
        }
    }

    public static class ClearSkills extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().clearSkills();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!ClearSkills - ????????????").toString();
        }
    }

    public static class SP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().setRemainingSp(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLESP, 0);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!sp [??????] - ??????SP").toString();
        }
    }

    public static class AP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().setRemainingAp((short) CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1));
            Map<MapleStat, Integer> statupdate = new EnumMap<>(MapleStat.class);
            statupdate.put(MapleStat.AVAILABLEAP, (int) c.getPlayer().getRemainingAp());
            c.getSession().write(CWvsContext.updatePlayerStats(statupdate, true, c.getPlayer()));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!ap [??????] - ??????AP").toString();
        }
    }

    public static class Shop extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleShopFactory shop = MapleShopFactory.getInstance();
            int shopId = 0;
            try {
                shopId = Integer.parseInt(splitted[1]);
            } catch (Exception ex) {
            }
            if (shop.getShop(shopId) != null) {
                shop.getShop(shopId).sendShop(c);
            } else {
                c.getPlayer().dropMessage(5, "?????????ID[" + shopId + "]?????????");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!shop - ????????????").toString();
        }
    }

    public static class ???????????? extends CommandExecute {

        protected static ScheduledFuture<?> ts = null;

        @Override
        public boolean execute(final MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            if (ts != null) {
                ts.cancel(false);
                c.getPlayer().dropMessage(0, "??????????????????????????????");
            }
            int minutesLeft;
            try {
                minutesLeft = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException ex) {
                return false;
            }
            if (minutesLeft > 0) {
                ts = EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        ChannelServer.getAllInstances().forEach((cserv) -> {
                            cserv.getPlayerStorage().getAllCharactersThreadSafe().stream().filter((mch) -> (mch.getLevel() >= 29 && !mch.isGM())).forEachOrdered((mch) -> {
                                NPCScriptManager.getInstance().start(mch.getClient(), 9010010, "CrucialTime");
                            });
                        });
                        World.Broadcast.broadcastMessage(CWvsContext.getItemNotice("??????????????????????????????30????????????????????????????????????"));
                        ts.cancel(false);
                        ts = null;
                    }
                }, minutesLeft * 60000); // ?????????
                c.getPlayer().dropMessage(0, "???????????????????????????");
            } else {
                c.getPlayer().dropMessage(0, "????????????????????? > 0???");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!???????????? <??????:??????> - ????????????").toString();
        }
    }

    public static class UnlockInv extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            java.util.Map<Item, MapleInventoryType> eqs = new ArrayMap<>();
            boolean add = false;
            if (splitted.length < 2 || splitted[1].equals("??????")) {
                for (MapleInventoryType type : MapleInventoryType.values()) {
                    for (Item item : c.getPlayer().getInventory(type)) {
                        if (ItemFlag.LOCK.check(item.getFlag())) {
                            item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                            add = true;
                            c.getPlayer().reloadC();
                            c.getPlayer().dropMessage(5, "????????????");
                            //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                        }
                        if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                            item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                            add = true;
                            c.getPlayer().reloadC();
                            c.getPlayer().dropMessage(5, "????????????");
                            //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                        }
                        if (add) {
                            eqs.put(item, type);
                        }
                        add = false;
                    }
                }
            } else if (splitted[1].equals("???????????????")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIPPED)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.EQUIP);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("??????")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIP)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.EQUIP);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("??????")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.USE)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.USE);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("??????")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.SETUP)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.SETUP);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("??????")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.ETC);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("??????")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "????????????");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.CASH);
                    }
                    add = false;
                }
            } else {
                return false;
            }

            eqs.entrySet().forEach((eq) -> {
                c.getPlayer().forceReAddItem_NoUpdate(eq.getKey().copy(), eq.getValue());
            });
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!unlockinv <??????/???????????????/??????/??????/??????/??????/??????> - ????????????").toString();
        }
    }

    public static class Letter extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "????????????: ");
                return false;
            }
            int start, nstart;
            if (splitted[1].equalsIgnoreCase("green")) {
                start = 3991026;
                nstart = 3990019;
            } else if (splitted[1].equalsIgnoreCase("red")) {
                start = 3991000;
                nstart = 3990009;
            } else {
                c.getPlayer().dropMessage(6, "???????????????!");
                return true;
            }
            String splitString = StringUtil.joinStringFrom(splitted, 2);
            List<Integer> chars = new ArrayList<>();
            splitString = splitString.toUpperCase();
            // System.out.println(splitString);
            for (int i = 0; i < splitString.length(); i++) {
                char chr = splitString.charAt(i);
                if (chr == ' ') {
                    chars.add(-1);
                } else if ((int) (chr) >= (int) 'A' && (int) (chr) <= (int) 'Z') {
                    chars.add((int) (chr));
                } else if ((int) (chr) >= (int) '0' && (int) (chr) <= (int) ('9')) {
                    chars.add((int) (chr) + 200);
                }
            }
            final int w = 32;
            int dStart = c.getPlayer().getPosition().x - (splitString.length() / 2 * w);
            for (Integer i : chars) {
                if (i == -1) {
                    dStart += w;
                } else if (i < 200) {
                    int val = start + i - (int) ('A');
                    client.inventory.Item item = new client.inventory.Item(val, (byte) 0, (short) 1);
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false);
                    dStart += w;
                } else if (i >= 200 && i <= 300) {
                    int val = nstart + i - (int) ('0') - 200;
                    client.inventory.Item item = new client.inventory.Item(val, (byte) 0, (short) 1);
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false);
                    dStart += w;
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(" !letter <color (green/red)> <word> - ??????").toString();
        }

    }

    public static class Marry extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            int itemId = Integer.parseInt(splitted[2]);
            if (!GameConstants.isEffectRing(itemId)) {
                c.getPlayer().dropMessage(6, "???????????????ID.");
            } else {
                MapleCharacter fff;
                String name = splitted[1];
                int ch = World.Find.findChannel(name);
                if (ch <= 0) {
                    c.getPlayer().dropMessage(6, "??????????????????");
                    return false;
                }
                fff = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);
                if (fff == null) {
                    c.getPlayer().dropMessage(6, "??????????????????");
                } else {
                    int[] ringID = {MapleInventoryIdentifier.getInstance(), MapleInventoryIdentifier.getInstance()};
                    try {
                        MapleCharacter[] chrz = {fff, c.getPlayer()};
                        for (int i = 0; i < chrz.length; i++) {
                            Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(itemId);
                            if (eq == null) {
                                c.getPlayer().dropMessage(6, "???????????????ID.");
                                return true;
                            } else {
                                eq.setUniqueId(ringID[i]);
                                MapleInventoryManipulator.addbyItem(chrz[i].getClient(), eq.copy());
                                chrz[i].dropMessage(6, "?????????  " + chrz[i == 0 ? 1 : 0].getName() + " ??????");
                            }
                        }
                        MapleRing.addToDB(itemId, c.getPlayer(), fff.getName(), fff.getId(), ringID);
                    } catch (SQLException e) {
                    }
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!marry <????????????> <????????????> - ??????").toString();
        }
    }

    public static class KillID extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter victim;
            int id = 0;
            try {
                id = Integer.parseInt(splitted[1]);
            } catch (Exception ex) {

            }
            int ch = World.Find.findChannel(id);
            if (ch <= 0) {
                return false;
            }
            victim = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterById(id);
            if (victim == null) {
                c.getPlayer().dropMessage(6, "[kill] ??????ID " + id + " ?????????.");
            } else if (player.allowedToTarget(victim)) {
                victim.getStat().setHp((short) 0, victim);
                victim.getStat().setMp((short) 0, victim);
                victim.updateSingleStat(MapleStat.HP, 0);
                victim.updateSingleStat(MapleStat.MP, 0);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!KillID <??????ID> - ????????????").toString();
        }
    }

    public static class autoreg extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            ServerConstants.setAR(!ServerConstants.getAR());
            c.getPlayer().dropMessage(0, "[autoreg] " + (ServerConstants.getAR() ? "??????" : "??????"));
            System.out.println("[autoreg] " + (ServerConstants.getAR() ? "??????" : "??????"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!autoreg  - ??????????????????").toString();
        }
    }

    public static class logindoor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            LoginServer.setAdminOnly(!LoginServer.isAdminOnly());
            c.getPlayer().dropMessage(0, "[logindoor] " + (LoginServer.isAdminOnly() ? "??????" : "??????"));
            System.out.println("[logindoor] " + (LoginServer.isAdminOnly() ? "??????" : "??????"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!logindoor  - ???????????????????????????").toString();
        }
    }

    public static class LevelUp extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                c.getPlayer().levelUp();
            } else {
                int up = 0;
                try {
                    up = Integer.parseInt(splitted[1]);
                } catch (Exception ex) {

                }
                for (int i = 0; i < up; i++) {
                    c.getPlayer().levelUp();
                }
            }
            c.getPlayer().setExp(0);
            c.getPlayer().updateSingleStat(MapleStat.EXP, 0);
//            if (c.getPlayer().getLevel() < 200) {
//                c.getPlayer().gainExp(GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) + 1, true, false, true);
//            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!levelup - ????????????").toString();
        }
    }

    public static class FakeRelog extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleCharacter player = c.getPlayer();
            c.sendPacket(CField.getCharInfo(player));
            player.getMap().removePlayer(player);
            player.getMap().addPlayer(player);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!fakerelog - ??????????????????").toString();

        }
    }

    public static class SpawnReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleReactorStats reactorSt = MapleReactorFactory.getReactor(Integer.parseInt(splitted[1]));
            MapleReactor reactor = new MapleReactor(reactorSt, Integer.parseInt(splitted[1]));
            reactor.setDelay(-1);
            reactor.setPosition(c.getPlayer().getPosition());
            c.getPlayer().getMap().spawnReactor(reactor);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!spawnreactor - ??????Reactor").toString();

        }
    }

    public static class HReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().getMap().getReactorByOid(Integer.parseInt(splitted[1])).hitReactor(c);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!hitreactor - ??????Reactor").toString();

        }
    }

    public static class DestroyReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleMap map = c.getPlayer().getMap();
            List<MapleMapObject> reactors = map.getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.REACTOR));
            if (splitted[1].equals("all")) {
                reactors.stream().map((reactorL) -> (MapleReactor) reactorL).forEachOrdered((reactor2l) -> {
                    c.getPlayer().getMap().destroyReactor(reactor2l.getObjectId());
                });
            } else {
                c.getPlayer().getMap().destroyReactor(Integer.parseInt(splitted[1]));
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!drstroyreactor - ??????Reactor").toString();

        }
    }

    public static class ResetReactors extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().resetReactors();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!resetreactors - ????????????????????????Reactor").toString();

        }
    }

    public static class SetReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().getMap().setReactorState(Byte.parseByte(splitted[1]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!hitreactor - ??????Reactor").toString();

        }
    }

    public static class ResetQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forfeit(c.getPlayer());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!resetquest <??????ID> - ????????????").toString();

        }
    }

    public static class StartQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).start(c.getPlayer(), Integer.parseInt(splitted[2]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!startquest <??????ID> - ????????????").toString();

        }
    }

    public static class CompleteQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).complete(c.getPlayer(), Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!completequest <??????ID> - ????????????").toString();

        }
    }

    public static class FStartQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forceStart(c.getPlayer(), Integer.parseInt(splitted[2]), splitted.length >= 4 ? splitted[3] : null);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!fstartquest <??????ID> - ??????????????????").toString();

        }
    }

    public static class FCompleteQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleQuest.getInstance(Integer.parseInt(splitted[1])).forceComplete(c.getPlayer(), Integer.parseInt(splitted[2]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!fcompletequest <??????ID> - ??????????????????").toString();

        }
    }

    public static class FStartOther extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {

            MapleQuest.getInstance(Integer.parseInt(splitted[2])).forceStart(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]), Integer.parseInt(splitted[3]), splitted.length >= 4 ? splitted[4] : null);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!fstartother - ????????????").toString();

        }
    }

    public static class FCompleteOther extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            MapleQuest.getInstance(Integer.parseInt(splitted[2])).forceComplete(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]), Integer.parseInt(splitted[3]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!fcompleteother - ????????????").toString();

        }
    }

    public static class log extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            boolean next = false;
            boolean Action = false;
            String LogType = null;
            String[] Log = {"??????", "??????", "??????", "??????", "????????????", "??????", "??????", "??????"};
            StringBuilder show_log = new StringBuilder();
            for (String s : Log) {
                show_log.append(s);
                show_log.append(" / ");
            }
            if (splitted.length < 3) {
                c.getPlayer().dropMessage("??????Log??????: " + show_log.toString());
                return false;
            }
            if (!splitted[1].contains("???") && !splitted[1].contains("???")) {
                return false;
            }
            if (splitted[1].contains("???") && splitted[1].contains("???")) {
                c.getPlayer().dropMessage("?????????????????????????????????????????????????");
                return true;
            }

            for (int i = 0; i < Log.length; i++) {
                if (splitted[2].contains(Log[i])) {
                    next = true;
                    LogType = Log[i];
                    break;
                }
            }
            Action = splitted[1].contains("???");
            if (!next) {
                c.getPlayer().dropMessage("??????Log??????: " + show_log.toString());
                return true;
            }

            switch (LogType) {
                case "??????":
                    ServerConstants.LOG_MEGA = Action;
                    break;
                case "??????":
                    ServerConstants.LOG_DAMAGE = Action;
                    break;
                case "??????":
                    ServerConstants.LOG_CHAT = Action;
                    break;
                case "??????":
                    ServerConstants.LOG_CSBUY = Action;
                    break;
                case "????????????":
                    ServerConstants.LOG_MERCHANT = Action;
                    break;
                case "??????":
                    ServerConstants.LOG_CHALKBOARD = Action;
                    break;
                case "??????":
                    ServerConstants.LOG_SCROLL = Action;
                    break;
                case "??????":
                    ServerConstants.LOG_DC = Action;
                    break;
            }
            String msg = "[GM ??????] ?????????[" + c.getPlayer().getName() + "] " + splitted[1] + "???" + LogType + "???Log";
            World.Broadcast.broadcastGMMessage(CWvsContext.getItemNotice(msg));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!log ???/??? Log????????????").toString();
        }

    }

    public static class RemoveItem extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            MapleCharacter chr;
            String name = splitted[1];
            int id = Integer.parseInt(splitted[2]);
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "??????????????????");
                return true;
            }
            chr = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);

            if (chr == null) {
                c.getPlayer().dropMessage(6, "?????????????????????");
            } else {
                chr.removeAll(id, false);
                c.getPlayer().dropMessage(6, "??????ID??? " + id + " ?????????????????? " + name + " ??????????????????");
            }
            return true;

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!RemoveItem <????????????> <??????ID> - ???????????????????????????").toString();
        }
    }

    public static class RemoveItemOff extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3) {
                return false;
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                int item = Integer.parseInt(splitted[1]);
                String name = splitted[2];
                int id = 0, quantity = 0;
                List<Long> inventoryitemid = new LinkedList();
                boolean isEquip = GameConstants.isEquip(item);

                if (MapleCharacter.getCharacterIdByName(c.getWorld(), name) == -1) {
                    c.getPlayer().dropMessage(5, "???????????????????????????");
                    return true;
                } else {
                    id = MapleCharacter.getCharacterIdByName(c.getWorld(), name);
                }

                PreparedStatement ps = con.prepareStatement("select inventoryitemid, quantity from inventoryitems WHERE itemid = ? and characterid = ?");
                ps.setInt(1, item);
                ps.setInt(2, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        if (isEquip) {
                            long Equipid = rs.getLong("inventoryitemid");
                            if (Equipid != 0) {
                                inventoryitemid.add(Equipid);
                            }
                            quantity++;
                        } else {
                            quantity += rs.getInt("quantity");
                        }
                    }
                }
                if (quantity == 0) {
                    c.getPlayer().dropMessage(5, "??????[" + name + "]????????????[" + item + "]????????????");
                    return true;
                }

                if (isEquip) {
                    StringBuilder Sql = new StringBuilder();
                    Sql.append("Delete from inventoryequipment WHERE inventoryitemid = ");
                    for (int i = 0; i < inventoryitemid.size(); i++) {
                        Sql.append(inventoryitemid.get(i));
                        if (i < (inventoryitemid.size() - 1)) {
                            Sql.append(" OR inventoryitemid = ");
                        }
                    }
                    ps = con.prepareStatement(Sql.toString());
                    ps.executeUpdate();
                }

                ps = con.prepareStatement("Delete from inventoryitems WHERE itemid = ? and characterid = ?");
                ps.setInt(1, item);
                ps.setInt(2, id);
                ps.executeUpdate();
                ps.close();

                c.getPlayer().dropMessage(6, "????????? " + name + " ???????????????????????? ID[" + item + "] ??????x" + quantity);
                return true;
            } catch (SQLException e) {
                return true;
            }
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!RemoveItemOff <??????ID> <????????????> - ???????????????????????????").toString();
        }
    }

    public static class ??????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage("????????????????????????....");
            OnlyID.getInstance().StartCheckings();
            c.getPlayer().dropMessage("????????????????????????");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??????????????? - ???????????????").toString();
        }
    }

    public static class ??????????????? extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            List<Triple<Integer, Long, Long>> OnlyIDList = OnlyID.getData();
            if (OnlyIDList.isEmpty()) {
                c.getPlayer().dropMessage("????????????????????????????????????????????? !??????????????? ???????????????");
                return true;
            } else {
                c.getPlayer().dropMessage(6, "??????????????????????????????...");
            }
            try {
                final ListIterator<Triple<Integer, Long, Long>> OnlyId = OnlyIDList.listIterator();
                while (OnlyId.hasNext()) {
                    Triple<Integer, Long, Long> Only = OnlyId.next();
                    int chr = Only.getLeft();
                    long invetoryitemid = Only.getMid();
                    long equiponlyid = Only.getRight();
                    int ch = World.Find.findChannel(chr);
                    if (ch < 0 && ch != -10) {
                        HandleOffline(c, chr, invetoryitemid, equiponlyid);
                    } else {
                        MapleCharacter chrs = null;
                        if (ch == -10) {
                            chrs = CashShopServer.getPlayerStorage().getCharacterById(chr);
                        } else {
                            chrs = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterById(chr);
                        }
                        if (chrs == null) {
                            break;
                        }
                        MapleInventoryManipulator.removeAllByEquipOnlyId(chrs.getClient(), invetoryitemid);
                    }
                }
                OnlyID.clearData();
            } catch (Exception ex) {
                String output = FileoutputUtil.NowTime();
                FileoutputUtil.outputFileError(FileoutputUtil.CommandEx_Log, ex);
                FileoutputUtil.logToFile(FileoutputUtil.CommandEx_Log, output + " \r\n");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!??????????????? - ???????????????").toString();
        }

        public void HandleOffline(MapleClient c, int chr, long inventoryitemid, long equiponlyid) {
            try {
                String itemname = "null";
                Connection con = DatabaseConnection.getConnection();

                try (PreparedStatement ps = con.prepareStatement("select itemid from inventoryitems WHERE inventoryitemid = ?")) {
                    ps.setLong(1, inventoryitemid);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int itemid = rs.getInt("itemid");
                            itemname = MapleItemInformationProvider.getInstance().getName(itemid);
                        } else {
                            c.getPlayer().dropMessage("????????????: ?????????????????????????????????");
                        }
                    }
                }

                try (PreparedStatement ps = con.prepareStatement("Delete from inventoryequipment WHERE inventoryitemid = " + inventoryitemid)) {
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("Delete from inventoryitems WHERE inventoryitemid = ?")) {
                    ps.setLong(1, inventoryitemid);
                    ps.executeUpdate();
                }

                String msgtext = "??????ID: " + chr + " ????????????????????????????????????[" + itemname + "]?????????????????????";
                World.Broadcast.broadcastGMMessage(CWvsContext.getItemNotice("[GM??????] " + msgtext));
                FileoutputUtil.logToFile("logs/Hack/????????????_?????????.txt", msgtext + " ????????????ID: " + equiponlyid + "\r\n");

            } catch (Exception ex) {
                String output = FileoutputUtil.NowTime();
                FileoutputUtil.outputFileError(FileoutputUtil.CommandEx_Log, ex);
                FileoutputUtil.logToFile(FileoutputUtil.CommandEx_Log, output + " \r\n");
            }
        }
    }

}
