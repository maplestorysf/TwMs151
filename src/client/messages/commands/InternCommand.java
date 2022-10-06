package client.messages.commands;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import constants.ServerConstants;
import database.DatabaseConnection;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.world.CheaterData;
import handling.world.World;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.TreeMap;
import server.MaplePortal;
import server.maps.MapleMap;
import tools.StringUtil;

public class InternCommand {

    public static ServerConstants.PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.巡邏者;
    }

    public static class Mute extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            String name = "";
            try {
                name = splitted[1];
            } catch (Exception ex) {
            }
            int ch = World.Find.findChannel(name);
            if (ch > 0) {
                MapleCharacter victim = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);
                victim.canTalk(!victim.getCanTalk());
                c.getPlayer().dropMessage("玩家[" + victim.getName() + "] 目前已經" + (victim.getCanTalk() ? "可以說話" : "閉嘴"));
            } else if (ch == -10) {
                c.getPlayer().dropMessage("玩家[" + splitted[1] + "] 目前正在購物商城");
            } else {
                try {
                    boolean canTalk = true;
                    Connection con = DatabaseConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement("SELECT canTalk FROM Characters WHERE name = ?");
                    ps.setString(1, name);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        ps.close();
                        rs.close();
                        c.getPlayer().dropMessage("玩家[" + name + "] 不存在於資料庫");
                        return true;
                    } else {
                        canTalk = rs.getInt("canTalk") == 1;
                    }

                    int tochange = (canTalk ? 0 : 1);
                    try (PreparedStatement pss = con.prepareStatement("Update Characters set canTalk = " + tochange + " Where name = " + name)) {
                        pss.executeUpdate();
                    }
                    c.getPlayer().dropMessage("玩家[" + name + "] 目前已經" + (tochange == 1 ? "可以說話" : "閉嘴"));
                } catch (Exception ex) {
                    c.getPlayer().dropMessage("發生異常情況 " + ex);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!Mute 	玩家名稱 - 讓玩家閉嘴或可以說話").toString();
        }
    }

    public static class MuteID extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            int id = 0;
            try {
                id = Integer.parseInt(splitted[1]);
            } catch (Exception ex) {
            }
            int ch = World.Find.findChannel(id);
            if (ch > 0) {
                MapleCharacter victim = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterById(id);
                victim.canTalk(!victim.getCanTalk());
                c.getPlayer().dropMessage("玩家[" + victim.getName() + "] 目前已經" + (victim.getCanTalk() ? "可以說話" : "閉嘴"));
            } else if (ch == -10) {
                c.getPlayer().dropMessage("玩家[" + splitted[1] + "] 目前正在購物商城");
            } else {
                try {
                    boolean canTalk = true;
                    Connection con = DatabaseConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement("SELECT canTalk FROM Characters WHERE id = ?");
                    ps.setInt(1, id);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        ps.close();
                        rs.close();
                        c.getPlayer().dropMessage("玩家編號[" + id + "] 不存在於資料庫");
                        return true;
                    } else {
                        canTalk = rs.getInt("canTalk") == 1;
                    }

                    int tochange = (canTalk ? 0 : 1);
                    try (PreparedStatement pss = con.prepareStatement("Update Characters set canTalk = " + tochange + " Where id = " + id)) {
                        pss.executeUpdate();
                    }
                    c.getPlayer().dropMessage("玩家[" + id + "] 目前已經" + (tochange == 1 ? "可以說話" : "閉嘴"));
                } catch (Exception ex) {
                    c.getPlayer().dropMessage("發生異常情況 " + ex);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!MuteID 	玩家編號 - 讓玩家閉嘴或可以說話").toString();
        }
    }

    public static class 禁言名單 extends MuteList {

    }

    public static class MuteList extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            java.util.Map<Integer, String> CharactersInfo = new TreeMap();
            Connection con = DatabaseConnection.getConnection();
            StringBuilder ret = new StringBuilder();
            String show = "";
            try {
                try (PreparedStatement ps = con.prepareStatement("select id, name from Characters WHERE mute = 1")) {
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            CharactersInfo.put(rs.getInt("id"), rs.getString("name"));
                        }
                    }
                }
            } catch (Exception ex) {
                c.getPlayer().dropMessage("發生異常情況 " + ex);
            }
            if (CharactersInfo.isEmpty()) {
                c.getPlayer().dropMessage("查詢禁言名單為空");
            } else {
                for (java.util.Map.Entry<Integer, String> entry : CharactersInfo.entrySet()) {
                    ret = new StringBuilder();
                    ret.append(" 角色暱稱 ");
                    ret.append(StringUtil.getRightPaddedStr(entry.getValue(), ' ', 13));
                    ret.append(" ID: ");
                    ret.append(StringUtil.getRightPaddedStr(entry.getKey() + "", ' ', 5));
                    ret.append("\r\n");
                    show += ret.toString();
                }
                c.getPlayer().dropNPC(show);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!MuteList  - 查看禁言名單").toString();
        }
    }

    public static class MuteMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().getMap().getCharactersThreadsafe().stream().map((chr) -> {
                chr.canTalk(!chr.getCanTalk());
                return chr;
            }).map((chr) -> {
                StringBuilder ret = new StringBuilder();
                ret.append(" 角色暱稱 ");
                ret.append(StringUtil.getRightPaddedStr(chr.getName(), ' ', 13));
                ret.append(" ID: ");
                ret.append(StringUtil.getRightPaddedStr(chr.getId() + "", ' ', 5));
                ret.append(" 目前已經: ");
                ret.append(chr.getCanTalk() ? "可以說話" : "閉嘴");
                return ret;
            }).forEachOrdered((ret) -> {
                c.getPlayer().dropMessage(ret.toString());
            });
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!MuteMap 	- 讓地圖玩家閉嘴或可以說話").toString();
        }
    }

    public static class HellBan extends PracticerCommand.Ban {

        public HellBan() {
            hellban = true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!hellban <玩家名稱> <原因> - hellban").toString();
        }
    }

    public static class CC extends ChangeChannel {

        @Override
        public String getMessage() {
            return new StringBuilder().append("!cc <頻道> - 更換頻道").toString();
        }
    }

    public static class ChangeChannel extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            int cc = Integer.parseInt(splitted[1]);
            if (c.getChannel() != cc) {
                c.getPlayer().changeChannel(cc);
            } else {
                c.getPlayer().dropMessage(5, "請輸入正確的頻道。");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!changechannel <頻道> - 更換頻道").toString();
        }
    }

    public static class 角色訊息 extends spy {
    }

    public static class spy extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            } else {
                String name = splitted[1];
                int ch = World.Find.findChannel(name);
                MapleCharacter victim = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);
                if (victim != null) {
                    if (victim.getGMLevel() > c.getPlayer().getGMLevel()) {
                        c.getPlayer().dropMessage(5, "你不能查看比你高權限的人!");
                    } else {
                        int mesoInStorage = 0;
                        try {
                            if (victim.getStorage() != null) {
                                mesoInStorage = victim.getStorage().getMeso();
                            }
                        } catch (Exception ex) {
                        }
                        c.getPlayer().dropMessage(5, "此玩家狀態:");
                        c.getPlayer().dropMessage(5, "玩家名稱: " + victim.getName() + " 玩家編號: " + victim.getId() + " 帳號: " + victim.getClient().getAccountName() + " 帳號ID: " + victim.getAccountID());
                        c.getPlayer().dropMessage(5, "玩家VIP: " + victim.getVip() + " 玩家權限: " + victim.getGMLevel() + " 等級: " + victim.getLevel() + " 職業: " + victim.getJob() + " 名聲: " + victim.getFame());
                        c.getPlayer().dropMessage(5, "地圖: " + victim.getMapId() + " - " + victim.getMap().getMapName());
                        c.getPlayer().dropMessage(5, "目前HP: " + victim.getStat().getHp() + " 目前MP: " + victim.getStat().getMp());
                        c.getPlayer().dropMessage(5, "最大HP: " + victim.getStat().getMaxHp() + " 最大MP: " + victim.getStat().getMaxMp());
                        c.getPlayer().dropMessage(5, "力量: " + victim.getStat().getStr() + "  ||  敏捷: " + victim.getStat().getDex() + "  ||  智力: " + victim.getStat().getInt() + "  ||  幸運: " + victim.getStat().getLuk());
                        c.getPlayer().dropMessage(5, "物理攻擊: " + victim.getStat().getTotalWatk() + "  ||  魔法攻擊: " + victim.getStat().getTotalMagic());
                        //       c.getPlayer().dropMessage(5, "物理攻擊: " + victim.getStat().getTotalWatk() + "  ||  魔法攻擊: " + victim.getStat().getTotalMagic());
                        c.getPlayer().dropMessage(5, "經驗倍率: " + victim.getStat().expBuff + " 金錢倍率: " + victim.getStat().mesoBuff + " 掉寶倍率: " + victim.getStat().dropBuff);
                        c.getPlayer().dropMessage(5, "GASH: " + victim.getCSPoints(1) + " 楓葉點數: " + victim.getCSPoints(2) + " 楓幣: " + victim.getMeso() + " 倉庫楓幣 " + mesoInStorage);
                        if (ch <= 0 && CashShopServer.getPlayerStorage().getCharacterByName(name) == null) {
                            c.getPlayer().dropMessage(5, "該角色為離線狀態");
                        } else {
                            if (ch <= 0) {
                                victim = CashShopServer.getPlayerStorage().getCharacterByName(name);
                            }
                            c.getPlayer().dropMessage(5, "IP:" + victim.getClient().getSessionIPAddress() + " 目前MAC:" + victim.getNowMacs() + " 所有MAC:" + victim.getClient().getMacs());
                            c.getPlayer().dropMessage(5, "對伺服器延遲: " + victim.getClient().getLatency());
                        }
                    }
                } else {
                    c.getPlayer().dropMessage(5, "該角色為離線狀態");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!").append(getClass().getSimpleName().toLowerCase()).append(" <玩家名字> - 觀察玩家").toString();
        }
    }

    public static class spybuff extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            } else {
                String name = splitted[1];
                int ch = World.Find.findChannel(name);
                MapleCharacter player = null;
                if (ch == -10) {
                    c.getPlayer().dropMessage("目前玩家[" + name + "]正在商城中");
                    return true;
                } else if (ch > 0) {
                    player = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);
                }
                if (player != null) {
                    int Watk = player.getBuffSource(MapleBuffStat.WATK);
                    int Matk = player.getBuffSource(MapleBuffStat.MATK);
                    int MapleWarrior = player.getBuffSource(MapleBuffStat.MAPLE_WARRIOR);
                    int SharpEyes = player.getBuffSource(MapleBuffStat.SHARP_EYES);
                    int Speed = player.getBuffSource(MapleBuffStat.SPEED);
                    int Jump = player.getBuffSource(MapleBuffStat.JUMP);
                    int AttackSpeed = player.getBuffSource(MapleBuffStat.BOOSTER);
                    int SpeedInfusion = player.getBuffSource(MapleBuffStat.SPEED_INFUSION);
                    c.getPlayer().dropMessage("玩家[" + name + "] 目前補助技能如下");
                    if (Watk != -1) {
                        c.getPlayer().dropMessage("物理攻擊技能: " + Watk);
                    }
                    if (Matk != -1) {
                        c.getPlayer().dropMessage("魔法攻擊技能: " + Matk);
                    }
                    if (MapleWarrior != -1) {
                        c.getPlayer().dropMessage("楓葉祝福技能: " + MapleWarrior);
                    }
                    if (SharpEyes != -1) {
                        c.getPlayer().dropMessage("會心之眼技能: " + SharpEyes);
                    }
                    if (Speed != -1) {
                        c.getPlayer().dropMessage("移動速度技能: " + Speed);
                    }
                    if (Jump != -1) {
                        c.getPlayer().dropMessage("跳越高度技能: " + Jump);
                    }
                    if (AttackSpeed != -1) {
                        c.getPlayer().dropMessage("攻擊速度技能: " + AttackSpeed);
                    }
                    if (SpeedInfusion != -1) {
                        c.getPlayer().dropMessage("最終極速技能: " + SpeedInfusion);
                    }
                    c.getPlayer().dropMessage("玩家[" + name + "] 補助技能如上");
                } else {
                    c.getPlayer().dropMessage(5, "找不到此玩家.");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!").append(getClass().getSimpleName().toLowerCase()).append(" <玩家名字> - 查看玩家補助技能").toString();
        }

    }

    public static class spyID extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            } else {
                int id = 0;
                try {
                    id = Integer.parseInt(splitted[1]);
                } catch (Exception ex) {
                }
                //  MapleCharacter victim = MapleCharacter.getCharacterById(id);
                int ch = World.Find.findChannel(id);
                MapleCharacter victim = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterById(id);
                if (victim != null) {
                    if (victim.getGMLevel() > c.getPlayer().getGMLevel()) {
                        c.getPlayer().dropMessage(5, "你不能查看比你高權限的人!");
                    } else {
                        int mesoInStorage = 0;
                        try {
                            if (victim.getStorage() != null) {
                                mesoInStorage = victim.getStorage().getMeso();
                            }
                        } catch (Exception ex) {
                        }
                        c.getPlayer().dropMessage(5, "此玩家狀態:");
                        c.getPlayer().dropMessage(5, "玩家名稱: " + victim.getName() + " 玩家編號: " + victim.getId() + " 帳號: " + victim.getClient().getAccountName() + " 帳號ID: " + victim.getAccountID());
                        c.getPlayer().dropMessage(5, "玩家VIP: " + victim.getVip() + " 玩家權限: " + victim.getGMLevel() + " 等級: " + victim.getLevel() + " 職業: " + victim.getJob() + " 名聲: " + victim.getFame());
                        c.getPlayer().dropMessage(5, "地圖: " + victim.getMapId() + " - " + victim.getMap().getMapName());
                        c.getPlayer().dropMessage(5, "目前HP: " + victim.getStat().getHp() + " 目前MP: " + victim.getStat().getMp());
                        c.getPlayer().dropMessage(5, "最大HP: " + victim.getStat().getMaxHp() + " 最大MP: " + victim.getStat().getMaxMp());
                        c.getPlayer().dropMessage(5, "力量: " + victim.getStat().getStr() + "  ||  敏捷: " + victim.getStat().getDex() + "  ||  智力: " + victim.getStat().getInt() + "  ||  幸運: " + victim.getStat().getLuk());
                        c.getPlayer().dropMessage(5, "物理攻擊: " + victim.getStat().getTotalWatk() + "  ||  魔法攻擊: " + victim.getStat().getTotalMagic());
                        //       c.getPlayer().dropMessage(5, "物理攻擊: " + victim.getStat().getTotalWatk() + "  ||  魔法攻擊: " + victim.getStat().getTotalMagic());
                        c.getPlayer().dropMessage(5, "經驗倍率: " + victim.getStat().expBuff + " 金錢倍率: " + victim.getStat().mesoBuff + " 掉寶倍率: " + victim.getStat().dropBuff);
                        c.getPlayer().dropMessage(5, "GASH: " + victim.getCSPoints(1) + " 楓葉點數: " + victim.getCSPoints(2) + " 楓幣: " + victim.getMeso() + " 倉庫楓幣 " + mesoInStorage);
                        if (ch <= 0 && CashShopServer.getPlayerStorage().getCharacterById(id) == null) {
                            c.getPlayer().dropMessage(5, "該角色為離線狀態");
                        } else {
                            if (ch <= 0) {
                                victim = CashShopServer.getPlayerStorage().getCharacterById(id);
                            }
                            c.getPlayer().dropMessage(5, "IP:" + victim.getClient().getSessionIPAddress() + " 目前MAC:" + victim.getNowMacs() + " 所有MAC:" + victim.getClient().getMacs());
                            c.getPlayer().dropMessage(5, "對伺服器延遲: " + victim.getClient().getLatency());
                        }
                    }
                } else {
                    c.getPlayer().dropMessage(5, "找不到此玩家.");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!").append(getClass().getSimpleName().toLowerCase()).append(" <玩家名字> - 觀察玩家").toString();
        }
    }

    public static class Map extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            try {
                MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                if (target == null) {
                    c.getPlayer().dropMessage(5, "地圖不存在.");
                    return true;
                }
                MaplePortal targetPortal = null;
                if (splitted.length > 2) {
                    try {
                        targetPortal = target.getPortal(Integer.parseInt(splitted[2]));
                    } catch (IndexOutOfBoundsException e) {
                        // noop, assume the gm didn't know how many portals there are
                        c.getPlayer().dropMessage(5, "傳送點錯誤.");
                    } catch (NumberFormatException a) {
                        // noop, assume that the gm is drunk
                    }
                }
                if (targetPortal == null) {
                    targetPortal = target.getPortal(0);
                }
                c.getPlayer().changeMap(target, targetPortal);
            } catch (Exception e) {
                c.getPlayer().dropMessage(5, "Error: " + e.getMessage());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!map <mapid|charname> [portal] - 傳送到某地圖/人").toString();
        }
    }

    public static class WarpMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            try {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                if (target == null) {
                    c.getPlayer().dropMessage(6, "地圖不存在。");
                    return false;
                }
                final MapleMap from = c.getPlayer().getMap();
                from.getCharactersThreadsafe().forEach((chr) -> {
                    chr.changeMap(target, target.getPortal(0));
                });
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!WarpMap 	地圖代碼 - 把地圖上的人全部傳到那張地圖").toString();
        }
    }

    public static class Debug extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().setDebugMessage(!c.getPlayer().getDebugMessage());
            c.getPlayer().dropMessage("DeBug訊息已經" + (c.getPlayer().getDebugMessage() ? "開啟" : "關閉"));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!Debug - 開啟Debug訊息").toString();
        }
    }

    public static class CharInfo extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {

            if (splitted.length < 2) {
                return false;
            }
            final StringBuilder builder = new StringBuilder();
            MapleCharacter other;
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "玩家必須上線");
                return true;
            }
            other = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);

            if (other == null) {
                builder.append("角色不存在");
                c.getPlayer().dropMessage(6, builder.toString());
            } else {
                if (other.getClient().getLastPing() <= 0) {
                    other.getClient().sendPing();
                }
                builder.append(MapleClient.getLogMessage(other, ""));
                builder.append(" 在 ").append(other.getPosition().x);
                builder.append(" /").append(other.getPosition().y);

                builder.append(" || 血量 : ");
                builder.append(other.getStat().getHp());
                builder.append(" /");
                builder.append(other.getStat().getCurrentMaxHp());

                builder.append(" || 魔量 : ");
                builder.append(other.getStat().getMp());
                builder.append(" /");
                builder.append(other.getStat().getCurrentMaxMp(other.getJob()));

                builder.append(" || 物理攻擊力 : ");
                builder.append(other.getStat().getTotalWatk());
                builder.append(" || 魔法攻擊力 : ");
                builder.append(other.getStat().getTotalMagic());
                builder.append(" || 最高攻擊 : ");
                builder.append(other.getStat().getCurrentMaxBaseDamage());
                builder.append(" || 攻擊%數 : ");
                builder.append(other.getStat().dam_r);
                builder.append(" || BOSS攻擊%數 : ");
                builder.append(other.getStat().bossdam_r);

                builder.append(" || 力量 : ");
                builder.append(other.getStat().getStr());
                builder.append(" || 敏捷 : ");
                builder.append(other.getStat().getDex());
                builder.append(" || 智力 : ");
                builder.append(other.getStat().getInt());
                builder.append(" || 幸運 : ");
                builder.append(other.getStat().getLuk());

                builder.append(" || 全部力量 : ");
                builder.append(other.getStat().getTotalStr());
                builder.append(" || 全部敏捷 : ");
                builder.append(other.getStat().getTotalDex());
                builder.append(" || 全部智力 : ");
                builder.append(other.getStat().getTotalInt());
                builder.append(" || 全部幸運 : ");
                builder.append(other.getStat().getTotalLuk());

                builder.append(" || 經驗值 : ");
                builder.append(other.getExp());

                builder.append(" || 組隊狀態 : ");
                builder.append(other.getParty() != null);

                builder.append(" || 交易狀態: ");
                builder.append(other.getTrade() != null);
                builder.append(" || Latency: ");
                builder.append(other.getClient().getLatency());
                builder.append(" || 最後PING: ");
                builder.append(other.getClient().getLastPing());
                builder.append(" || 最後PONG: ");
                builder.append(other.getClient().getLastPong());
                builder.append(" || IP: ");
                builder.append(other.getClient().getSessionIPAddress());
                other.getClient().DebugMessage(builder);

                c.getPlayer().dropMessage(6, builder.toString());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!charinfo <角色名稱> - 查看角色狀態").toString();

        }
    }

    public static class Cheaters extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            List<CheaterData> cheaters = World.getCheaters(c);
            for (int x = cheaters.size() - 1; x >= 0; x--) {
                CheaterData cheater = cheaters.get(x);
                c.getPlayer().dropMessage(6, cheater.getInfo());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!cheaters - 查看作弊角色").toString();

        }
    }

    public static class ItemCheck extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 3 || splitted[1] == null || splitted[1].equals("") || splitted[2] == null || splitted[2].equals("")) {
                return false;
            } else {
                int item = Integer.parseInt(splitted[2]);
                MapleCharacter chr;
                String name = splitted[1];
                int ch = World.Find.findChannel(name);
                if (ch <= 0) {
                    c.getPlayer().dropMessage(6, "玩家必須上線");
                    return true;
                }
                chr = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);

                int itemamount = chr.getItemQuantity(item, true);
                if (itemamount > 0) {
                    c.getPlayer().dropMessage(6, chr.getName() + " 有 " + itemamount + " (" + item + ").");
                } else {
                    c.getPlayer().dropMessage(6, chr.getName() + " 並沒有 (" + item + ")");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!itemcheck <playername> <itemid> - 檢查物品").toString();
        }
    }

    public static class CheckGash extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            if (splitted.length < 2) {
                return false;
            }
            MapleCharacter chrs;
            String name = splitted[1];
            int ch = World.Find.findChannel(name);
            if (ch <= 0) {
                c.getPlayer().dropMessage(6, "玩家必須上線");
                return true;
            }
            chrs = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(name);
            if (chrs == null) {
                c.getPlayer().dropMessage(5, "找不到該角色");
            } else {
                c.getPlayer().dropMessage(6, chrs.getName() + " 有 " + chrs.getCSPoints(1) + " 點數.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!checkgash <玩家名稱> - 檢查點數").toString();
        }
    }

    public static class whoishere extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            StringBuilder builder = new StringBuilder("在此地圖的玩家: ");
            for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (builder.length() > 150) { // wild guess :o
                    builder.setLength(builder.length() - 2);
                    c.getPlayer().dropMessage(6, builder.toString());
                    builder = new StringBuilder();
                }
                builder.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                builder.append(", ");
            }
            builder.setLength(builder.length() - 2);
            c.getPlayer().dropMessage(6, builder.toString());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!whoishere - 查看目前地圖上的玩家").toString();

        }
    }

    public static class Connected extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            java.util.Map<Integer, Integer> connected = World.getConnected();
            StringBuilder conStr = new StringBuilder("已連接的客戶端: ");
            boolean first = true;
            for (int i : connected.keySet()) {
                if (!first) {
                    conStr.append(", ");
                } else {
                    first = false;
                }
                if (i == 0) {
                    conStr.append("所有: ");
                    conStr.append(connected.get(i));
                } else {
                    conStr.append("頻道 ");
                    conStr.append(i);
                    conStr.append(": ");
                    conStr.append(connected.get(i));
                }
            }
            c.getPlayer().dropMessage(6, conStr.toString());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!connected - 查看已連線的客戶端").toString();
        }
    }

    public static class openmap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            int mapid = 0;
            String input = null;
            MapleMap map = null;
            if (splitted.length < 2) {
                return false;
            }
            try {
                input = splitted[1];
                mapid = Integer.parseInt(input);
            } catch (Exception ex) {
            }
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                cserv.getMapFactory().HealMap(mapid);
            }
            return true;

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!openmap - 開放地圖").toString();
        }
    }

    public static class closemap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            int mapid = 0;
            String input = null;
            MapleMap map = null;
            if (splitted.length < 2) {
                return false;
            }
            try {
                input = splitted[1];
                mapid = Integer.parseInt(input);
            } catch (Exception ex) {
            }
            if (c.getChannelServer().getMapFactory().getMap(mapid) == null) {
                c.getPlayer().dropMessage("地圖不存在");
                return true;
            }
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                cserv.getMapFactory().destroyMap(mapid, true);
            }
            return true;

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!closemap - 關閉地圖").toString();
        }
    }
}
