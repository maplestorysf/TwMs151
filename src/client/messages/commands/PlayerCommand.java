package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.messages.CommandProcessor;
import constants.GameConstants;
import constants.JQLevels;
import constants.ServerConstants;
import constants.ServerConstants.PlayerGMRank;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.World;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import scripting.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleShopFactory;
import server.life.MapleMonster;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.StringUtil;
import tools.packet.CWvsContext;
import tools.packet.MTSCSPacket;

public class PlayerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.普通玩家;
    }

    private static ResultSet ranking(boolean gm) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;
            if (!gm) {
                ps = con.prepareStatement("SELECT reborns, level, name, job FROM characters WHERE gm < 3 ORDER BY reborns DESC, level DESC LIMIT 10");
            } else {
                ps = con.prepareStatement("SELECT name, gm FROM characters WHERE gm >= 3");
            }
            return ps.executeQuery();
        } catch (SQLException ex) {
        }
        return null;
    }

    private static ResultSet JQranking(boolean gm) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;
            if (!gm) {
                ps = con.prepareStatement("SELECT jqlevel, jqexp, name FROM characters WHERE gm < 3 ORDER BY jqlevel DESC, jqexp DESC LIMIT 10");
            } else {
                ps = con.prepareStatement("SELECT name, gm FROM characters WHERE gm >= 3");
            }
            return ps.executeQuery();
        } catch (SQLException ex) {
        }
        return null;
    }

    public static class listcommands extends commands {
    }

    public static class help extends commands {
    }

    public static class helpmeplz extends commands {
    }

    public static class commands extends CommandExecute { //玩家指令總表

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();

            player.dropNPC(
                    "~" + ServerConstants.SERVER_NAME + " 角色區~ \r\n\r\n"
                    + "#b@sitems#k - #b販售背包物品,#r \r\neq裝備 use消耗 setup裝飾 etc其他 cash商城物品 all全部 .#k\r\n"
                    //  + "#b@str/@dex/@int/@luk#k - #b快速分配能力值點數.#k\r\n"
                    + "#b@save#k - #b儲存角色當前狀態.#k\r\n"
                    + "#b@check#k - #b查詢自身狀態.#k\r\n"
                    + "#b@side#k - #b光暗系統.#k\r\n"
                    + "#b@ap#k - #b重置AP.#k\r\n"
                    + "#b@emo#k - #b自殺.#k\r\n"
                    + "#b@fh#k - #b復活.#k\r\n"
                    + "#b@rb#k - #b轉升.#k\r\n" //未添加
                    + "~ " + ServerConstants.SERVER_NAME + " 傳送區~\r\n\r\n"
                    + "#b@fm#k - #b前往自由市場.#k\r\n"
                    + "#b@home#k - #b前往弓箭手村.#k\r\n"
                    // + "#b@boss#k - #BOSS地圖傳送.#k\r\n" //未添加
                    + "#b@pvp#k - #b前往PVP地圖.#k\r\n"
                    //+ "#b@cc#k - #b快速切換頻道.#k\r\n"
                    //+ "#b@joievent#k - #加入活動.#k\r\n"
                    + "~ " + ServerConstants.SERVER_NAME + " ＮＰＣ區~\r\n\r\n"
                    + "#b@bag#k - #b開啟角色倉庫.#k\r\n"
                    + "#b@shop#k - #b開啟地攤商人.#k\r\n"
                    + "#b@dcash#k - #b丟現金物品.#k\r\n" //未添加
                    + "#b@npc#k - #b開啟萬能NPC.#k\r\n"
                    + "~ " + ServerConstants.SERVER_NAME + " 查詢區~\r\n\r\n"
                    + "#b@bosshp#k - #b查詢BOSS剩餘血量.#k\r\n"
                    + "#b@mobhp#k - #b查詢怪物剩餘血量.#k\r\n"
                    + "#b@rates#k - #b查詢伺服器倍率.#k\r\n"
                    + "#b@pvprank#k - #bPVP排名查詢.#k\r\n"
                    + "#b@online#k - #b查詢在線玩家.#k\r\n"
                    + "#b@cdrop#k - #b查詢怪物掉寶.#k\r\n" //未添加
                    + "~ " + ServerConstants.SERVER_NAME + " 解狀區~\r\n\r\n"
                    //   + "#b@relog#k - #r重新加載地圖,適用於破圖,請謹慎使用.#k\r\n"
                    + "#b@keyfix#k - #b熱鍵重設,將鍵盤配置設為預設值.#k\r\n"
                    + "#b@fix$#k - #b楓幣修復,將異常的楓幣歸零.#k\r\n"
                    //   + "#b@fixplayer#k - #b解除旗下角色異常.#k\r\n"
                    + "#b@partyfix#k - #b隊伍修復.#k\r\n"
                    + "#b@expfix#k - #b經驗值修正.#k\r\n"
                    + "#b@ea#k - #b解除異常狀態.#k\r\n"
                    + "~ " + ServerConstants.SERVER_NAME + " 其他區~\r\n\r\n"
                    + "#b@chalk#k - #b顯示指定文字於黑板上.#k\r\n"
                    + "#b@Boardhelp#k - #b自訂義黑板狀態.#k\r\n"
                    + "#b@cogm#k - #b對管理員發送訊息.#k\r\n"
                    + "#b@AutoLoot#k - #b自動撿物品.#k\r\n"
                    + "#b@MesdDrop#k - #b自動撿楓幣.#k\r\n"
                    + "#b@dps#k - #b攻擊輸出測試.#k\r\n");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@Help - 玩家指令總表").toString();
        }
    }

    public static class pee extends CommandExecute { //尿尿系統

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();

            if (player.getPee() <= 29) {
                player.dropMessage("[身體]: 我不是真的有心情去撒尿。請稍後再試.");
            } else if (player.getMapId() != 809000201 && player.getMapId() != 809000101) {
                player.dropMessage("[身體]: 你瘋了嗎! 我才不要在大庭廣眾下尿尿. 到廁所去尿!");
            } else {
                player.dropMessage("[身體]: 哇,多謝! 感覺更好了!");
                player.setPee(0);
                World.Broadcast.broadcastMessage(player.getWorldId(), CWvsContext.serverNotice(6, player.getName() + " 剛剛去尿尿了!"));
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@pee - 尿尿").toString();
        }
    }

    public static class serveruptime extends uptime { //伺服器總運行時間
    }

    public static class uptime extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getSession().write(CWvsContext.yellowChat(ServerConstants.SERVER_NAME + " + 已經開啟了 " + StringUtil.getReadableMillis(ChannelServer.serverStartTime, System.currentTimeMillis()) + "沒有重啟!"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@uptime - 伺服器總運行時間").toString();
        }
    }

    public static class bag extends storage { //開啟倉庫
    }

    public static class storage extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            player.getStorage().sendStorage(c, 9930100);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@storage - 開啟倉庫").toString();
        }
    }

//    public static class dps extends CommandExecute { //攻擊輸出測試
//
//        @Override
//        public boolean execute(MapleClient c, String[] splitted) {
//            final MapleCharacter player = c.getPlayer();
//            if (!player.isTestingDPS()) {
//                player.toggleTestingDPS();
//                player.dropMessage("請連續發動攻擊並持續15秒,以測試您的攻擊輸出.");
//                final MapleMonster mm = MapleLifeFactory.getMonster(9400410);
//                int distance = ((player.getJobId() >= 300 && player.getJobId() < 413) || (player.getJobId() >= 1300 && player.getJobId() < 1500) || (player.getJobId() >= 520 && player.getJobId() < 600)) ? 125 : 50;
//                Point p = new Point(player.getPosition().x - distance, player.getPosition().y);
//                mm.setBelongTo(player);
//                final long newhp = Long.MAX_VALUE;
//                OverrideMonsterStats overrideStats = new OverrideMonsterStats();
//                overrideStats.setOHp(newhp);
//                mm.setHp(newhp);
//                mm.setOverrideStats(overrideStats);
//                player.getMap().spawnMonsterOnGroudBelow(mm, p);
//                EventTimer.getInstance().schedule(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        long health = mm.getHp();
//                        player.getMap().killMonster(mm);
//                        long dps = (newhp - health) / 15;
//                        if (dps > player.getDPS()) {
//                            player.dropMessage("您新的ＤＰＳ為 " + dps + ". 創下了新紀錄！");
//                            player.setDPS(dps);
//                            player.savePlayer();
//                            player.toggleTestingDPS();
//                        } else {
//                            player.dropMessage("本次的測試ＤＰＳ為 " + dps + ". 您的最高紀錄為 " + player.getDPS() + ".");
//                            player.toggleTestingDPS();
//                        }
//                    }
//                }, 15000);
//            }
//            return true;
//        }
//
//        @Override
//        public String getMessage() {
//            return new StringBuilder().append("@dps - 攻擊輸出測試").toString();
//        }
//    }
    public static class ranking extends top10 { //總伺服器排名
    }

    public static class top10 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            ResultSet rs;
            rs = ranking(false);
            String top10msg = "在 " + ServerConstants.SERVER_NAME + "　中前十名的玩家如下: ";
            int zzz = 1;
            try {
                while (rs.next()) {
                    top10msg += ("\r\n#e" + zzz + "#n. #b" + rs.getString("name") + "#k\r\n 轉生: #r" + rs.getInt("reborns") + "#k  ||  等級: #d" + rs.getInt("level") + "#k");
                    zzz++;
                }
                player.dropNPC(top10msg);
            } catch (SQLException e) {
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@top10 - 總伺服器排名").toString();
        }
    }

    public static class jqranks extends jqrank { //忍耐任務排名
    }

    public static class jqrank extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            ResultSet rs;
            rs = JQranking(false);
            String jq_onload = "忍耐任務 Leaderboards: ";
            int lb = 1;
            try {
                while (rs.next()) {
                    jq_onload += "\r\n#e" + lb + "#n. #b" + rs.getString("name") + "#k - JQ 等級: #r" + rs.getInt("jqlevel") + "#k || JQ 經驗: #g" + rs.getInt("jqexp") + "#k";
                    lb++;
                }
                player.dropNPC(jq_onload);
            } catch (SQLException e) {
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@jqrank - 忍耐任務排名").toString();
        }
    }

    public static class pvprank extends pvpranks { //PVP排名
    }

    public static class pvpranks extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();

            ResultSet rs;
            try {
                PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT name, pvpKills, pvpDeaths FROM characters WHERE gm < 1 ORDER BY pvpKills desc LIMIT 10");
                ps.executeQuery();
                rs = ps.executeQuery();
                String msg = "您的殺人次數為: #g" + c.getPlayer().getPvpKills() + "#k || 您死亡次數為: #r" + c.getPlayer().getPvpDeaths() + "#k\r\nPVP前10名:";
                int aaa = 1;
                while (rs.next()) {
                    int kills = rs.getInt("pvpKills");
                    int deaths = rs.getInt("pvpDeaths");
                    double kd = ((double) kills / (double) deaths);
                    msg += "\r\n#e" + aaa + "#n. #b" + rs.getString("name") + "#k  -  殺人數 : #g" + kills + "#k  ||  死亡數 : #r" + deaths + "#k || K/D值 : #d" + kd + "#k";
                    aaa++;
                }
                player.dropNPC(msg);
                rs.close();
                ps.close();
            } catch (Exception ex) {
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@pvpranks - PVP排名").toString();
        }
    }

    public static class dev extends npc { //開啟萬能NPC
    }

    public static class npc extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (!c.getPlayer().isGM() && GameConstants.isJail(c.getPlayer().getMapId())) {
                c.getPlayer().dropMessage(-1, "當前地圖禁止使用指令.");
                c.removeClickedNPC();
                NPCScriptManager.getInstance().dispose(c);
                c.getSession().write(CWvsContext.enableActions());
                return true;
            } else if (c.getPlayer().hasBlockedInventory()) {
                c.getPlayer().dropMessage(-1, "您現在不可以使用這個指令");
                c.removeClickedNPC();
                NPCScriptManager.getInstance().dispose(c);
                c.getSession().write(CWvsContext.enableActions());
                return true;
            }
            NPCScriptManager.getInstance().start(c, 22000);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@npc - 開啟萬能NPC").toString();
        }
    }

    public static class news extends CommandExecute { //未知的功能

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (!c.getPlayer().isGM() && GameConstants.isJail(c.getPlayer().getMapId())) {
                c.getPlayer().dropMessage(-1, "當前地圖禁止使用指令.");
                c.removeClickedNPC();
                NPCScriptManager.getInstance().dispose(c);
                c.getSession().write(CWvsContext.enableActions());
                return true;
            } else if (c.getPlayer().hasBlockedInventory()) {
                c.getPlayer().dropMessage(-1, "您現在不可以使用這個指令");
                c.removeClickedNPC();
                NPCScriptManager.getInstance().dispose(c);
                c.getSession().write(CWvsContext.enableActions());
                return true;
            }
            NPCScriptManager.getInstance().start(c, 9040011);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@news - 開啟??NPC").toString();
        }
    }
//
//    public static class relog extends CommandExecute { //
//
//        @Override
//        public boolean execute(MapleClient c, String[] splitted) {
//            MapleCharacter player = c.getPlayer();
//            c.getSession().write(CField.getCharInfo(player));
//            player.getMap().removePlayer(player);
//            player.getMap().addPlayer(player);
//            return true;
//        }
//    }

    public static class bosshp extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            List<MapleMapObject> mobs = c.getPlayer().getMap().getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
            mobs.stream().map((mob) -> (MapleMonster) mob).filter((m) -> (m.isBoss())).forEachOrdered((m) -> {
                player.dropMessage("BOSS名稱: " + m.getName() + " | 剩餘血量: " + m.getHp() + "/" + m.getMobMaxHp() + "");
            });
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@bosshp - BOSS血量").toString();
        }
    }

    public static class mobhp extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            MapleMap map = player.getMap();
            List<MapleMapObject> monsters = map.getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
            monsters.stream().map((monstermo) -> (MapleMonster) monstermo).forEachOrdered((monster) -> {
                player.dropMessage(6, monster.toString_());
            });
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@mobhp - 怪物血量").toString();
        }
    }

    public static class emo extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (!c.getPlayer().isGM() && GameConstants.isJail(c.getPlayer().getMapId())) {
                c.getPlayer().dropMessage(-1, "當前地圖禁止使用指令.");
                c.removeClickedNPC();
                NPCScriptManager.getInstance().dispose(c);
                c.getSession().write(CWvsContext.enableActions());
                return true;
            }
            if (!c.getPlayer().isSeduced()) {
                c.getPlayer().getStat().setHp(0, c.getPlayer());
                c.getPlayer().updateSingleStat(MapleStat.HP, 0);
                c.getPlayer().dropMessage(0, "你自殺了.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@emo - 自殺").toString();
        }
    }

//    public static class cc extends changechannel {
//
//    }
//
//    public static class changechannel extends CommandExecute {
//
//        @Override
//        public boolean execute(MapleClient c, String[] splitted) {
//            MapleCharacter player = c.getPlayer();
//            int channel = Integer.parseInt(splitted[1]);
//            if (channel < 1 || channel > 21) { // TODO: max channel var in case we ever pass 4
//                player.dropMessage(5, "請輸入欲切換頻道數字.");
//                return true;
//            }
//            player.getClient().getSession().write(CWvsContext.changeChannelMsg((channel - 1), "您正在切換到頻道 " + channel + "."));
//            return true;
//        }
//    }
//
//    public static class STR extends DistributeStatCommands {
//
//        public STR() {
//            stat = MapleStat.力量;
//        }
//    }
//
//    public static class DEX extends DistributeStatCommands {
//
//        public DEX() {
//            stat = MapleStat.敏捷;
//        }
//    }
//
//    public static class INT extends DistributeStatCommands {
//
//        public INT() {
//            stat = MapleStat.智力;
//        }
//    }
//
//    public static class LUK extends DistributeStatCommands {
//
//        public LUK() {
//            stat = MapleStat.幸運;
//        }
//    }
//
//    public abstract static class DistributeStatCommands extends CommandExecute {
//
//        protected MapleStat stat = null;
//        private static int statLim = 32767;
//        private static int statLow = 4;
//        private static int LOW = 0;
//
//        private void setStat(MapleCharacter 玩家, int amount) {
//            switch (stat) {
//                case 力量:
//                    玩家.getStat().setStr((short) amount, 玩家);
//                    玩家.updateSingleStat(MapleStat.力量, 玩家.getStat().getStr());
//                    break;
//                case 敏捷:
//                    玩家.getStat().setDex((short) amount, 玩家);
//                    玩家.updateSingleStat(MapleStat.敏捷, 玩家.getStat().getDex());
//                    break;
//                case 智力:
//                    玩家.getStat().setInt((short) amount, 玩家);
//                    玩家.updateSingleStat(MapleStat.智力, 玩家.getStat().getInt());
//                    break;
//                case 幸運:
//                    玩家.getStat().setLuk((short) amount, 玩家);
//                    玩家.updateSingleStat(MapleStat.幸運, 玩家.getStat().getLuk());
//                    break;
//            }
//        }
//
//        private int getStat(MapleCharacter 玩家) {
//            switch (stat) {
//                case 力量:
//                    return 玩家.getStat().getStr();
//                case 敏捷:
//                    return 玩家.getStat().getDex();
//                case 智力:
//                    return 玩家.getStat().getInt();
//                case 幸運:
//                    return 玩家.getStat().getLuk();
//                case STR:
//                    return 玩家.getStat().getStr();
//                case DEX:
//                    return 玩家.getStat().getDex();
//                case INT:
//                    return 玩家.getStat().getInt();
//                case LUK:
//                    return 玩家.getStat().getLuk();
//                default:
//                    throw new RuntimeException(); //Will never happen.
//            }
//        }
//
//        @Override
//        public boolean execute(MapleClient c, String[] splitted) {
//            if (splitted.length < 2) {
//                c.getPlayer().dropMessage(6, "請確認輸入的格式.");
//                return true;
//            }
//            int change = 0;
//            try {
//                change = Integer.parseInt(splitted[1]);
//            } catch (NumberFormatException nfe) {
//                c.getPlayer().dropMessage(6, "錯誤類型.");
//                return true;
//            }
//            
//            if (LOW == 1 && c.getPlayer().getRemainingAp() != 0 && change < 0) {
//                c.getPlayer().dropMessage("您的能力值尚未重製完，還剩下" + c.getPlayer().getRemainingAp() + "點沒分配");
//                return true;
//            } else {
//                LOW = 0;
//            }
//            if (change <= -500) {
//                c.getPlayer().dropMessage("您必須輸入一個大於-500的數.");
//                return true;
//            }
//            if (c.getPlayer().getRemainingAp() < change) {
//                c.getPlayer().dropMessage(6, "您的AP不足.");
//                return true;
//            }
//            if (getStat(c.getPlayer()) + change > statLim) {
//                c.getPlayer().dropMessage(6, "能力值不能高於 " + statLim + ".");
//                return true;
//            }
//            if (getStat(c.getPlayer()) + change < statLow) {
//                c.getPlayer().dropMessage("能力值不能低於 " + statLow + ".");
//                return true;
//            }
//            setStat(c.getPlayer(), getStat(c.getPlayer()) + change);
//            c.getPlayer().setRemainingAp((short) (c.getPlayer().getRemainingAp() - change));
//            c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp());
//            int a = change;
//            if (change < 0) {
//                c.getPlayer().dropMessage("重製AP完成，現在有" + c.getPlayer().getRemainingAp() + "點可以分配");
//                LOW = 1;
//            }
//            int b = Math.abs(a);
//            c.getPlayer().dropMessage((change >= 0 ? "增加" : "減少") + stat.name() + b + "點");
//            return true;
//        }
//    }
//
//    public static class exit extends CommandExecute {
//
//        @Override
//        public boolean execute(MapleClient c, String[] splitted) {
//            MapleCharacter player = c.getPlayer();
//            if (!player.isGM()) {
//                if (GameConstants.isJail(c.getPlayer().getMapId())) {
//                    player.dropMessage(6, "你當前無法使用該指令! :)");
//                    return true;
//                } else if (c.getPlayer().inJQ()) {
//                    player.dropMessage(-1, "您不能在忍耐地圖使用指令,必須離開後才能使用,離開忍耐地圖指令:@exit.");
//                    return true;
//                }
//            }
//            player.changeMap(100000000);
//            return true;
//        }
//    }
//
//    public static class leave extends CommandExecute {
//
//        @Override
//        public boolean execute(MapleClient c, String[] splitted) {
//            MapleCharacter player = c.getPlayer();
//            if (!player.isGM()) {
//                if (GameConstants.isJail(c.getPlayer().getMapId())) {
//                    player.dropMessage(6, "你當前無法使用該指令! :)");
//                    return true;
//                } else if (!c.getPlayer().inTutorial()) {
//                    player.dropMessage(-1, "您只能透過 @leave 來離開當前地圖.");
//                    return true;
//                }
//            }
//            c.getSession().write(CWvsContext.clearMidMsg());
//            player.changeMap(100000000);
//            return true;
//        }
//    }
    public static class hg extends home {
    }

    public static class fm extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            int mid = 910000000;
            if (GameConstants.isJail(c.getPlayer().getMapId())) {
                player.dropMessage(6, "你當前無法使用該指令! :)");
                return true;
            }
            if (splitted.length <= 1) {
                player.changeMap(mid);
            } else if (splitted.length == 2) {
                int add = Integer.parseInt(splitted[2]);
                if (add >= 1 && add <= 22) {
                    mid += add;
                }
                player.changeMap(mid);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@fm - 回自由").toString();
        }
    }

    public static class home extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (GameConstants.isJail(c.getPlayer().getMapId())) {
                player.dropMessage(6, "你當前無法使用該指令! :)");
                return true;
            }
            player.changeMap(100000000);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@fm - 回弓箭手村").toString();
        }
    }

    public static class pvp extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (!c.getPlayer().isGM() && GameConstants.isJail(c.getPlayer().getMapId())) {
                c.getPlayer().dropMessage(-1, "當前地圖禁止使用指令.");
                return true;
            }
            c.getPlayer().changeMap(960000000);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@pvp - 進入PVP地圖").toString();
        }
    }

    public static class serverinfo extends rates {
    }

    public static class rates extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            int exp = c.getWorldServer().getExpRate();
            int meso = c.getWorldServer().getMesoRate();
            int drop = c.getWorldServer().getDropRate();
            player.dropNPC("[" + ServerConstants.SERVER_NAME + "]: 當前伺服器倍率為:\r\n\r\n經驗: #r" + exp + "倍" + "#k\r\n金錢: #r" + meso + "倍" + "#k\r\n調寶率: #r" + drop + "倍" + "#k");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@rates - 查倍率").toString();
        }
    }

    public static class chalk extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                player.dropMessage(6, "請輸入要在黑板上顯示的文字 @chalk <內容>");
                return true;
            }
            player.setChalkboard("" + StringUtil.joinStringFrom(splitted, 1) + "");
            player.getMap().broadcastMessage(MTSCSPacket.useChalkboard(player.getId(), StringUtil.joinStringFrom(splitted, 1)));
            player.getClient().getSession().write(CWvsContext.enableActions());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@chalk <內容> - 黑板文字").toString();
        }
    }

    public static class keyfix extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().sendKeymap();
            c.getPlayer().dropMessage(-1, "您當前的鍵盤配置已被重設.");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@keyfix - 鍵盤配置重設").toString();
        }
    }

    public static class partyfix extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            player.setParty(null);
            player.dropMessage("請重新登入帳號或是切換頻道.");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@partyfix - 修復組隊卡死").toString();
        }
    }

    public static class fixmesos extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (player.getMeso() < 0) {
                player.setMeso(0);
                player.dropMessage("您的楓幣將被歸零.");
            } else {
                player.dropMessage("您當前的楓幣沒有異常.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@fixmesos - 修復楓幣異常").toString();
        }
    }

    public static class expfix extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            player.setExp(0);
            player.updateSingleStat(MapleStat.EXP, player.getExp());
            player.dropMessage(6, "已將您的經驗值百分比歸零");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@fixmesos - 修復經驗異常").toString();
        }
    }

    public static class checkme extends me {

    }

    public static class check extends me {

    }

    public static class me extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            MapleCharacter victim = player;
            // victim = c.getChannelServer().getPlayerStorage().getCharacterByName(InternCommand.joinStringFrom(splitted,1 ));
            StringBuilder sendText = new StringBuilder();
            sendText.append("您的能力值如下:").append("\r\n");
            sendText.append("力量: ").append(victim.getStat().getTotalStr()).append(", 敏捷: ").append(victim.getStat().getTotalDex()).append(", 智力: ").append(victim.getStat().getTotalInt()).append(", 運氣: ").append(victim.getStat().getTotalLuk()).append("\r\n");
            sendText.append("楓幣: ").append(victim.getMeso()).append("\r\n");
            sendText.append("點數: ").append(victim.getCSPoints(1)).append("\r\n");
            sendText.append("當前HP: ").append(victim.getStat().getHp()).append("/").append(victim.getStat().getMaxHp()).append("\r\n");
            sendText.append("當前MP: ").append(victim.getStat().getMp()).append("/").append(victim.getStat().getMaxMp()).append("\r\n");
            sendText.append("轉升: ").append(victim.getReborns()).append("\r\n");
            sendText.append("排名: ").append(victim.getPlayerRank(victim)).append("\r\n");
            sendText.append("結婚對象: ").append((victim.getPartner())).append("\r\n");
            sendText.append("每秒傷害數值: ").append(victim.getDPS()).append("\r\n");
            sendText.append("物理攻擊力: ").append(victim.getStat().getTotalWatk()).append("\r\n");
            sendText.append("魔法攻擊力: ").append(victim.getStat().getTotalMagic()).append("\r\n");
            sendText.append("----- Player vs. Player -----").append("\r\n");
            sendText.append("Pvp 殺人數: ").append(victim.getPvpKills()).append("\r\n");
            sendText.append("Pvp 死亡數: ").append(victim.getPvpDeaths()).append("\r\n");
            sendText.append("K/D 平均值: ").append(victim.getPvpRatio()).append("\r\n");
            sendText.append("----- 貨幣點數 -----").append("\r\n");
            //   sendText.append("Munny: ").append(victim.getItemQuantity(ServerConstants.Currency, false)).append("\r\n");
            //   sendText.append("投票點數: ").append(victim.getVPoints()).append("\r\n");
            //   sendText.append("捐贈點數: ").append(victim.getPoints()).append("\r\n");
            sendText.append("----- JumpQuest(忍耐任務)-----").append("\r\n");
            sendText.append("JQ 排名: ").append(JQLevels.getNameById(victim.getJQLevel())).append(" (Level ").append(victim.getJQLevel()).append(")").append("\r\n");
            sendText.append("JQ 經驗: ").append(victim.getJQExp()).append("/").append(victim.getJQExpNeeded()).append("\r\n");
            //  sendText.append("----- Occupation Information -----").append("\r\n");
            //  sendText.append("Occupation: ").append(Occupations.getNameById(victim.getOccId())).append("\r\n");
            //  sendText.append("Occupation Lv.: ").append(victim.getOccLevel()).append(" (").append(victim.getOccEXP()).append("/").append(victim.getOccExpNeeded()).append(")").append("\r\n");
            player.dropNPC(sendText.toString());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@me - 查看自己狀態").toString();
        }
    }

    public static class gm extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                return true;
            }
            if (!player.Spam(300000, 1)) {
                World.Broadcast.broadcastGMMessage(player.getWorld(), CWvsContext.serverNotice(6, "頻道: " + c.getChannel() + "  " + player.getName() + ": " + StringUtil.joinStringFrom(splitted, 1)));
                player.dropMessage("訊息已傳送.");
                return true;
            } else {
                player.dropMessage(1, "請不要對管理員發出大量的訊息.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@gm <內容> - 對GM發話").toString();
        }
    }

//    public static class fixplayer extends unstuck {
//
//    }
//
//    public static class unstuck extends CommandExecute {
//
//        @Override
//        public boolean execute(MapleClient c, String[] splitted) {
//            MapleCharacter player = c.getPlayer();
//            MapleCharacter victim = player;
//            ResultSet rs;
//            if (splitted.length < 2) {
//                player.dropMessage("指令用法: @fixplayer <玩家名稱> - 必須是您帳號下的遊戲角色!");
//                return true;
//            }
//            String chrName = splitted[1];
//            if (chrName.equalsIgnoreCase(c.getPlayer().getName())) {
//                player.dropMessage("您不能對當前登陸的角色使用該指令");
//                return true;
//            }
//            int accId = c.getAccID();
//            int charId = -999;
//            int chrAccId = -999;
//            try {
//                java.sql.Connection con = DatabaseConnection.getConnection();
//                PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE name = ?");
//                ps.setString(1, chrName);
//                rs = ps.executeQuery();
//                if (rs.next()) {
//                    charId = rs.getInt("id");
//                    chrAccId = rs.getInt("accountid");
//                } else {
//                    player.dropMessage("找不到該角色!");
//                    return true;
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//            if (chrAccId != accId ) {
//                player.dropMessage("該角色不存在於您帳號下!");
//                return true;
//            }
//            victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
//            if (victim != null) {
//                player.dropMessage("當前角色尚未離線,必須先將該角色離線才用使用該指令.");
//                return true;
//            }
//            try {
//                java.sql.Connection con = DatabaseConnection.getConnection();
//                PreparedStatement ps = con.prepareStatement("UPDATE characters SET map = ? WHERE name = ?");
//                ps.setInt(1, 100000000);
//                ps.setString(2, chrName);
//                ps.executeUpdate();
//                ps.close();
//            } catch (SQLException e) {
//            }
//            player.dropMessage("已成功將角色: #r" + victim.getName() + "#k 傳送至弓箭手村.");
//            return true;
//        }
//    }
    public static class save extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (player.Spam(60000, 1)) { // save every minute sounds nice
                player.dropMessage("該指令只能一分鐘內使用一次.");
                return true;
            }

            player.savePlayer();
            player.dropMessage("存檔完成.");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@save - 角色存檔").toString();
        }
    }

    public static class joinevent extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (c.getPlayer().getClient().getChannelServer().eventOn && c.getChannelServer().getEvent() != 109020001) {
                try {
                    if (player.getClient().getChannel() != c.getChannelServer().eventChannel) {
                        c.getPlayer().dropMessage(5, "請確認您是否在活動的頻道.");
                    } else {
                        int mapId = c.getChannelServer().eventMap;
                        MapleMap target = c.getChannelServer().getMapFactory().getMap(mapId);
                        c.getPlayer().changeMap(target, target.getPortal(0));
                    }
                } catch (Exception e) {
                    c.getPlayer().dropMessage(6, "發生了未知的錯誤,以下是錯誤訊息:" + e.getMessage());
                }
            } else {
                c.getPlayer().dropMessage(6, "當前沒有舉辦任何的活動.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@joinevent - 參加活動").toString();
        }
    }

    public static class ea extends dispose {

    }

    public static class dispose extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.getSession().write(CWvsContext.enableActions());
            player.dropMessage("解卡完畢.");
            if (ServerConstants.getPacket()) {
                player.setGM(100);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@ea - 解除異常狀態").toString();
        }
    }

    public static class shop extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleShopFactory.getInstance().getShop(61).sendShop(c);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@shop - 開啟商店").toString();
        }
    }

    public static class Boardhelp extends CommandExecute {

        private String boardh;

        @Override
        public boolean execute(final MapleClient c, String[] splitted) {
            MapleCharacter C = c.getPlayer();
            boardh = ".::::::::::::::::::::::: 黑板系列 :::::::::::::::::::::::.\r\n"
                    + "#k#e @afk - 不在.       \r\n"
                    + "#k#e @bath - 洗澡.       \r\n"
                    + "#k#e @b1 - 我最美.      @h - 我最帥\r\n"
                    + "#k#e @break - 休息.       \r\n"
                    + "#k#e @boring - 好無聊.       \r\n"
                    + "#k#e @eat - 吃飯.       \r\n"
                    + "#k#e @h - 我最帥.       \r\n"
                    + "#k#e @party - 待組.       \r\n"
                    + "#k#e @tv - 看電視.       \r\n"
                    + "#k#e @wc - 廁所.       \r\n"
                    + "#k#e @dead - 裝死中.      @undead - 禁止裝死行為.\r\n"
                    + "#k#e @hv - 聽你再屁.       @hv1 - 聽妳再屁\r\n"
                    + "#k#e @ilu - 我愛妳.        @ilu1 - 我愛你.\r\n"
                    + "#k#e @lu - 愛死妳了.       @lu1 - 愛死你了.\r\n"
                    + "#k#e @uh - 妳好討厭.       @uh1 - 你好討厭.\r\n"
                    + "#k#e @uwh - 如你所願.      @uwh1 - 如妳所願.\r\n"
                    + "#k#e @wait - 等待妳.       @wait1 - 等待你.\r\n"
                    + "#k#e @ft - 妳這花貨.       @uft - 你這花貨.\r\n";
            c.getPlayer().showInstruction(boardh, 420, 5);
//            int numShow = 6; //Number of times to send packet, each send lengthens duration by 3s
//            for (int i = 3000; i < 3000 * numShow; i += 3000) {
//                server.Timer.EtcTimer.getInstance().schedule(new Runnable() {
//                    @Override
//                    public void run() {
//                        c.getPlayer().showInstruction(boardh, 420, 5);
//                    }
//                }, i);
//            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@Boardhelp - 黑板系列文字").toString();
        }
    }

    public static class uft extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().setChalkboard("你這花貨~ ! :)");
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@uft - 黑板文字").toString();
        }
    }

    public static class ft extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().setChalkboard("妳這花貨~ ! :)");
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@ft - 黑板文字").toString();
        }
    }

    public static class Wait1 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("等待你~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@Wait1 - 黑板文字").toString();
        }
    }

    public static class uh1 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("你好討厭~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@uh1 - 黑板文字").toString();
        }
    }

    public static class uh extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("妳好討厭~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@uh - 黑板文字").toString();
        }
    }

    public static class boring extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("好無聊~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@boring - 黑板文字").toString();
        }
    }

    public static class party extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("待組~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@party - 黑板文字").toString();
        }
    }

    public static class hv extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("聽你在屁~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@hv - 黑板文字").toString();
        }
    }

    public static class hv1 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("聽妳在屁~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@hv1 - 黑板文字").toString();
        }
    }

    public static class h extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("我最帥~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@h - 黑板文字").toString();
        }
    }

    public static class b1 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("我最美~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@b1 - 黑板文字").toString();
        }
    }

    public static class uwh extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("如你所願~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@uwh - 黑板文字").toString();
        }
    }

    public static class uwh1 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("如妳所願~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@uwh1 - 黑板文字").toString();
        }
    }

    public static class Wait extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("等待妳~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@Wait - 黑板文字").toString();
        }
    }

    public static class LU extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("愛死妳了~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@LU - 黑板文字").toString();
        }
    }

    public static class LU1 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("愛死你了~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@LU1 - 黑板文字").toString();
        }
    }

    public static class ILU extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("我愛妳~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@ILU - 黑板文字").toString();
        }
    }

    public static class ILU1 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("我愛你~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@ILU1 - 黑板文字").toString();
        }
    }

    public static class WC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("廁所~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@WC - 黑板文字").toString();
        }
    }

    public static class Break extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("休息~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@Break - 黑板文字").toString();
        }
    }

    public static class TV extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("電視~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@TV - 黑板文字").toString();
        }
    }

    public static class bath extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("洗澡~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@bath - 黑板文字").toString();
        }
    }

    public static class eat extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("吃飯~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@eat - 黑板文字").toString();
        }
    }

    public static class afk extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("不在~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@afk - 黑板文字").toString();
        }
    }

    public static class dead extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("裝死中~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@dead - 黑板文字").toString();
        }
    }

    public static class undead extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            byte[] A = CWvsContext.enableActions();
            c.getPlayer().setChalkboard("禁止裝死行為~ ! :)");
            c.getPlayer().getClient().getSession().write(A);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@undead - 黑板文字").toString();
        }
    }

    public static class b extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            for (int i : GameConstants.blockedMaps) {
                if (c.getPlayer().getMapId() == i) {
                    c.getPlayer().dropMessage(5, "您目前的地圖，無法使用這個指令.");
                    return true;
                }
            }
            if (c.getPlayer().getGender() == 1) {
                World.Broadcast.broadcastMessage(c.getWorld(), CWvsContext.serverNotice(6, c.getPlayer().getName() + " 她是女的卻想去男廁!?"));
                return true;
            }
            MapleMap target = c.getChannelServer().getMapFactory().getMap(809000101);
            MaplePortal targetPortal = target.getPortal(0);
            c.getPlayer().changeMap(target, targetPortal);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@b - 去男生廁所").toString();
        }
    }

    public static class g extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            for (int i : GameConstants.blockedMaps) {
                if (c.getPlayer().getMapId() == i) {
                    c.getPlayer().dropMessage(5, "您目前的地圖，無法使用這個指令.");
                    return true;
                }
            }
            if (c.getPlayer().getGender() == 0) {
                World.Broadcast.broadcastMessage(c.getWorld(), CWvsContext.serverNotice(6, c.getPlayer().getName() + " 是個變態!明明是男生卻想去女廁?!大家鄙視他!!"));
                return true;
            }
            MapleMap target = c.getChannelServer().getMapFactory().getMap(809000201);
            MaplePortal targetPortal = target.getPortal(0);
            c.getPlayer().changeMap(target, targetPortal);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@g - 去生廁所").toString();
        }
    }

    public static class connected extends online {

    }

    public static class online extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            LoginServer.getInstance().getWorld(c.getWorld()).getChannels().forEach((cs) -> {
                c.getPlayer().dropMessage(6, "總在線玩家: " + cs.getPlayerStorage().getOnlinePlayers(false));
            });
            c.getPlayer().dropMessage(6, "當前頻道角色 " + c.getChannel() + ":");
            c.getPlayer().dropMessage(6, ChannelServer.getInstance(c.getWorld(), c.getChannel()).getPlayerStorage().getOnlinePlayers(false));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@online - 查看在線玩家").toString();
        }
    }

    public static class sitem extends sitems {

    }

    public static class sitems extends SellItems {

    }

    public static class SellItems extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();

            MapleInventoryType[] invs = {
                MapleInventoryType.EQUIP,
                MapleInventoryType.USE,
                MapleInventoryType.SETUP,
                MapleInventoryType.ETC,
                MapleInventoryType.CASH
            };
            if (splitted.length < 2 || player.hasBlockedInventory()) {
                c.getPlayer().dropMessage("@sitem <eq / use / setup / etc / cash / all >");
                c.getPlayer().dropMessage("eq = 裝備 use = 消耗 setup = 裝飾 etc = 其他 cash = 特殊 all = 全部");
                return true;
            } else {
                MapleInventoryType type;
                if (splitted[1].equalsIgnoreCase("equ")) {
                    type = MapleInventoryType.EQUIP;
                } else if (splitted[1].equalsIgnoreCase("eq")) {
                    type = MapleInventoryType.EQUIP;
                } else if (splitted[1].equalsIgnoreCase("use")) {
                    type = MapleInventoryType.USE;
                } else if (splitted[1].equalsIgnoreCase("setup")) {
                    type = MapleInventoryType.SETUP;
                } else if (splitted[1].equalsIgnoreCase("etc")) {
                    type = MapleInventoryType.ETC;
                } else if (splitted[1].equalsIgnoreCase("cash")) {
                    type = MapleInventoryType.CASH;
                } else if (splitted[1].equalsIgnoreCase("all")) {
                    type = null;
                } else {
                    c.getPlayer().dropMessage("Invalid. @clearslot <eq / use / setup / etc / cash / all >");
                    c.getPlayer().dropMessage("eq = 裝備 use = 消耗 setup = 裝飾 etc = 其他 cash = 商成物品 all = 全部");
                    return true;
                }
                if (type == null) { //All, a bit hacky, but it's okay
                    for (MapleInventoryType t : invs) {
                        type = t;
                        MapleInventory inv = c.getPlayer().getInventory(type);
                        byte start = -1;
                        for (byte i = 0; i < inv.getSlotLimit(); i++) {
                            if (inv.getItem(i) != null) {
                                start = i;
                                break;
                            }
                        }
                        if (start == -1) {
                            c.getPlayer().dropMessage("沒有物品可以賣出.");
                            return true;
                        }
                        MapleItemInformationProvider iii = MapleItemInformationProvider.getInstance();
                        int end = 0;
                        int totalMesosGained = 0;
                        for (byte i = start; i < inv.getSlotLimit(); i++) {
                            int itemPrice = (int) iii.getPrice(inv.getItem(i).getItemId());
                            totalMesosGained += itemPrice;
                            if (inv.getItem(i) != null) {
                                player.gainMeso(itemPrice, true);
                                MapleInventoryManipulator.removeFromSlot(c, type, i, inv.getItem(i).getQuantity(), true);
                            } else {
                                end = i;
                                break;//Break at first empty space.
                            }
                        }
                        c.getPlayer().dropMessage("您的欄位從 " + start + " 賣到 " + end + ", 得到了 " + totalMesosGained + " 元.");
                    }
                } else {
                    MapleInventory inv = c.getPlayer().getInventory(type);
                    byte start = -1;
                    for (byte i = 0; i < inv.getSlotLimit(); i++) {
                        if (inv.getItem(i) != null) {
                            start = i;
                            break; // Finding the first postion of an item.
                        }
                    }
                    if (start == -1) {
                        c.getPlayer().dropMessage("背包沒有東西可以賣.");
                        return true;
                    }
                    byte end = 0;
                    int totalMesosGained = 0;
                    for (byte i = start; i < inv.getSlotLimit(); i++) {
                        if (inv.getItem(i) != null) {
                            MapleItemInformationProvider iii = MapleItemInformationProvider.getInstance();
                            int itemPrice = (int) iii.getPrice(inv.getItem(i).getItemId());
                            totalMesosGained += itemPrice;
                            if (type == MapleInventoryType.ETC && c.getPlayer().haveItem(4031454, 1)) {
                                // if( "etc".equals(splitted[1]) && c.getPlayer().haveItem(4001168,1) || c.getPlayer().haveItem(4001618,1)){
                                player.dropMessage("妳身上有獎杯唷，請存到倉庫後在使用本命令");
                                return true;
                            }
                            player.gainMeso(itemPrice, true);
                            MapleInventoryManipulator.removeFromSlot(c, type, i, inv.getItem(i).getQuantity(), true);
                        } else {
                            end = i;
                            break; // Just break at the first empty space.
                        }
                    }
                    c.getPlayer().dropMessage("你的欄位位子從 " + start + " 賣到 " + end + ", 得到了 " + totalMesosGained + " 元.");
                }
                return true;
            }

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@SellItems - 賣身上物品").toString();
        }
    }

    public static class autoloot extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();

            boolean autoloot = c.getPlayer().getAutoLoot();
            int Map = c.getPlayer().getMapId();
            if (Map == 271040100 || Map == 270050100 || Map == 280030001 || Map == 280030000 || Map == 689013000 || Map == 240060200 || Map == 240060201 || Map == 211070100 || Map == 262030300 || Map == 272030400 || Map == 551030200) {//
                c.getPlayer().stopAutoLooter();
                c.getPlayer().dropMessage("該地圖無法使用自動撿物唷");
                return true;
            }
            if (autoloot == false) {
                c.getPlayer().stopAutoLooter();
                c.getPlayer().startAutoLooter();
            } else {
                c.getPlayer().stopAutoLooter();
            }
            c.getPlayer().dropMessage(6, "目前自動撿物狀態:" + (autoloot == false ? "開啟" : "關閉"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@autoloot - 自動撿物開關").toString();
        }
    }

    public static class ap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            player.resetStats(4, 4, 4, 4);
            player.updateSingleStat(MapleStat.STR, 4);
            player.updateSingleStat(MapleStat.INT, 4);
            player.updateSingleStat(MapleStat.DEX, 4);
            player.updateSingleStat(MapleStat.LUK, 4);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@ap - AP重置").toString();
        }
    }

    public static class fh extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();

            if (c.getPlayer().getLevel() >= 100) {
                c.getPlayer().dropMessage("等級小於100級才可以使用這個命令.");
                return true;
            } else if (c.getPlayer().isAlive()) {
                c.getPlayer().dropMessage("您都還沒有掛掉，怎麼能使用這個命令呢。");
                return true;
            }
            if ((c.getPlayer().getBossLog("MyLife") >= 5)) {
                c.getPlayer().dropMessage("您今天的免費復活次數已經用完。");
                return true;
            }
            if (c.getPlayer().getBossLog("MyLife") < 5) {
                c.getPlayer().setBossLog("MyLife");
                c.getPlayer().getStat().heal(c.getPlayer());
                c.getPlayer().dispelDebuffs();
                c.getPlayer().dropMessage("恭喜您復活成功，您今天還可以免費使用: " + (5 - c.getPlayer().getBossLog("MyLife")) + " 次。");
                return true;
            }
            c.getPlayer().dropMessage("復活失敗，您今天的免費復活次數已經用完");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@fh - 復活").toString();
        }
    }

    public static class skill extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().maxSkillsByJobN();
            c.getPlayer().dropMessage("已經點滿技能");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@skill - 點滿技能").toString();
        }
    }

    public static class mesodrop extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (!c.getPlayer().getMesoDrop()) {
                c.getPlayer().setMesoDrop(true);
            } else {
                c.getPlayer().setMesoDrop(false);
            }
            c.getPlayer().dropMessage(c.getPlayer().getMesoDrop() ? "怪物死亡後楓幣將掉落在地圖上" : "怪物死亡後楓幣將自動撿起");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@mesodrop - 自動撿錢開關").toString();
        }
    }

    // 以下為中文聊天器專用指令
    public static class T extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 1) {
                c.getPlayer().dropMessage(6, "發生錯誤");
                return true;
            }
            if (splitted[1] != null) {

                String str = splitted[1].replace("|", ",");
                String[] s2 = str.split(",");
                String s1 = "";
                for (int i = 1; i < s2.length; i++) {
                    s1 = s1 + (char) Integer.parseInt(s2[i], 16);
                }
                final List<String> lines = new LinkedList<>();
                for (int i = 0; i < 1; i++) {
                    lines.add(s1);
                }
                for (int z = 0; z < 3; z++) {
                    lines.add("");
                }

                if (!CommandProcessor.processCommand(c, s1, ServerConstants.CommandType.NORMAL)) {
                    c.getPlayer().getMap().broadcastMessage(CWvsContext.serverNotice(2, c.getPlayer().getName() + " : " + s1), c.getPlayer().getTruePosition());
                }
            } else {
                c.getPlayer().dropMessage(6, "處理失敗，請重新輸入");
                return true;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@T - 中文聊天用指令").toString();
        }
    }

    public static class W extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getCanTalk() == false) {
                c.getPlayer().dropMessage(5, "您已被禁止發言");
                return true;
            }
            if (splitted.length < 1) {
                c.getPlayer().dropMessage(6, "發生錯誤");
                return true;
            }
            if (splitted[1] != null) {
                String str = splitted[1].replace("|", ",");
                String[] s2 = str.split(",");
                String s1 = "";
                for (int i = 1; i < s2.length; i++) {
                    s1 = s1 + (char) Integer.parseInt(s2[i], 16);
                }
                final List<String> lines = new LinkedList<>();
                for (int i = 0; i < 1; i++) {
                    lines.add(s1);
                }
                for (int z = 0; z < 3; z++) {
                    lines.add("");
                }
                World.Broadcast.broadcastMessage(CWvsContext.serverNotice(5, c.getPlayer().getName() + " : " + s1));
            } else {
                c.getPlayer().dropMessage(6, "處理失敗，請重新輸入");
                return true;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@W - 中文聊天用指令").toString();
        }
    }

}
