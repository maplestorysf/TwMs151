package console;

import client.LoginCrypto;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import constants.ServerConstants;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.login.handler.AutoRegister;
import handling.world.World;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import scripting.PortalScriptManager;
import scripting.ReactorScriptManager;
import server.CashItemFactory;
import server.MapleItemInformationProvider;
import server.MapleShopFactory;

import server.ShutdownServer;
import server.Timer.EventTimer;
import server.life.MapleMonsterInformationProvider;
import server.life.MobSkillFactory;
import tools.FileoutputUtil;
import tools.packet.CWvsContext;

public class consoleCommand {

    public static void autobans() {
        boolean yn = ServerConstants.getAB();
        if (yn) {
            yn = false;
            ServerConstants.setAB(false);
        } else {
            yn = true;
            ServerConstants.setAB(true);
        }
        System.out.println("系統自動封鎖已經 " + (yn ? "開啟" : "關閉") + ".");
    }

    public static void GiveDrop() {
        boolean x = ServerConstants.getDrop();
        if (x) {
            ServerConstants.setDrop(false);
        } else {
            ServerConstants.setDrop(true);
        }
        x = ServerConstants.getDrop();
        System.out.println("Drop給予狀態: " + (!x ? "關閉" : "開放")
        );
    }

    public static void ARnow() {
        boolean x = ServerConstants.getAR();
        if (x) {
            ServerConstants.setAR(false);
        } else {
            ServerConstants.setAR(true);
        }
        x = ServerConstants.getAR();
        System.out.println("自動註冊狀態為: " + (!x ? "關閉" : "開啟")
        );
    }

    public static void LHnow() {
        boolean x = ServerConstants.getLH();
        if (x) {
            ServerConstants.setLH(false);
        } else {
            ServerConstants.setLH(true);
        }
        x = ServerConstants.getLH();
        System.out.println("管理員測試狀態為: " + (!x ? "關閉" : "開啟")
        );
    }

    public static void CSnow() {
        boolean x = ServerConstants.getCS();
        if (x) {
            ServerConstants.setCS(false);
        } else {
            ServerConstants.setCS(true);
        }
        x = ServerConstants.getCS();
        System.out.println("購物商城狀態為: " + (x ? "關閉" : "開啟")
        );
    }

    public static void packet() {
        if (ServerConstants.getPacket()) {
            ServerConstants.setPacket(false);
        } else {
            ServerConstants.setPacket(true);
        }
        boolean x = ServerConstants.getPacket();
        System.out.println("封包狀態為: " + (!x ? "關閉" : "開啟"));
    }

    public static void Register(String acc, String password) {
        if (acc == null || password == null) {
            System.out.println("帳號或密碼輸入異常");
        }
        boolean ACCexist = AutoRegister.getAccountExists(acc);
        if (ACCexist) {
            System.out.println("該帳號已被使用");
            return;
        }
        if (acc.length() >= 12) {
            System.out.println("該帳號長度過長");
            return;
        }

        Connection con;
        try {
            con = (Connection) DatabaseConnection.getConnection();
        } catch (Exception ex) {
            System.out.println(ex);
            return;
        }

        try {
            try (PreparedStatement ps = (PreparedStatement) con.prepareStatement("INSERT INTO accounts (name, password) VALUES (?, ?)")) {
                ps.setString(1, acc);
                ps.setString(2, LoginCrypto.hexSha1(password));
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            return;
        }
        System.out.println("[註冊完成] 帳號: " + acc + " 密碼: " + password);

    }

    public static void chinfoID(int p) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterById(p);
        chinfo(victim.getName());
    }

    public static void chinfo(String p) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterByName(p);
        StringBuilder builder = new StringBuilder();
        System.out.println(MapleClient.getLogMessage(victim, "") + " 位於 (" + victim.getPosition().x + "，" + victim.getPosition().y + " + " + victim.getMapId() + ") 權限等級：" + victim.getGMLevel());
        System.out.println("HP：" + victim.getStat().getHp() + " / " + victim.getStat().getCurrentMaxHp() + "，MP：" + victim.getStat().getMp() + " / " + victim.getStat().getCurrentMaxMp(victim.getJob()) + "，職業：" + victim.getJob());
        System.out.println("物理攻擊力：" + victim.getStat().getTotalWatk() + "，魔法攻擊力：" + victim.getStat().getTotalMagic() + "，最大傷害：" + victim.getStat().getCurrentMaxBaseDamage());
        System.out.println("傷害率：" + victim.getStat().dam_r + "，BOSS 傷害率：" + victim.getStat().bossdam_r + "，爆擊機率：" + victim.getStat().passive_sharpeye_rate() + "，爆擊傷害加成：" + victim.getStat().passive_sharpeye_percent());
        System.out.println("力量：" + victim.getStat().getStr() + " + (" + (victim.getStat().getTotalStr() - victim.getStat().getStr()) + ")" + "，敏捷：" + victim.getStat().getDex() + " + (" + (victim.getStat().getTotalDex() - victim.getStat().getDex()) + ")");
        System.out.println("智力：" + victim.getStat().getInt() + " + (" + (victim.getStat().getTotalInt() - victim.getStat().getInt()) + ")" + "，幸運：" + victim.getStat().getLuk() + " + (" + (victim.getStat().getTotalLuk() - victim.getStat().getLuk()) + ")");
        System.out.println("經驗值：" + victim.getExp() + "，楓幣：" + victim.getMeso() + "，隊伍：" + (victim.getParty() == null ? -1 : victim.getParty().getId()) + "，交易狀態：" + (victim.getTrade() != null));
        victim.getClient().DebugMessage(builder);
        System.out.println(builder.toString());
        System.out.println("連線 IP：" + victim.getClient().getSessionIPAddress());
    }

    public static void ipID(int p) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterById(p);
        ip(victim.getName());
    }

    public static void ip(String p) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterByName(p);
        System.out.println("玩家 " + victim.getName() + " IP 位址為：" + victim.getClient().getSessionIPAddress());
    }

    public static void healID(int p) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter player = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterById(p);
        heal(player.getName());
    }

    public static void heal(String p) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterByName(p);
        victim.getStat().heal(victim);
        victim.dispelDebuffs();
        System.out.println("角色恢復完成!");
    }

    public static void QID(int p, int id, int mount) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter player = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterById(p);
        Q(player.getName(), id, mount);
    }

    public static void Q(String p, int id, int mount) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter player = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterByName(p);
        player.setCData(id, mount);
        System.out.println("任務點數給予完成!");
    }

    public static void ItemID(int p, int id, int amount) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter player = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterById(p);
        Item(player.getName(), id, amount);
    }

    public static void Item(String p, int id, int amount) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter player = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterByName(p);
        player.gainItem(id, (short) amount);
        System.out.println("道具給予完成!");
    }

    public static void nx(String p, int m) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterByName(p);
        victim.modifyCSPoints(1, m, true);
        System.out.println("點數調整完成!");
    }

    public static void nxID(int p, int m) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterById(p);
        nx(victim.getName(), m);

    }

    public static void moID(int p, int m) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterById(p);
        mo(victim.getName(), m);

    }

    public static void mo(String p, int m) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterByName(p);
        victim.gainMeso(m, true);
        System.out.println("楓幣調整完成!");
    }

    public static void mapID(int p, int m) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterById(p);
        map(victim.getName(), m);
    }

    public static void map(String p, int m) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterByName(p);
        victim.changeMap(m, 0);
        System.out.println("地圖變更完成!");
    }

    public static void jbID(int p, int j) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterById(p);
        jb(victim.getName(), j);
    }

    public static void jb(String p, int j) {

        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterByName(p);
        victim.changeJob((short) j);
        victim.setSubcategory(victim.getSubcategory());
        System.out.println("職業調整完成!");

    }

    public static void LvID(int p, int lv) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterById(p);
        Lv(victim.getName(), lv);
    }

    public static void Lv(String p, int lv) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterByName(p);
        victim.setLevel((short) lv);
        victim.updateSingleStat(MapleStat.LEVEL, lv);
        victim.setExp(0);
        victim.updateSingleStat(MapleStat.EXP, 0);
        if (victim.getExp() < 0) {
            victim.setExp(0);
        }
        System.out.println("等級調整完成!");
    }

    public static void ReloadPID(int p) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterById(p);
        ReloadP(victim.getName());
    }

    public static void ReloadP(String p) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterByName(p);
        victim.reloadChar();
        System.out.println(victim.getName() + " 已經重製");
    }

    public static void searchId(String p) {
        int ch = World.Find.findChannel(p);
        int world = World.Find.findWorld(p);
        if (ch <= 0) {
            System.out.println(p + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterByName(p);
        System.out.println("本角色編號為: " + victim.getId());
    }

    public static void cnamebyid(int id, String np) {
        int ch = World.Find.findChannel(id);
        int world = World.Find.findWorld(id);
        if (ch <= 0) {
            System.out.println(id + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterById(id);
        cname(victim.getName(), np);
    }

    public static void cname(String player, String nplayer) {
        int ch = World.Find.findChannel(player);
        int world = World.Find.findWorld(player);
        if (ch <= 0) {
            System.out.println(player + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterByName(player);
        if (nplayer.length() >= 12) {
            System.out.println("名稱太長囉");
            return;
        }
        victim.setName(nplayer);
        victim.saveToDB(false, true);
        victim.reloadChar();
        System.out.println(player + " 已經改名為 " + nplayer + " 了(2次更改請使用編號(本角色編號: " + victim.getId() + ")");

    }

    public static void gmpersonID(int players, byte power) {
        int ch = World.Find.findChannel(players);
        int world = World.Find.findWorld(players);
        if (ch <= 0) {
            System.out.println(players + " 並不在線上唷");
            return;
        }
        MapleCharacter player = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterById(players);
        gmperson(player.getName(), power);
    }

    public static void gmperson(String players, byte power) {
        int ch = World.Find.findChannel(players);
        int world = World.Find.findWorld(players);
        if (ch <= 0) {
            System.out.println(players + " 並不在線上唷");
            return;
        }
        MapleCharacter player = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterByName(players);
        player.setGM(power);
        player.saveToDB(false, true);
        if (power != 0) {
            player.dropMessage("您已經成為權限為 " + power + " 的GM唷");
        }
        System.out.println(player.getName() + " 已經成為權限為 " + power + " 的GM唷");

    }

    public static void online() {
        int add = 0;
        for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
//            int amount = ChannelServer.getInstance(i).getPlayerStorage().getAllCharacters().size();
//            add += amount;
//            System.out.println("位於頻道 " + i + " 的上線玩家" + " : " + amount);
//            System.out.println(ChannelServer.getInstance(i).getPlayerStorage().getOnlinePlayers(true));
        }
        System.out.println("位於伺服器的上線玩家數量:" + add);
    }
    //     return 1;

    public static void downon() {
        try {
            com.mysql.jdbc.Connection dcon = (com.mysql.jdbc.Connection) DatabaseConnection.getConnection();
            com.mysql.jdbc.PreparedStatement ps = (com.mysql.jdbc.PreparedStatement) dcon.prepareStatement("UPDATE accounts SET loggedin = 0");
            ps.executeUpdate();
            ps.close();
            System.out.println("卡號自救提示： 所有角色已經全部解卡！");
        } catch (SQLException ex) {
            System.out.println("資料庫解卡出現異常" + ex);
        }
    }

    public static void ServerMessageYGM(String serverMessage) {
        ChannelServer.getAllInstances().forEach((cserv) -> {
            cserv.getPlayerStorage().getAllCharacters().stream().filter((mch) -> (mch.isAdmin())).forEachOrdered((mch) -> {
                mch.dropMessage(-1, "[超級管理員]" + serverMessage);
            });
        });
    }

    public static void ServerMessageY(String serverMessage) {
        ChannelServer.getAllInstances().forEach((cserv) -> {
            cserv.getPlayerStorage().getAllCharacters().forEach((mch) -> {
                mch.dropMessage(-1, "[超級管理員]" + serverMessage);
            });
        });
    }

    public static void ServerMessageS(String serverMessage) {
        ChannelServer.getAllInstances().forEach((cserv) -> {
            cserv.setServerMessage("[超級管理員]" + serverMessage);
        });
    }

    public static void ServerMessage(String serverMessage) {
        ChannelServer.getAllInstances().stream().map((cserv) -> {
            cserv.setServerMessage(serverMessage);
            return cserv;
        }).forEachOrdered((cserv) -> {
            cserv.getPlayerStorage().getAllCharacters().forEach((mch) -> {
                mch.dropMessage(-1, "[超級管理員]" + serverMessage);
            });
        });
    }

    public static void Notice(String Notice) {
        ChannelServer.getAllInstances().forEach((cserv) -> {
            cserv.getPlayerStorage().getAllCharacters().forEach((mch) -> {
                mch.dropMessage(1, Notice);
            });
        });
    }

    public static void Tip(String Notice) {
        ChannelServer.getAllInstances().forEach((cserv) -> {
            cserv.broadcastMessage(CWvsContext.yellowChat("[超級管理員] " + Notice));
        });
    }

    public static void ReloadWz() {
        MapleItemInformationProvider.getInstance().runEtc();
        MapleMonsterInformationProvider.getInstance().load();
        MapleItemInformationProvider.getInstance().runItems();
        MobSkillFactory.getInstance();
        CashItemFactory.getInstance().initialize();
        MapleMonsterInformationProvider.getInstance().addExtra();
    }

    public static void SaveAll() {
        ChannelServer.getAllInstances().forEach((cserv) -> {
            cserv.getPlayerStorage().getAllCharacters().stream().map((chr) -> {
                chr.saveToDB(false, false);
                return chr;
            }).forEachOrdered((chr) -> {
                chr.getAndroid().saveToDb();
            });
        });
    }

    public static void reloadall() {
        ChannelServer.getAllInstances().forEach((instance) -> {
            instance.reloadEvents();
        });
        MapleShopFactory.getInstance().clear();
        PortalScriptManager.getInstance().clearScripts();
        MapleItemInformationProvider.getInstance().runEtc();
        MapleMonsterInformationProvider.getInstance().load();
        MapleItemInformationProvider.getInstance().runItems();
        MobSkillFactory.getInstance();
        CashItemFactory.getInstance().initialize();
        MapleMonsterInformationProvider.getInstance().addExtra();
        MapleMonsterInformationProvider.getInstance().clearDrops();
        ReactorScriptManager.getInstance().clearDrops();

    }

    public static void unBan(String input) {
        MapleClient.unban(input);
        MapleClient.unbanIPMacs(input);
        FileoutputUtil.log("Log/Ban/解除封鎖.txt", "\r\n [CCS]" + input);
        System.out.println("解除封鎖處理完成");

    }

    public static void BanID(int victims, String reason) {
        int ch = World.Find.findChannel(victims);
        int world = World.Find.findWorld(victims);
        if (ch <= 0) {
            System.out.println(victims + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterById(victims);
        Ban(victim.getName(), reason);
    }

    public static void Ban(String victims, String reason) {

        int ch = World.Find.findChannel(victims);
        int world = World.Find.findWorld(victims);
        if (ch <= 0) {
            System.out.println(victims + " 並不在線上唷");
            return;
        }
        MapleCharacter victim = ChannelServer.getInstance(world, ch).getPlayerStorage().getCharacterByName(victims);
        if (victim != null) {
            victim.ban(reason != null ? reason + " - (Server console ban)" : "Server console ban", false);
            victim.getClient().disconnect(true, true);
            System.out.println("成功封鎖 " + victim.getName() + ".");
            FileoutputUtil.log("Log/Ban/CCS封鎖.txt", "\r\n" + victim.getName() + " | " + reason);

        } else {
            System.out.println("封鎖" + victim.getName() + "失敗.");

        }

    }

    public static void ExpRate(int id, int rate) {
        LoginServer.getWorldStatic(id).setExpRate(rate);
        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "世界" + id + "的經驗倍率已被條成" + rate + "倍"));
    }

    public static void DropRate(int id, int rate) {
        LoginServer.getWorldStatic(id).setDropRate(rate);
        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "世界" + id + "的掉寶倍率已被條成" + rate + "倍"));
    }

    public static void MesoRate(int id, int rate) {
        LoginServer.getWorldStatic(id).setMesoRate(rate);
        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "世界" + id + "的金錢倍率已被條成" + rate + "倍"));
    }

    private static int left = 0;
    private static boolean Now = false;

    public static boolean getNow() {
        return Now;
    }

    public static void shutdownTime(int t) {
        if (t == 0) {
            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, "伺服器即將關機"));
            System.out.println("伺服器即將關機!");
            shutdownServer();
            Now = false;
            return;
        }
        ChannelServer.getAllInstances().stream().filter((cs) -> (t >= 0)).forEachOrdered((cs) -> {
            cs.setServerMessage("伺服器將於 " + t + " 分鐘後關機");
        });
        if (t >= 0) {
            World.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, "伺服器將於 " + t + " 分鐘後關機"));
        }
        left = t - 1;
        Now = true;

        EventTimer.getInstance().registerMin(new Runnable() {//循環廣播
            @Override
            public void run() {
                if (left >= 0) {
                    ChannelServer.getAllInstances().forEach((cs) -> {
                        cs.setServerMessage("伺服器將於 " + left + " 分鐘後關機");
                    });
                    World.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, "伺服器將於 " + left + " 分鐘後關機"));
                    System.out.println("伺服器將要在" + left + "分鐘後關機!");
                }
                left--;
            }
        }, 1, 1);

        EventTimer.getInstance().register(new Runnable() {//最終處理
            @Override
            public void run() {
                shutdownServer();
                Now = false;
            }
        }, 1000 * 60 * t, 1000 * 60 * t);
    }

    public static void shutdownServer() {
        ShutdownServer.getInstance().shutdown();
        ShutdownServer.getInstance().shutdown();
    }

}
